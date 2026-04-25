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

@Rule(key = "M23_150")
public class NonVoidFunctionMustReturnCheck extends CCheck {

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.FUNCTION_DEF);
    }

    @Override
    public void visitNode(AstNode functionDef) {
        AstNode declSpecs = functionDef.getFirstChild(CGrammar.DECLARATION_SPECIFIERS);
        if (declSpecs == null) {
            return;
        }

        if (isVoidFunction(declSpecs)) {
            return;
        }

        String functionName = getFunctionName(functionDef);

        AstNode functionBody = functionDef.getFirstChild(CGrammar.FUNCTION_BODY);
        if (functionBody == null) {
            return;
        }

        if (!hasReturnWithValue(functionBody)) {
            AstNode reportNode = getFunctionNameNode(functionDef);
            if (reportNode == null) {
                reportNode = functionDef;
            }
            addIssue(
                "Function \"" + functionName + "\" has non-void return type"
                + " but does not return a value on all paths.",
                reportNode
            );
        }
    }

    private boolean isVoidFunction(AstNode declSpecs) {
        List<AstNode> typeSpecifiers = declSpecs.getChildren(CGrammar.TYPE_SPECIFIER);
        if (typeSpecifiers.isEmpty()) {
            return false;
        }

        for (AstNode ts : typeSpecifiers) {
            String val = ts.getTokenValue();
            if ("void".equalsIgnoreCase(val)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasReturnWithValue(AstNode functionBody) {
        for (AstNode jumpStmt : functionBody.getDescendants(CGrammar.JUMP_STATEMENT)) {
            AstNode firstChild = jumpStmt.getFirstChild();
            if (firstChild == null || !firstChild.is(CKeyword.RETURN)) {
                continue;
            }

            if (jumpStmt.getFirstChild(CGrammar.EXPRESSION) != null) {
                return true;
            }
        }
        return false;
    }

    private String getFunctionName(AstNode functionDef) {
        AstNode nameNode = getFunctionNameNode(functionDef);
        if (nameNode != null) {
            return nameNode.getTokenValue();
        }
        return "<unknown>";
    }

    private AstNode getFunctionNameNode(AstNode functionDef) {
        AstNode declarator = functionDef.getFirstChild(CGrammar.DECLARATOR);
        if (declarator == null) {
            return null;
        }
        AstNode directDecl = declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
        if (directDecl == null) {
            return null;
        }
        return directDecl.getFirstChild(CGrammar.IDENTIFIER);
    }
}