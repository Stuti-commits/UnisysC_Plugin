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

@Rule(key = "M23_142")
public class NoReturnLocalAddressCheck extends CCheck {

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.JUMP_STATEMENT);
    }

    @Override
    public void visitNode(AstNode jumpStatement) {
        AstNode returnKeyword = jumpStatement.getFirstChild();
        if (returnKeyword == null || !"return".equals(returnKeyword.getTokenValue())) {
            return;
        }

        // Must have an expression after return
        AstNode expression = jumpStatement.getFirstChild(CGrammar.EXPRESSION);
        if (expression == null) {
            return;
        }

        // Find the enclosing FUNCTION_DEF — we need its local declarations
        AstNode functionDef = getEnclosingFunctionDef(jumpStatement);
        if (functionDef == null) {
            return;
        }

        if (returnsAddressOfLocal(expression, functionDef)) {
            addIssue(
                "A function shall not return a pointer or reference to a local variable"
                + " with automatic storage duration.",
                returnKeyword
            );
            return;
        }

        if (returnsLocalPointer(expression, functionDef)) {
            addIssue(
                "A function shall not return a pointer or reference to a local variable"
                + " with automatic storage duration.",
                returnKeyword
            );
        }
    }

    private boolean returnsAddressOfLocal(AstNode expression, AstNode functionDef) {
        for (AstNode unaryExpr : expression.getDescendants(CGrammar.UNARY_EXPR)) {
            AstNode unaryOp = unaryExpr.getFirstChild(CGrammar.UNARY_OPERATOR);
            if (unaryOp == null || !"&".equals(unaryOp.getTokenValue())) {
                continue;
            }

            AstNode operand = unaryOp.getNextSibling();
            if (operand == null) {
                continue;
            }

            String varName = extractIdentifierName(operand);
            if (varName == null) {
                continue;
            }

            if (isLocalVariable(functionDef, varName)) {
                return true;
            }
        }
        return false;
    }

    private boolean returnsLocalPointer(AstNode expression, AstNode functionDef) {
        String varName = extractIdentifierName(expression);
        if (varName == null) {
            return false;
        }

        return isLocalPointer(functionDef, varName);
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


    private AstNode getEnclosingFunctionDef(AstNode node) {
        AstNode current = node.getParent();
        while (current != null && !current.is(CGrammar.FUNCTION_DEF)) {
            current = current.getParent();
        }
        return current;
    }

    private boolean isLocalVariable(AstNode functionDef, String varName) {
        AstNode functionBody = functionDef.getFirstChild(CGrammar.FUNCTION_BODY);
        if (functionBody == null) {
            return false;
        }

        AstNode compoundStmt = functionBody.getFirstChild(CGrammar.COMPOUND_STATEMENT);
        if (compoundStmt == null) {
            return false;
        }

        AstNode declList = compoundStmt.getFirstChild(CGrammar.DECLARATION_LIST);
        if (declList == null) {
            return false;
        }

        for (AstNode decl : declList.getChildren(CGrammar.DECLARATION)) {
            if (hasStorageClassSpecifier(decl, "static")
                    || hasStorageClassSpecifier(decl, "extern")) {
                continue;
            }

            if (declarationDefinesName(decl, varName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLocalPointer(AstNode functionDef, String varName) {
        AstNode functionBody = functionDef.getFirstChild(CGrammar.FUNCTION_BODY);
        if (functionBody == null) {
            return false;
        }

        AstNode compoundStmt = functionBody.getFirstChild(CGrammar.COMPOUND_STATEMENT);
        if (compoundStmt == null) {
            return false;
        }

        AstNode declList = compoundStmt.getFirstChild(CGrammar.DECLARATION_LIST);
        if (declList == null) {
            return false;
        }

        for (AstNode decl : declList.getChildren(CGrammar.DECLARATION)) {
            if (hasStorageClassSpecifier(decl, "static")
                    || hasStorageClassSpecifier(decl, "extern")) {
                continue;
            }

            if (declarationDefinesPointerName(decl, varName)) {
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

    private boolean declarationDefinesPointerName(AstNode decl, String varName) {
        for (AstNode declarator : decl.getDescendants(CGrammar.DECLARATOR)) {
            if (!declarator.hasDirectChildren(CGrammar.POINTER)) {
                continue;
            }
            AstNode directDecl = declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
            if (directDecl != null && varName.equals(directDecl.getTokenValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasStorageClassSpecifier(AstNode decl, String keyword) {
        AstNode declSpecs = decl.getFirstChild(CGrammar.DECLARATION_SPECIFIERS);
        if (declSpecs == null) {
            return false;
        }
        for (AstNode scs : declSpecs.getChildren(CGrammar.STORAGE_CLASS_SPECIFIER)) {
            if (keyword.equalsIgnoreCase(scs.getTokenValue())) {
                return true;
            }
        }
        return false;
    }
}
