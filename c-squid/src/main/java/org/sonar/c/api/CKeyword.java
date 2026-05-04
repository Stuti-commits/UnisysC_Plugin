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

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

/**
 * Below is a list of ActionScript 3 syntactic keywords, that can be used as
 * identifiers, but that have special meaning in certain contexts:
 * each
 * get
 * set
 * namespace
 * include
 * dynamic
 * final
 * native
 * override
 * static
 */
public enum CKeyword implements TokenType {
  // "as" is not a keyword in ActionScript 2, so we treat it as syntactic keyword
  // to permit usage as identifier - see SONARPLUGINS-2072
  // AS("as"),

  ASM("asm"),
  AUTO("auto"),
  BREAK("break"),
  CASE("case"),
  CHAR("char"),
  CLASS("class"),
  CONST("const"),
  CONTINUE("continue"),
  DEFAULT("default"),
  DO("do"),
  DOUBLE("double"),
  ELSE("else"),
  ENUM("enum"),
  EXTERN("extern"),
  __FAR("__far"),
  FLOAT("float"),
  FOR("for"),
  FUNCTION("function"),
  GOTO("goto"),
  IF("if"),
  IN("in"),
  INLINE("inline"),
  INSTANCEOF("instanceof"),
  INT("int"),
  IS("is"),
  LONG("long"),
  __NEAR("__near"),
  NULL("null"),
  REGISTER("register"),
  RETURN("return"),
  SHORT("short"),
  SIGNED("signed"),
  SIZEOF("sizeof"),
  __STACK_NUMBER__("__stack_number__"),
  STATIC("static"),
  STRUCT("struct"),
  SWITCH("switch"),
  TYPEDEF("typedef"),
  UNION("union"),
  UNSIGNED("unsigned"),
  __USER_LOCK__("__user_lock__"),
  __USER_UNLOCK__("__user_unlock__"),
  VOID("void"),
  VOLATILE("volatile"),
  WHILE("while");

  private final String value;

  CKeyword(String value) {
    this.value = value;
  }

  @Override
  public String getName() {
    return name();
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public boolean hasToBeSkippedFromAst(AstNode node) {
    return false;
  }

  public static String[] keywordValues() {
    CKeyword[] keywordsEnum = CKeyword.values();
    String[] keywords = new String[keywordsEnum.length];
    for (int i = 0; i < keywords.length; i++) {
      keywords[i] = keywordsEnum[i].getValue();
    }
    return keywords;
  }

}