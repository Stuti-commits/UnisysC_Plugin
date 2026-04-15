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
package org.sonar.c.metrics;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import java.util.Arrays;
import java.util.List;

import org.sonar.c.CGrammar;
import org.sonar.c.CVisitor;
import org.sonar.c.CPunctuator;

public class ComplexityVisitor extends CVisitor {

  private int complexity = 0;

  @Override
  public List<AstNodeType> subscribedTo() {
    return Arrays.asList(
        CGrammar.FUNCTION_DEF,
        CGrammar.CONTROL_STATEMENT,
        CGrammar.ITERATION_STATEMENT,
        CGrammar.CASE_LABEL,
        CPunctuator.QUERY,
        CGrammar.LOGICAL_AND_EXPRESSION,
        CGrammar.LOGICAL_OR_EXPRESSION
    );
  }

  @Override
  public void visitNode(AstNode node) {
    // Every function def, branching statement, or logical operator adds 1.
    complexity++;
  }

  /**
   * Used for generic complexity of any node/tree (Used in complexity() test)
   */
  public static int complexity(AstNode root) {
    ComplexityVisitor visitor = new ComplexityVisitor();
    visitor.scanNode(root);
    return visitor.complexity;
  }

  /**
   * Specifically used for checking complexity within a function's scope.
   */
  public static int functionComplexity(AstNode functionDef) {
    ComplexityVisitor visitor = new ComplexityVisitor();
    visitor.scanNode(functionDef);
    return visitor.complexity;
  }
}