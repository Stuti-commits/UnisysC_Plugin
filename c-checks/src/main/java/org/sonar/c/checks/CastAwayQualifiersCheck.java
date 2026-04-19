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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.CKeyword;
import org.sonar.check.Rule;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

@Rule(key = "M23_090")
public class CastAwayQualifiersCheck extends CCheck {

  private final Map<String, Set<AstNodeType>> variableQualifiers = new HashMap<>();

  @Override
  public List<AstNodeType> subscribedTo() {
    return Arrays.asList(
        CGrammar.INIT_DECLARATOR,
        CGrammar.CAST_EXPRESSION);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(CGrammar.INIT_DECLARATOR)) {
      recordDeclaration(node);
    } else if (node.is(CGrammar.CAST_EXPRESSION)) {
      checkCast(node);
    }
  }

  private void recordDeclaration(AstNode initDeclarator) {
    AstNode declaration = initDeclarator.getParent().getParent();
    Set<AstNodeType> qualifiers = new HashSet<>();

    if (declaration.hasDescendant(CKeyword.CONST))
      qualifiers.add(CKeyword.CONST);
    if (declaration.hasDescendant(CKeyword.VOLATILE))
      qualifiers.add(CKeyword.VOLATILE);

    AstNode identifier = initDeclarator.getFirstDescendant(CGrammar.IDENTIFIER);
    if (identifier != null) {
      variableQualifiers.put(identifier.getTokenValue(), qualifiers);
    }
  }

  private void checkCast(AstNode castNode) {
    AstNode typeName = castNode.getFirstChild(CGrammar.TYPE_NAME);
    AstNode expression = castNode.getLastChild();

    if (typeName != null && expression != null) {
      String varName = expression.getTokenValue();
      Set<AstNodeType> originalQualifiers = variableQualifiers.getOrDefault(varName, Collections.emptySet());

      if (originalQualifiers.contains(CKeyword.CONST) && !typeName.hasDescendant(CKeyword.CONST)) {
        addIssue("A cast shall not remove the 'const' qualification from a pointer or reference.", castNode);
      }

      if (originalQualifiers.contains(CKeyword.VOLATILE) && !typeName.hasDescendant(CKeyword.VOLATILE)) {
        addIssue("A cast shall not remove the 'volatile' qualification from a pointer or reference.", castNode);
      }
    }
  }
}