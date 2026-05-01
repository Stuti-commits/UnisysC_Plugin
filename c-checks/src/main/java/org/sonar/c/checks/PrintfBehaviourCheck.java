/*
 * SonarQube Unisys C Plugin
 * Copyright (C) 2010-2025 SonarSource Sàrl
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Sonar Source-Available License Version 1, as published by SonarSource SA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the Sonar Source-Available License for more details.
 *
 * You should have received a copy of the Sonar Source-Available License
 * along with this program; if not, see https://sonarsource.com/license/ssal/
 */
package org.sonar.c.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Token;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.CPunctuator;
import org.sonar.check.Rule;

@Rule(key = "S2275")
public class PrintfBehaviourCheck extends CCheck {

    private static final Set<String> PRINTF_FUNCTIONS = new HashSet<>(
            Arrays.asList("printf", "fprintf", "sprintf", "vprintf", "vfprintf", "vsprintf"));

    private static final Set<Character> VALID_SPECIFIERS = new HashSet<>(
            Arrays.asList(
                    'd', 'i', 'o', 'u', 'x', 'X',
                    'f', 'F', 'e', 'E', 'g', 'G', 'a', 'A',
                    'c', 's', 'p', 'n', '%'));

    private static final Set<Character> NUMERIC_ONLY_FLAGS = new HashSet<>(Arrays.asList('+', ' ', '#'));
    private static final Set<Character> NON_NUMERIC_SPECIFIERS = new HashSet<>(
            Arrays.asList('p', 'c', 's', 'n', '%'));

    private static final Pattern FLOAT_LITERAL = Pattern.compile(
            "([0-9]*\\.[0-9]+|[0-9]+\\.)([eE][+-]?[0-9]+)?[fFlL]?|[0-9]+[eE][+-]?[0-9]+[fFlL]?");

    private static final Pattern INT_LITERAL = Pattern.compile("[0-9]+[uUlL]*");

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.POSTFIX_EXPRESSION);
    }

    @Override
    public void visitNode(AstNode node) {
        AstNode identifier = getFirstIdentifier(node);
        if (identifier == null) {
            return;
        }

        String functionName = identifier.getTokenValue();
        if (functionName == null || !PRINTF_FUNCTIONS.contains(functionName)) {
            return;
        }

        List<String> arguments = getArguments(node);
        if (arguments.isEmpty()) {
            return;
        }

        int formatIndex = functionName.startsWith("f") ? 1 : 0;
        if (arguments.size() <= formatIndex) {
            return;
        }

        String format = unquote(arguments.get(formatIndex));
        if (format == null) {
            return;
        }

        if (containsNullChar(format)) {
            addIssue("Printf format string must not contain a null character '\\0'.", node);
            return;
        }

        validateFormat(format, arguments.subList(formatIndex + 1, arguments.size()), node);
    }

    private void validateFormat(String format, List<String> dataArguments, AstNode issueNode) {
        boolean hasPositional = false;
        boolean hasNonPositional = false;
        int nextArgumentIndex = 0;
        int maxPositionalIndex = 0;

        for (int i = 0; i < format.length(); i++) {
            if (format.charAt(i) != '%') {
                continue;
            }

            if (i + 1 < format.length() && format.charAt(i + 1) == '%') {
                i++;
                continue;
            }

            ParsedSpecifier parsed = parseSpecifier(format, i);
            if (parsed == null) {
                addIssue("Printf format string contains an invalid conversion specifier.", issueNode);
                return;
            }

            i = parsed.endIndex;

            if (!VALID_SPECIFIERS.contains(parsed.specifier)) {
                addIssue("Printf format string contains an invalid conversion specifier.", issueNode);
                return;
            }

            for (char flag : parsed.flags.toCharArray()) {
                if (NUMERIC_ONLY_FLAGS.contains(flag) && NON_NUMERIC_SPECIFIERS.contains(parsed.specifier)) {
                    addIssue("This flag and conversion specifier combination has undefined behavior.", issueNode);
                    return;
                }
            }

            if (parsed.positionalIndex != null) {
                hasPositional = true;
                if (parsed.positionalIndex.intValue() == 0) {
                    addIssue("Positional arguments in printf format strings must start at 1.", issueNode);
                    return;
                }
                maxPositionalIndex = Math.max(maxPositionalIndex, parsed.positionalIndex.intValue());
            } else {
                hasNonPositional = true;
            }

            if (hasPositional && hasNonPositional) {
                addIssue("Printf format strings should not mix positional and non-positional argument references.",
                        issueNode);
                return;
            }

            if (parsed.specifier == '%') {
                continue;
            }

            if (hasPositional) {
                continue;
            }

            if (parsed.widthFromArgument) {
                if (nextArgumentIndex >= dataArguments.size()) {
                    addIssue("Printf format string refers to more arguments than are provided.", issueNode);
                    return;
                }
                if (isFloatLiteral(dataArguments.get(nextArgumentIndex))) {
                    addIssue("Field width specified by '*' should use an integer argument.", issueNode);
                    return;
                }
                nextArgumentIndex++;
            }

            if (parsed.precisionFromArgument) {
                if (nextArgumentIndex >= dataArguments.size()) {
                    addIssue("Printf format string refers to more arguments than are provided.", issueNode);
                    return;
                }
                nextArgumentIndex++;
            }

            if (nextArgumentIndex >= dataArguments.size()) {
                addIssue("Printf format string refers to more arguments than are provided.", issueNode);
                return;
            }

            String argument = dataArguments.get(nextArgumentIndex++);
            if ("diouxX".indexOf(parsed.specifier) >= 0 && isFloatLiteral(argument)) {
                addIssue("Printf format specifier expects an integer argument.", issueNode);
            } else if ("fFeEgGaA".indexOf(parsed.specifier) >= 0 && isIntLiteral(argument)) {
                addIssue("Printf format specifier expects a floating-point argument.", issueNode);
            }
        }

        if (hasPositional && maxPositionalIndex > dataArguments.size()) {
            addIssue("Printf format string refers to more arguments than are provided.", issueNode);
        }
    }

    private ParsedSpecifier parseSpecifier(String format, int percentIndex) {
        int cursor = percentIndex + 1;
        Integer positionalIndex = null;
        StringBuilder digits = new StringBuilder();

        while (cursor < format.length() && Character.isDigit(format.charAt(cursor))) {
            digits.append(format.charAt(cursor));
            cursor++;
        }

        if (digits.length() > 0 && cursor < format.length() && format.charAt(cursor) == '$') {
            positionalIndex = Integer.valueOf(digits.toString());
            cursor++;
        } else {
            cursor = percentIndex + 1;
        }

        StringBuilder flags = new StringBuilder();
        while (cursor < format.length() && isFlag(format.charAt(cursor))) {
            flags.append(format.charAt(cursor));
            cursor++;
        }

        boolean widthFromArgument = false;
        if (cursor < format.length() && format.charAt(cursor) == '*') {
            widthFromArgument = true;
            cursor++;
        } else {
            while (cursor < format.length() && Character.isDigit(format.charAt(cursor))) {
                cursor++;
            }
        }

        boolean precisionFromArgument = false;
        if (cursor < format.length() && format.charAt(cursor) == '.') {
            cursor++;
            if (cursor < format.length() && format.charAt(cursor) == '*') {
                precisionFromArgument = true;
                cursor++;
            } else {
                while (cursor < format.length() && Character.isDigit(format.charAt(cursor))) {
                    cursor++;
                }
            }
        }

        if (cursor + 1 < format.length()) {
            String twoChars = format.substring(cursor, cursor + 2);
            if ("hh".equals(twoChars) || "ll".equals(twoChars)) {
                cursor += 2;
            }
        }

        if (cursor < format.length() && "hlLjztq".indexOf(format.charAt(cursor)) >= 0) {
            cursor++;
        }

        if (cursor >= format.length()) {
            return null;
        }

        return new ParsedSpecifier(
                positionalIndex,
                flags.toString(),
                widthFromArgument,
                precisionFromArgument,
                format.charAt(cursor),
                cursor);
    }

    private AstNode getFirstIdentifier(AstNode node) {
        if (node.is(CGrammar.IDENTIFIER)) {
            return node;
        }
        for (AstNode child : node.getChildren()) {
            if (child.is(CPunctuator.LPARENTHESIS)) {
                break;
            }
            AstNode found = getFirstIdentifier(child);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private List<String> getArguments(AstNode callNode) {
        AstNode argumentExpressionList = findFirstDescendant(callNode, CGrammar.ARGUMENT_EXPRESSION_LIST);
        if (argumentExpressionList != null) {
            return toExpressionTexts(argumentExpressionList.getChildren(CGrammar.ASSIGNMENT_EXPRESSION));
        }

        AstNode arguments = findFirstDescendant(callNode, CGrammar.ARGUMENTS);
        if (arguments != null) {
            AstNode listExpression = arguments.getFirstChild(CGrammar.LIST_EXPRESSION);
            if (listExpression != null) {
                return toExpressionTexts(listExpression.getChildren(CGrammar.ASSIGNMENT_EXPRESSION));
            }
        }

        // Fallback: manual parsing from tokens if AST is simplified
        String callText = expressionText(callNode);
        int openParenIndex = callText.indexOf('(');
        int closeParenIndex = callText.lastIndexOf(')');
        if (openParenIndex < 0 || closeParenIndex <= openParenIndex) {
            return Collections.emptyList();
        }

        String argumentsText = callText.substring(openParenIndex + 1, closeParenIndex).trim();
        if (argumentsText.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> args = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        int nesting = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < argumentsText.length(); i++) {
            char character = argumentsText.charAt(i);

            if (inString) {
                current.append(character);
                if (escaped) {
                    escaped = false;
                } else if (character == '\\') {
                    escaped = true;
                } else if (character == '"') {
                    inString = false;
                }
                continue;
            }

            if (character == '"') {
                inString = true;
                current.append(character);
                continue;
            }

            if (character == '(') {
                nesting++;
                current.append(character);
                continue;
            }

            if (character == ')') {
                nesting--;
                current.append(character);
                continue;
            }

            if (character == ',' && nesting == 0) {
                args.add(current.toString().trim());
                current.setLength(0);
                continue;
            }

            current.append(character);
        }

        if (current.length() > 0) {
            args.add(current.toString().trim());
        }

        return args;
    }

    private AstNode findFirstDescendant(AstNode node, AstNodeType type) {
        if (node.is(type)) {
            return node;
        }
        for (AstNode child : node.getChildren()) {
            AstNode found = findFirstDescendant(child, type);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private List<String> toExpressionTexts(List<AstNode> argumentNodes) {
        if (argumentNodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> arguments = new java.util.ArrayList<>();
        for (AstNode argumentNode : argumentNodes) {
            arguments.add(expressionText(argumentNode));
        }
        return arguments;
    }

    private String expressionText(AstNode node) {
        StringBuilder builder = new StringBuilder();
        for (Token token : node.getTokens()) {
            builder.append(token.getValue());
        }
        return builder.toString();
    }

    private String unquote(String text) {
        if (text == null) {
            return null;
        }

        String trimmed = text.trim();
        if (!trimmed.startsWith("\"") || !trimmed.endsWith("\"")) {
            return null;
        }

        return trimmed.substring(1, trimmed.length() - 1);
    }

    private boolean containsNullChar(String format) {
        return format.contains("\\0") || format.indexOf('\0') >= 0;
    }

    private boolean isFloatLiteral(String text) {
        return text != null && FLOAT_LITERAL.matcher(text.trim()).matches();
    }

    private boolean isIntLiteral(String text) {
        return text != null && INT_LITERAL.matcher(text.trim()).matches();
    }

    private boolean isFlag(char character) {
        return "-+0 #'".indexOf(character) >= 0;
    }

    private static final class ParsedSpecifier {
        private final Integer positionalIndex;
        private final String flags;
        private final boolean widthFromArgument;
        private final boolean precisionFromArgument;
        private final char specifier;
        private final int endIndex;

        private ParsedSpecifier(
                Integer positionalIndex,
                String flags,
                boolean widthFromArgument,
                boolean precisionFromArgument,
                char specifier,
                int endIndex) {
            this.positionalIndex = positionalIndex;
            this.flags = flags;
            this.widthFromArgument = widthFromArgument;
            this.precisionFromArgument = precisionFromArgument;
            this.specifier = specifier;
            this.endIndex = endIndex;
        }
    }
}
