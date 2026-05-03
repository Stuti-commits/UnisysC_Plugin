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
package org.sonar.c.checks.utils;

import com.sonar.sslr.api.AstNode;
import java.util.ArrayList;
import java.util.List;
import org.sonar.c.CGrammar;

public class Function {
    private Function() {
    }

    public static String getName(AstNode functionDef) {
        Preconditions.checkState(functionDef.is(CGrammar.FUNCTION_DEF));
        AstNode directDeclarator = getDirectDeclarator(functionDef);
        if (directDeclarator == null) {
            return "";
        }
        AstNode identifier = directDeclarator.getFirstChild(CGrammar.IDENTIFIER);
        return identifier != null ? identifier.getTokenValue() : "";
    }

    public static boolean isEmptyConstructor(AstNode functionDef, String className) {
        return false;
    }

    public static boolean isConstructor(AstNode functionDef, String className) {
        return false;
    }

    public static List<AstNode> getParametersIdentifiers(AstNode functionDef) {
        Preconditions.checkState(functionDef.is(CGrammar.FUNCTION_DEF));
        List<AstNode> paramIdentifiers = new ArrayList<>();

        AstNode directDeclarator = getDirectDeclarator(functionDef);
        if (directDeclarator == null) {
            return paramIdentifiers;
        }

        AstNode paramTypeList = directDeclarator.getFirstChild(CGrammar.PARAMETER_TYPE_LIST);
        if (paramTypeList == null) {
            return paramIdentifiers; 
        }

        AstNode paramList = paramTypeList.getFirstChild(CGrammar.PARAMETER_LIST);
        if (paramList == null) {
            return paramIdentifiers;
        }

        for (AstNode paramDecl : paramList.getChildren(CGrammar.PARAMETER_DECLARATION)) {
            AstNode declarator = paramDecl.getFirstChild(CGrammar.DECLARATOR);
            if (declarator == null) {
                continue; 
            }

            AstNode directParamDeclarator = declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
            if (directParamDeclarator == null) {
                continue;
            }

            AstNode identifier = directParamDeclarator.getFirstChild(CGrammar.IDENTIFIER);
            if (identifier != null) {
                paramIdentifiers.add(identifier);
            }
        }

        return paramIdentifiers;
    }

    public static boolean isOverriding(AstNode functionDef) {
        return false;
    }


    private static AstNode getDirectDeclarator(AstNode functionDef) {
        AstNode declarator = functionDef.getFirstChild(CGrammar.DECLARATOR);
        if (declarator == null) {
            return null;
        }
        return declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
    }
}