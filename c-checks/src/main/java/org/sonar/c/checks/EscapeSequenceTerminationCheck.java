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
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

@Rule(key = "M23_320")
public class EscapeSequenceTerminationCheck extends CCheck {

    private static final String MESSAGE =
        "Escape sequence should be properly terminated.";

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.STRING_CONSTANT);
    }

    @Override
    public void visitNode(AstNode node) {

        for (AstNode seq : node.getDescendants(CGrammar.S_CHAR_SEQUENCE)) {

            List<AstNode> children = seq.getChildren();

            for (int i = 0; i < children.size(); i++) {

                AstNode current = children.get(i);

                if (current.hasDescendant(CGrammar.OCTAL_DIGIT)) {

                    if (i + 1 < children.size()) {
                        AstNode next = children.get(i + 1);

                        if (next.getTokenValue().matches("[0-9]")) {
                            addIssue(MESSAGE, current);
                            return;
                        }
                    }
                }

                if (current.hasDescendant(CGrammar.HEXADECIMAL_CODE)) {

                    if (i + 1 < children.size()) {
                        AstNode next = children.get(i + 1);

                        if (!next.hasDescendant(CGrammar.ESCAPE_SEQUENCE)) {
                            addIssue(MESSAGE, current);
                            return;
                        }
                    }
                }
            }
        }
    }  
}