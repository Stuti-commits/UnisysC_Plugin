/*
 * SonarQube Flex Plugin
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

import java.util.List;
import java.util.Collections;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.CKeyword;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

@Rule(key = "M23_112")
public class IfElseIfHasElseCheck extends CCheck {

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.CONTROL_STATEMENT);
    }

    @Override
    public void visitNode(AstNode controlStmt) {
        if (controlStmt.getFirstChild().is(CKeyword.IF)) {
            AstNode elseNode = controlStmt.getFirstChild(CKeyword.ELSE);
            if (elseNode != null) {
                AstNode elseStmt = elseNode.getNextSibling();
                if (elseStmt != null && isIfStmt(elseStmt)) {
                    checkNestedIf(elseStmt);
                }
            }
        }
    }

    private void checkNestedIf(AstNode node) {
        AstNode nestedIf = node.is(CGrammar.CONTROL_STATEMENT) ? node
                : node.getFirstDescendant(CGrammar.CONTROL_STATEMENT);

        if (nestedIf != null) {
            if (nestedIf.getFirstChild(CKeyword.ELSE) == null) {
                addIssue("All 'if ... else if' constructs shall be terminated with an 'else' statement.", nestedIf);
            }
        }
    }

    private boolean isIfStmt(AstNode node) {
        return node.hasDescendant(CKeyword.IF);
    }

}