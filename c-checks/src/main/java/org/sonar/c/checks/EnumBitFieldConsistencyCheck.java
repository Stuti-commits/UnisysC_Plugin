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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Token;

@Rule(key = "S5491")
public class EnumBitFieldConsistencyCheck extends CCheck {

    @Override
    public List<AstNodeType> subscribedTo() {
        return Collections.singletonList(CGrammar.PROGRAM);
    }

    @Override
    public void visitNode(AstNode program) {
        Map<String, Integer> enumRequiredBits = new HashMap<>();
        List<AstNode> enums = program.getDescendants(CGrammar.ENUM_SPECIFIER);
        
        for (AstNode enumNode : enums) {
            AstNode identifierNode = enumNode.getFirstChild(CGrammar.IDENTIFIER);
            AstNode enumList = enumNode.getFirstChild(CGrammar.ENUMERATION_LIST);

            if (identifierNode != null && enumList != null) {
                int maxValue = -1;
                List<AstNode> enumerators = enumList.getChildren(CGrammar.ENUMERATOR);

                for (AstNode enumerator : enumerators) {
                    AstNode constExpr = enumerator.getFirstChild(CGrammar.CONSTANT_EXPRESSION);
                    if (constExpr != null) {
                        try {
                            String cleanNumber = getFullText(constExpr).replaceAll("[uUlL]+$", "");
                            maxValue = Integer.decode(cleanNumber);
                        } catch (NumberFormatException e) {
                            maxValue++; 
                        }
                    } else {
                        maxValue++;
                    }
                }

                int requiredBits = maxValue <= 0 ? 1 : Integer.toBinaryString(maxValue).length();
                enumRequiredBits.put(identifierNode.getTokenValue(), requiredBits);
            }
        }

        List<AstNode> structDecls = program.getDescendants(CGrammar.STRUCT_DECLARATION);
        
        for (AstNode structDecl : structDecls) {
            AstNode typeSpecList = structDecl.getFirstChild(CGrammar.TYPE_SPECIFIER_LIST);
            if (typeSpecList == null) {
                continue;
            }

            String enumName = null;
            for (AstNode typeSpec : typeSpecList.getChildren(CGrammar.TYPE_SPECIFIER)) {
                AstNode enumSpec = typeSpec.getFirstChild(CGrammar.ENUM_SPECIFIER);
                if (enumSpec != null) {
                    AstNode idNode = enumSpec.getFirstChild(CGrammar.IDENTIFIER);
                    if (idNode != null) {
                        enumName = idNode.getTokenValue();
                    }
                }
            }

            if (enumName != null && enumRequiredBits.containsKey(enumName)) {
                int requiredBits = enumRequiredBits.get(enumName);
                AstNode declList = structDecl.getFirstChild(CGrammar.STRUCT_DECLARATOR_LIST);
                
                if (declList != null) {
                    for (AstNode declarator : declList.getChildren(CGrammar.STRUCT_DECLARATOR)) {
                        checkBitFieldSize(declarator, enumName, requiredBits);
                    }
                }
            }
        }
    }

    private void checkBitFieldSize(AstNode structDeclarator, String enumName, int requiredBits) {
        boolean isBitField = false;
        AstNode constExpr = null;

        for (AstNode child : structDeclarator.getChildren()) {
            if (":".equals(child.getTokenValue())) {
                isBitField = true;
            } else if (isBitField && child.is(CGrammar.CONSTANT_EXPRESSION)) {
                constExpr = child;
                break;
            }
        }

        if (isBitField && constExpr != null) {
            try {
                String cleanNumber = getFullText(constExpr).replaceAll("[uUlL]+$", "");
                int declaredSize = Integer.decode(cleanNumber);
                
                if (declaredSize > 0 && declaredSize < requiredBits) {
                    addIssue(
                        "Bit field size (" + declaredSize + ") is too small for enum \"" + enumName + 
                        "\" which requires at least " + requiredBits + " bits.", 
                        structDeclarator
                    );
                }
            } catch (NumberFormatException e) {
            }
        }
    }

    
    private String getFullText(AstNode node) {
        StringBuilder sb = new StringBuilder();
        for (Token token : node.getTokens()) {
            sb.append(token.getValue());
        }
        return sb.toString().trim();
    }
}