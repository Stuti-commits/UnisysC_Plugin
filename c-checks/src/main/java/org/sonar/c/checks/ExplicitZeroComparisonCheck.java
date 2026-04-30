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
import java.util.List;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.CKeyword;
import org.sonar.c.CPunctuator;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

@Rule(key = "S883")
public class ExplicitZeroComparisonCheck extends CCheck {

    @Override
    public List<AstNodeType> subscribedTo() {
        return Arrays.asList(
            CGrammar.CONTROL_STATEMENT,
            CGrammar.ITERATION_STATEMENT
        );
    }

    @Override
    public void visitNode(AstNode statement) {
        AstNode condition = getConditionExpression(statement);
        if (condition == null) {
            return;
        }

        if (!isExplicitComparison(condition)) {
            AstNode keyword = statement.getFirstChild();
            addIssue(
                "Tests of non-boolean values against zero should be explicit.",
                keyword
            );
        }
    }

    
    private AstNode getConditionExpression(AstNode statement) {
        AstNode firstChild = statement.getFirstChild();
        if (firstChild == null) {
            return null;
        }

        if (firstChild.is(CKeyword.SWITCH)) {
            return null;
        }

        if (firstChild.is(CKeyword.IF)
                || firstChild.is(CKeyword.WHILE)
                || firstChild.is(CKeyword.DO)) {
            return statement.getFirstChild(CGrammar.EXPRESSION);
        }

        if (firstChild.is(CKeyword.FOR)) {
            return getForConditionExpression(statement);
        }

        return null;
    }

    private AstNode getForConditionExpression(AstNode forStatement) {
        int semicolonCount = 0;
        for (AstNode child : forStatement.getChildren()) {
            if (child.is(CPunctuator.SEMICOLON)) {
                semicolonCount++;
                continue;
            }
            if (semicolonCount == 1 && child.is(CGrammar.EXPRESSION)) {
                return child;
            }
            if (semicolonCount >= 2) {
                break;
            }
        }
        return null;
    }

    private boolean isExplicitComparison(AstNode expression) {
        AstNode effective = unwrapSingleChild(expression);
        if (effective == null) {
            return false;
        }

        if (effective.is(CGrammar.EQUALITY_EXPRESSION)
                && hasComparisonOperator(effective)) {
            return true;
        }

        if (effective.is(CGrammar.RELATIONAL_EXPRESSION)
                && hasComparisonOperator(effective)) {
            return true;
        }

        if ((effective.is(CGrammar.LOGICAL_AND_EXPRESSION)
                || effective.is(CGrammar.LOGICAL_OR_EXPRESSION))
                && hasComparisonOperator(effective)) {
            return true;
        }

        return false;
    }

    private AstNode unwrapSingleChild(AstNode node) {
        if (node == null) {
            return null;
        }
        AstNode current = node;
        while (current.getChildren().size() == 1) {
            current = current.getChildren().get(0);
        }
        return current;
    }

    private boolean hasComparisonOperator(AstNode node) {
        for (AstNode child : node.getChildren()) {
            if (child.is(CGrammar.EQUALITY_OPERATOR)
                    || child.is(CGrammar.RELATIONAL_OPERATOR)
                    || child.is(CGrammar.LOGICAL_AND_OPERATOR)
                    || child.is(CGrammar.LOGICAL_OR_OPERATOR)) {
                return true;
            }
            String val = child.getTokenValue();
            if (val != null) {
                switch (val) {
                    case "==":
                    case "!=":
                    case "<":
                    case ">":
                    case "<=":
                    case ">=":
                    case "&&":
                    case "||":
                        return true;
                    default:
                        break;
                }
            }
        }
        return false;
    }
}