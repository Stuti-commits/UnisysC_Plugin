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
import java.util.Arrays;
import java.util.List;

import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.CKeyword;
import org.sonar.check.Rule;

@Rule(key = "S1005")
public class FunctionSinglePointOfExitCheck extends CCheck {

  private int returnStatements;

  @Override
  public List<AstNodeType> subscribedTo() {
    return Arrays.asList(CGrammar.FUNCTION_DEF, CGrammar.JUMP_STATEMENT);
  }

  @Override
  public void visitNode(AstNode node) {
    if (node.is(CGrammar.FUNCTION_DEF)) {
      returnStatements = 0;
    } else if (node.is(CGrammar.JUMP_STATEMENT)) {
      if (node.getFirstChild().is(CKeyword.RETURN)) {
        returnStatements++;
      }
    }
  }

  @Override
  public void leaveNode(AstNode node) {
    if (node.is(CGrammar.FUNCTION_DEF) && (returnStatements != 0) && (returnStatements > 1 || !hasReturnAtEnd(node))) {
      addIssue("A function shall have a single point of exit at the end of the function.", node);
    }
  }

  private static boolean hasReturnAtEnd(AstNode functionDefinitionNode) {
    AstNode functionBody = functionDefinitionNode.getFirstChild(CGrammar.FUNCTION_BODY);
    if (functionBody != null) {
      AstNode compoundStatement = functionBody.getFirstChild(CGrammar.COMPOUND_STATEMENT);
      if (compoundStatement != null) {
        AstNode statementList = compoundStatement.getFirstChild(CGrammar.STATEMENT_LIST);
        if (statementList != null) {
          AstNode lastStatement = statementList.getLastChild();
          if (lastStatement != null && lastStatement.getFirstChild().is(CGrammar.JUMP_STATEMENT)) {
            if (lastStatement.getFirstChild().getFirstChild().is(CKeyword.RETURN)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

}
