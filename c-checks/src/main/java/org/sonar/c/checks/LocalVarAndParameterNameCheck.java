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
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.checks.utils.Function;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = "S117")
public class LocalVarAndParameterNameCheck extends CCheck {


  private static final String DEFAULT = "^[_a-z][a-zA-Z0-9]*$";
  private static final String MESSAGE = "Rename this local variable \"{0}\" to match the regular expression {1}";
  private Pattern pattern = null;

  @RuleProperty(
    key = "format",
    description = "Regular expression used to check the names against.",
    defaultValue = DEFAULT)
  String format = DEFAULT;


  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.singletonList(CGrammar.FUNCTION_DEF);
  }

  @Override
  public void visitFile(@Nullable AstNode astNode) {
    if (pattern == null) {
      pattern = Pattern.compile(format);
    }
  }

  @Override
  public void visitNode(AstNode astNode) {
    checkFunctionParametersName(astNode);

    AstNode functionBody = astNode.getFirstChild(CGrammar.FUNCTION_BODY);
    if (functionBody != null) {
      AstNode compoundStatement = functionBody.getFirstChild(CGrammar.COMPOUND_STATEMENT);
      if (compoundStatement != null) {
        AstNode declarationList = compoundStatement.getFirstChild(CGrammar.DECLARATION_LIST);
        if (declarationList != null) {
          checkLocalVariableName(declarationList.getChildren(CGrammar.DECLARATION));
        }
      }
    }
  }

  private void checkLocalVariableName(List<AstNode> declarations) {
    for (AstNode declaration : declarations) {
      checkDeclaration(declaration);
    }
  }

  private void checkDeclaration(AstNode declaration) {
    AstNode initDeclaratorList = declaration.getFirstChild(CGrammar.INIT_DECLARATOR_LIST);
    if (initDeclaratorList == null) {
      return;
    }

    for (AstNode initDeclarator : initDeclaratorList.getChildren(CGrammar.INIT_DECLARATOR)) {
      AstNode declarator = initDeclarator.getFirstChild(CGrammar.DECLARATOR);
      if (declarator == null) {
        continue;
      }

      AstNode directDeclarator = declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
      if (directDeclarator == null) {
        continue;
      }

      AstNode identifier = directDeclarator.getFirstChild(CGrammar.IDENTIFIER);
      if (identifier == null) {
        continue;
      }

      String varName = identifier.getTokenValue();
      if (!pattern.matcher(varName).matches()) {
        addIssue(MessageFormat.format(MESSAGE, varName, format), identifier);
      }
    }
  }

  private void checkFunctionParametersName(AstNode functionDef) {
    for (AstNode paramIdentifier : Function.getParametersIdentifiers(functionDef)) {
      String paramName = paramIdentifier.getTokenValue();

      if (!pattern.matcher(paramName).matches()) {
        addIssue(MessageFormat.format(MESSAGE, paramName, format), paramIdentifier);
      }
    }
  }
}
