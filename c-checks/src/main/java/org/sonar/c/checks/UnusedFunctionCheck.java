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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.CKeyword;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

@Rule(key = "S1144")
public class UnusedFunctionCheck extends CCheck {
    
    private final Map<String, AstNode> functionDeclarations = new HashMap<>();
    private final Set<String> functionUsages = new HashSet<>();

    @Override
    public List<AstNodeType> subscribedTo() {
      return Arrays.asList(
          CGrammar.FUNCTION_DEF, 
          CGrammar.IDENTIFIER
      );
    }

    @Override
    public void visitFile(@Nullable AstNode astNode) {
      functionDeclarations.clear();
      functionUsages.clear();
    }

    @Override
    public void visitNode(AstNode astNode) {
        if (astNode.is(CGrammar.FUNCTION_DEF)) {
            if (isStatic(astNode)) {
                AstNode nameNode = findFunctionName(astNode);
                if (nameNode != null) {
                    functionDeclarations.put(nameNode.getTokenValue(), nameNode);
                }
            }
        } 
        else if (astNode.is(CGrammar.IDENTIFIER)) {
            if (!isWithinFunctionDefinition(astNode)) {
                functionUsages.add(astNode.getTokenValue());
            }
        }
    }

    private boolean isStatic(AstNode functionDef) {
        AstNode specifiers = functionDef.getFirstChild(CGrammar.DECLARATION_SPECIFIERS);
        if (specifiers != null) {
            return !specifiers.getDescendants(CKeyword.STATIC).isEmpty();
        }
        return false;
    }

    private AstNode findFunctionName(AstNode functionDef) {
        AstNode declarator = functionDef.getFirstChild(CGrammar.DECLARATOR);

        if (declarator != null) {
          AstNode directDeclarator = declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
        
            if (directDeclarator != null) {
              return directDeclarator.getFirstChild(CGrammar.IDENTIFIER);
            }
        }
        return null;
    }

    @Override
    public void leaveFile(AstNode astNode) {
      for (Map.Entry<String, AstNode> entry : functionDeclarations.entrySet()) {
        if (!functionUsages.contains(entry.getKey()) && !isMainFunction(entry.getKey())) {
          addIssue("Remove this unused function: " + entry.getKey(), entry.getValue());
        }
      }
    }

    private boolean isWithinFunctionDefinition(AstNode node) {
      return node.getParent().is(CGrammar.DIRECT_DECLARATOR);
    }
    
    private boolean isMainFunction(String name) {
      return "main".equals(name);
    }
}
