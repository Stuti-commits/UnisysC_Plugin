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
package org.sonar.c.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.sonar.sslr.grammar.GrammarRuleKey;

public enum CKeyword implements GrammarRuleKey {

  ASM,
  AUTO,
  BREAK,
  CASE,
  CHAR,
  CONST,
  CONTINUE,
  DEFAULT,
  DO,
  DOUBLE,
  ELSE,
  ENUM,
  EXTERN,
  __FAR, 
  FLOAT, 
  FOR,
  GOTO,
  IF,
  INCLUDE,
  INLINE,
  INT,
  LONG,
  __NEAR,
  REGISTER,
  RETURN,
  SHORT,  
  SIGNED,
  SIZEOF,
  __STACK_NUMBER__,
  STATIC,
  STRUCT,
  SWITCH,
  TYPEDEF,
  UNION,
  UNSIGNED,
  __USER__LOCK,
  __USER__UNLOCK,
  VOID,
  VOLATILE,
  WHILE,
  ;

  private final boolean syntactic;

  CKeyword() {
    this(false);
  }

  CKeyword(boolean syntactic) {
    this.syntactic = syntactic;
  }

  public static String[] keywordValues() {
    String[] keywordsValue = new String[CKeyword.values().length];
    int i = 0;
    for (CKeyword keyword : CKeyword.values()) {
      keywordsValue[i] = keyword.getValue();
      i++;
    }
    return keywordsValue;
  }

  public static List<CKeyword> keywords() {
    return Collections.unmodifiableList(Arrays.stream(values())
      .filter(cKeyword -> !cKeyword.syntactic)
      .collect(Collectors.toList()));
  }

  public String getValue() {
    return toString().toLowerCase(Locale.ENGLISH);
  }

}
