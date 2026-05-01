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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = "S100")
public class FunctionNameCheck extends CCheck {

  private static final String DEFAULT = "^[a-z]" + "[a-zA-Z0-9]*+";
  private Pattern pattern = null;

  @RuleProperty(key = "format", description = "Regular expression used to check the function names against", defaultValue = DEFAULT)
  String format = DEFAULT;

  @Override
  public List<AstNodeType> subscribedTo() {
    return Arrays.asList(CGrammar.FUNCTION_DEF);
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    pattern = Pattern.compile(format);
  }

  @Override
  public void visitNode(AstNode astNode) {
    handleFunction(astNode);
  }

  private void handleFunction(AstNode astNode) {
    AstNode identifier = findIdentifier(astNode);

    if (identifier != null) {
      String name = identifier.getTokenValue();

      if (pattern == null) {
        pattern = Pattern.compile(format);
      }

      if (!pattern.matcher(name).matches()) {
        addIssue(MessageFormat.format(
            "Rename this \"{0}\" function to match the regular expression {1}",
            name, format), identifier);
      }
    }
  }

  private AstNode findIdentifier(AstNode functionDef) {
    AstNode declarator = functionDef.getFirstChild(CGrammar.DECLARATOR);
    if (declarator != null) {
      AstNode directDeclarator = declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
      if (directDeclarator != null) {
        return directDeclarator.getFirstChild(CGrammar.IDENTIFIER);
      }
    }
    return null;
  }

}