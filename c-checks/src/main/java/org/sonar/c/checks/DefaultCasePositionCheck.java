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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.api.CKeyword;
import org.sonar.check.Rule;

@Rule(key = "S4524")
public class DefaultCasePositionCheck extends CCheck {

  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.singletonList(CGrammar.CONTROL_STATEMENT);
  }

  @Override
  public void visitNode(AstNode astNode) {
    if (!isSwitchStatement(astNode)) {
      return;
    }

    AstNode body = astNode.getFirstChild(CGrammar.STATEMENT);
    if (body == null) {
      return;
    }

    List<AstNode> labels = body.getDescendants(CGrammar.LABELED_STATEMENT).stream()
        .filter(node -> isCaseLabel(node) || isDefaultLabel(node))
        .collect(Collectors.toList());

    if (labels.size() <= 1) {
      return;
    }

    for (int i = 0; i < labels.size(); i++) {
      AstNode label = labels.get(i);
      if (isDefaultLabel(label)) {
        if (i > 0 && i < labels.size() - 1) {
          addIssue("Move this \"default\" clause to the beginning or end of this \"switch\" statement.", label);
        }
      }
    }
  }

  private boolean isSwitchStatement(AstNode node) {
    return node.getToken() != null && "switch".equals(node.getToken().getValue());
  }

  private boolean isCaseLabel(AstNode node) {
    return node.hasDirectChildren(CKeyword.CASE) || 
           (node.getToken() != null && "case".equals(node.getToken().getValue()));
  }

  private boolean isDefaultLabel(AstNode node) {
    return node.hasDirectChildren(CKeyword.DEFAULT) || 
           (node.getToken() != null && "default".equals(node.getToken().getValue()));
  }

}
