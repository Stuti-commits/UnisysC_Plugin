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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Token;

@Rule(key = "M23_036")
public class UnsignedLiteralSuffixCheck extends CCheck {

    private final Set<String> unsignedVariables = new HashSet<>();

    @Override
    public List<AstNodeType> subscribedTo() {
        return Arrays.asList(CGrammar.DECLARATION, CGrammar.ASSIGNMENT_EXPRESSION);
    }

    @Override
    public void visitNode(AstNode node) {
        if (node.is(CGrammar.DECLARATION)) {
            handleDeclaration(node);
        } else if (node.is(CGrammar.ASSIGNMENT_EXPRESSION)) {
            handleAssignment(node);
        }
    }

    private void handleDeclaration(AstNode node) {
        if (isUnsigned(node)) {
            AstNode list = node.getFirstChild(CGrammar.INIT_DECLARATOR_LIST);
            if (list != null) {
                for (AstNode initDeclarator : list.getChildren(CGrammar.INIT_DECLARATOR)) {
                    AstNode declarator = initDeclarator.getFirstChild(CGrammar.DECLARATOR);
                    if (declarator != null) {
                        unsignedVariables.add(declarator.getTokenValue());
                    }
                    checkLiteralsInNode(initDeclarator);
                }
            }
        }
    }

    private void handleAssignment(AstNode node) {
        AstNode leftSide = node.getFirstChild();
        if (unsignedVariables.contains(leftSide.getTokenValue())) {
            checkLiteralsInNode(node);
        }
    }

    private void checkLiteralsInNode(AstNode node) {
        if (node.is(CGrammar.I_CONSTANT)) {
            if (!node.hasDescendant(CGrammar.UNSIGNED_SUFFIX)) {
                addIssue("Unsigned 'integer literals' shall be appropriately suffixed", node);
            }
        } else {
            for (AstNode child : node.getChildren()) {
                checkLiteralsInNode(child);
            }
        }
    }

    private boolean isUnsigned(AstNode node) {
        AstNode specifiers = node.getFirstChild(CGrammar.DECLARATION_SPECIFIERS);
        if (specifiers != null) {
            for (Token t : specifiers.getTokens()) {
                if ("unsigned".equals(t.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }
}