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
package org.sonar.c.grammar.expressions;

import org.junit.jupiter.api.Test;
import org.sonar.c.CGrammar;
import org.sonar.sslr.parser.LexerlessGrammar;
import org.sonar.sslr.tests.Assertions;

public class BinaryExpressionTest {

  private final LexerlessGrammar g = CGrammar.createGrammar();

  @Test
  public void multiplicative() {
    Assertions.assertThat(g.rule(CGrammar.MULTIPLICATIVE_EXPR))
      .matches("a * b")
      .matches("a / b")
      .matches("a % b");
  }

  @Test
  public void additive() {
    Assertions.assertThat(g.rule(CGrammar.ADDITIVE_EXPR))
      .matches("a + b")
      .matches("a - b");
  }

  @Test
  public void shift() {
    Assertions.assertThat(g.rule(CGrammar.SHIFT_EXPR))
      .matches("a << b")
      .matches("a >> b");
  }

  @Test
  public void relational() {
    Assertions.assertThat(g.rule(CGrammar.RELATIONAL_EXPR))
      .matches("a <= b")
      .matches("a >= b")
      .matches("a < b")
      .matches("a > b");
  }

  @Test
  public void equality() {
    Assertions.assertThat(g.rule(CGrammar.EQUALITY_EXPR))
      .matches("a == b")
      .matches("a != b");
  }

  @Test
  public void bitewise() {
    Assertions.assertThat(g.rule(CGrammar.BITEWISE_AND_EXPR))
      .matches("a & b");

    Assertions.assertThat(g.rule(CGrammar.BITEWISE_XOR_EXPR))
      .matches("a ^ b");

    Assertions.assertThat(g.rule(CGrammar.BITEWISE_OR_EXPR))
      .matches("a | b");
  }

  @Test
  public void logical() {
    Assertions.assertThat(g.rule(CGrammar.LOGICAL_AND_EXPR))
      .matches("a && b");
 
    Assertions.assertThat(g.rule(CGrammar.LOGICAL_OR_EXPR))
      .matches("a || b");
  }

}
