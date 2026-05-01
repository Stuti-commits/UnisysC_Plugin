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
import com.sonar.sslr.api.Token;

@Rule(key = "S3646")
public class TypeAndVarSeparateDeclarationCheck extends CCheck {

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.DECLARATION);
    }

    @Override
    public void visitNode(AstNode declaration) {
        AstNode initDeclaratorList = declaration.getFirstChild(CGrammar.INIT_DECLARATOR_LIST);
        if (initDeclaratorList == null) {
            return;
        }

        AstNode declSpecifiers = declaration.getFirstChild(CGrammar.DECLARATION_SPECIFIERS);
        if (declSpecifiers == null) {
            return;
        }

        for (AstNode storageClass : declSpecifiers.getChildren(CGrammar.STORAGE_CLASS_SPECIFIER)) {
            if ("typedef".equals(storageClass.getTokenValue())) {
                return;
            }
        }

        for (AstNode typeSpecifier : declSpecifiers.getChildren(CGrammar.TYPE_SPECIFIER)) {
            AstNode structOrUnion = typeSpecifier.getFirstChild(CGrammar.STRUCT_OR_UNION_SPECIFIER);
            AstNode enumSpec = typeSpecifier.getFirstChild(CGrammar.ENUM_SPECIFIER);

            AstNode definitionNode = structOrUnion != null ? structOrUnion : enumSpec;

            if (definitionNode != null) {
                if (isInlineTypeDefinition(definitionNode)) {
                    addIssue("Types and variables should be declared in separate statements.", initDeclaratorList);
                    return; 
                }
            }
        }
    }

    private boolean isInlineTypeDefinition(AstNode node) {
        for (Token token : node.getTokens()) {
            if ("{".equals(token.getValue())) {
                return true;
            }
        }
        return false;
    }
}