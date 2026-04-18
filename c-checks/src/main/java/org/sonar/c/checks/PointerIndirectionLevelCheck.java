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

import java.util.Collections;
import java.util.List;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.CPunctuator;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

@Rule(key = "M23_083")
public class PointerIndirectionLevelCheck extends CCheck {

    private static final int MAX_POINTER_LEVELS = 2;

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.POINTER);
    }

    @Override
    public void visitNode(AstNode pointerNode) {
        AstNode parent = pointerNode.getParent();
        if (parent == null || !parent.is(CGrammar.DECLARATOR)) {
            return;
        }

        int starCount = pointerNode.getChildren(CPunctuator.STAR).size();
        if (starCount > MAX_POINTER_LEVELS) {
            addIssue(
                    String.format(
                            "Pointer '%s' has %d levels of indirection; maximum allowed is %d.",
                            getDeclaratorName(pointerNode),
                            starCount,
                            MAX_POINTER_LEVELS),
                    pointerNode);
        }
    }

    private String getDeclaratorName(AstNode pointerNode) {
        AstNode declarator = pointerNode.getParent();
        if (declarator != null) {
            AstNode directDeclarator = declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
            if (directDeclarator != null) {
                AstNode identifier = directDeclarator.getFirstChild(CGrammar.IDENTIFIER);
                if (identifier != null) {
                    return identifier.getTokenValue();
                }
            }
        }
        return "<unknown>";
    }
}
