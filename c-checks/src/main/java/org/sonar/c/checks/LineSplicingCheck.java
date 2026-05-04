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
import java.util.Collections;
import java.util.List;

import org.sonar.c.CCheck;
import org.sonar.check.Rule;

@Rule(key = "S2323")
public class LineSplicingCheck extends CCheck {

    private static final String MESSAGE = "Line-splicing should not be used in \"//\" comments";

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.emptyList();
    }

    @Override
    public void visitFile(AstNode node) {
        String content = getContext().fileContent();
        if (content != null) {
            scanSource(content);
        }
    }

    private void scanSource(String source) {
        int index = 0;
        int line = 1;

        while (index < source.length()) {
            char current = source.charAt(index);
            if (current == '\r') {
                if (index + 1 < source.length() && source.charAt(index + 1) == '\n') {
                    index++;
                }
                line++;
                index++;
                continue;
            }

            if (current == '\n') {
                line++;
                index++;
                continue;
            }

            if (startsBlockComment(source, index)) {
                index += 2;
                while (index < source.length()) {
                    char blockChar = source.charAt(index);
                    if (blockChar == '\r') {
                        if (index + 1 < source.length() && source.charAt(index + 1) == '\n') {
                            index++;
                        }
                        line++;
                        index++;
                        continue;
                    }
                    if (blockChar == '\n') {
                        line++;
                        index++;
                        continue;
                    }
                    if (blockChar == '*' && index + 1 < source.length()
                            && source.charAt(index + 1) == '/') {
                        index += 2;
                        break;
                    }
                    index++;
                }
                continue;
            }

            if (startsStringLiteral(source, index)) {
                index = skipStringLiteral(source, index);
                continue;
            }
            if (startsCharLiteral(source, index)) {
                index = skipCharLiteral(source, index);
                continue;
            }

            if (startsLineComment(source, index)) {
                index += 2;

                while (index < source.length()) {
                    char commentChar = source.charAt(index);

                    if (commentChar == '\\') {
                        int backslashCount = 0;
                        int lookahead = index;

                        while (lookahead < source.length()
                                && source.charAt(lookahead) == '\\') {
                            backslashCount++;
                            lookahead++;
                        }
                        while (lookahead < source.length()
                                && (source.charAt(lookahead) == ' '
                                        || source.charAt(lookahead) == '\t')) {
                            lookahead++;
                        }

                        boolean isAtLineEnd = lookahead < source.length()
                                && (source.charAt(lookahead) == '\n'
                                        || source.charAt(lookahead) == '\r');

                        if (isAtLineEnd && (backslashCount % 2 != 0)) {
                            addIssueAtLine(MESSAGE, line);
                            index = lookahead;
                            if (source.charAt(index) == '\r') {
                                if (index + 1 < source.length()
                                        && source.charAt(index + 1) == '\n') {
                                    index++;
                                }
                            }
                            line++;
                            index++;
                            continue;
                        }

                        index = lookahead;
                        continue;
                    }
                    if (commentChar == '\r') {
                        if (index + 1 < source.length() && source.charAt(index + 1) == '\n') {
                            index++;
                        }
                        line++;
                        index++;
                        break;
                    }

                    if (commentChar == '\n') {
                        line++;
                        index++;
                        break;
                    }

                    index++;
                }
                continue;
            }
            index++;
        }
    }

    private static boolean startsLineComment(String source, int index) {
        return index + 1 < source.length()
                && source.charAt(index) == '/'
                && source.charAt(index + 1) == '/';
    }

    private static boolean startsBlockComment(String source, int index) {
        return index + 1 < source.length()
                && source.charAt(index) == '/'
                && source.charAt(index + 1) == '*';
    }

    private static boolean startsStringLiteral(String source, int index) {
        if (index < source.length() && source.charAt(index) == '"') {
            return true;
        }
        return index + 1 < source.length()
                && source.charAt(index) == 'L'
                && source.charAt(index + 1) == '"';
    }

    private static boolean startsCharLiteral(String source, int index) {
        if (index < source.length() && source.charAt(index) == '\'') {
            return true;
        }
        return index + 1 < source.length()
                && source.charAt(index) == 'L'
                && source.charAt(index + 1) == '\'';
    }

    private static int skipStringLiteral(String source, int index) {
        if (source.charAt(index) == 'L') {
            index++;
        }
        index++;
        while (index < source.length()) {
            char c = source.charAt(index);
            if (c == '\\') {
                index += 2;
                continue;
            }
            if (c == '"' || c == '\r' || c == '\n') {
                index++;
                break;
            }
            index++;
        }
        return index;
    }

    private static int skipCharLiteral(String source, int index) {

        if (source.charAt(index) == 'L') {
            index++;
        }
        index++;
        while (index < source.length()) {
            char c = source.charAt(index);
            if (c == '\\') {
                index += 2;
                continue;
            }
            if (c == '\'' || c == '\r' || c == '\n') {
                index++;
                break;
            }
            index++;
        }
        return index;
    }
}