/*
 * SonarQube Flex Plugin
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
package org.sonar.c.grammar.definitions;

import org.junit.jupiter.api.Test;
import org.sonar.c.CGrammar;
import org.sonar.sslr.parser.LexerlessGrammar;
import org.sonar.sslr.tests.Assertions;

public class GlobalDefinitionTest {

  private final LexerlessGrammar g = CGrammar.createGrammar();

  @Test
  public void variableDefinition() {
    Assertions.assertThat(g.rule(CGrammar.DECLARATION))
        .matches("int x;")
        .matches("int x = 1;");
  }

  @Test
  public void functionDefinition() {
    Assertions.assertThat(g.rule(CGrammar.FUNCTION_DEF))
        .matches("int main() { return 0; }")
        .matches("void foo() {}");
  }

  @Test
  public void declaration() {
    Assertions.assertThat(g.rule(CGrammar.DECLARATION))
        .matches("int x;")
        .matches("int x, y;");
  }

  @Test
  public void regexp() {
    Assertions.assertThat(g.rule(CGrammar.REGULAR_EXPRESSION))
        .matches("/test-\\d/i")
        .matches("/<p>.*?<\\/p>/s")
        .matches("/\\d{3}-\\d{3}-\\d{4}|\\(\\d{3}\\)\\s?\\d{3}-\\d{4}/")
        .matches("/([0-9a-zA-Z]+[-._+&])*[0-9a-zA-Z]+@([-0-9a-zA-Z]+[.])+[a-zA-Z]{2,6}/")
        .notMatches("/<p>.*?<\\/p>s");
  }
}
