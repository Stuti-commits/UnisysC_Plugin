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

import java.util.Collections;
import java.util.List;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.CKeyword;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

@Rule(key = "S916")
public class NoNestedSwitchLabelCheck extends CCheck {

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.LABELED_STATEMENT);
    }

    @Override
    public void visitNode(AstNode labeledStatement) {
        AstNode firstChild = labeledStatement.getFirstChild();
        if (firstChild == null) {
            return;
        }

        if (!firstChild.is(CKeyword.CASE) && !firstChild.is(CKeyword.DEFAULT)) {
            return;
        }

        AstNode enclosingStatement = findEnclosingControlOrIteration(labeledStatement);

        if (enclosingStatement == null) {
            return;
        }

        if (!isSwitch(enclosingStatement)) {
            addIssue(
                "Switch labels shall not be nested inside non-switch blocks.",
                firstChild
            );
        }
    }

    private AstNode findEnclosingControlOrIteration(AstNode node) {
        AstNode current = node.getParent();
        while (current != null) {
            if (current.is(CGrammar.CONTROL_STATEMENT)
                    || current.is(CGrammar.ITERATION_STATEMENT)) {
                return current;
            }
            if (current.is(CGrammar.FUNCTION_DEF)
                    || current.is(CGrammar.PROGRAM)) {
                return null;
            }
            current = current.getParent();
        }
        return null;
    }

    private boolean isSwitch(AstNode statement) {
        if (!statement.is(CGrammar.CONTROL_STATEMENT)) {
            return false;
        }
        AstNode firstChild = statement.getFirstChild();
        return firstChild != null && firstChild.is(CKeyword.SWITCH);
    }
}