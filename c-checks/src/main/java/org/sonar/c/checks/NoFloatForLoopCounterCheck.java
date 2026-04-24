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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.CKeyword;
import org.sonar.c.CPunctuator;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

@Rule(key = "S2193")
public class NoFloatForLoopCounterCheck extends CCheck {

    private static final Set<String> FLOAT_TYPES = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList(
            "float",
            "double"
        ))
    );

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.ITERATION_STATEMENT);
    }

    @Override
    public void visitNode(AstNode iterationStatement) {
        AstNode forKeyword = iterationStatement.getFirstChild(CKeyword.FOR);
        if (forKeyword == null) {
            return;
        }

        AstNode initExpression = getForInitExpression(iterationStatement);
        if (initExpression == null) {
            return;
        }

        String counterName = extractCounterName(initExpression);
        if (counterName == null) {
            return;
        }

        if (isFloatDeclared(iterationStatement, counterName)) {
            addIssue(
                "\"for\" loop counters shall not have essentially floating type.",
                forKeyword
            );
        }
    }
    private AstNode getForInitExpression(AstNode iterationStatement) {
        boolean seenFor = false;
        boolean seenLParen = false;
        for (AstNode child : iterationStatement.getChildren()) {
            if (child.is(CKeyword.FOR)) {
                seenFor = true;
                continue;
            }
            if (seenFor && child.is(CPunctuator.LPARENTHESIS)) {
                seenLParen = true;
                continue;
            }
            if (seenLParen && child.is(CPunctuator.SEMICOLON)) {
                return null;
            }
            if (seenLParen && child.is(CGrammar.EXPRESSION)) {
                return child;
            }
        }
        return null;
    }

    private String extractCounterName(AstNode expression) {
        AstNode assignExpr = findFirstAssignmentExpression(expression);
        if (assignExpr == null) {
            return null;
        }

        AstNode assignOp = assignExpr.getFirstChild(CGrammar.ASSIGNMENT_OPERATOR);
        if (assignOp == null) {
            return null;
        }

        AstNode leftSide = assignExpr.getFirstChild();
        if (leftSide == null) {
            return null;
        }

        return extractIdentifierName(leftSide);
    }

    private AstNode findFirstAssignmentExpression(AstNode node) {
        if (node.is(CGrammar.ASSIGNMENT_EXPRESSION)
                && node.getFirstChild(CGrammar.ASSIGNMENT_OPERATOR) != null) {
            return node;
        }
        for (AstNode child : node.getChildren()) {
            AstNode found = findFirstAssignmentExpression(child);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private String extractIdentifierName(AstNode node) {
        if (node.is(CGrammar.IDENTIFIER)) {
            return node.getTokenValue();
        }
        for (AstNode child : node.getChildren()) {
            String name = extractIdentifierName(child);
            if (name != null) {
                return name;
            }
        }
        return null;
    }

    private boolean isFloatDeclared(AstNode startNode, String varName) {
        AstNode scopeRoot = startNode.getParent();
        while (scopeRoot != null
                && !scopeRoot.is(CGrammar.FUNCTION_DEF)
                && !scopeRoot.is(CGrammar.PROGRAM)) {
            scopeRoot = scopeRoot.getParent();
        }
        if (scopeRoot == null) {
            return false;
        }

        for (AstNode decl : scopeRoot.getDescendants(CGrammar.DECLARATION)) {
            if (declarationDefinesName(decl, varName)
                    && declarationHasFloatType(decl)) {
                return true;
            }
        }
        return false;
    }

    private boolean declarationDefinesName(AstNode decl, String varName) {
        for (AstNode dd : decl.getDescendants(CGrammar.DIRECT_DECLARATOR)) {
            if (varName.equals(dd.getTokenValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean declarationHasFloatType(AstNode decl) {
        for (AstNode ts : decl.getDescendants(CGrammar.TYPE_SPECIFIER)) {
            String val = ts.getTokenValue();
            if (val != null && FLOAT_TYPES.contains(val.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}