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

@Rule(key = "S2681")
public class MultiLineBlockCheck extends CCheck {

  private static final String MESSAGE = "Enclose this multiline block in curly braces.";

  private String[] lines = new String[0];

  @Override
  public List<AstNodeType> subscribedTo() {
    return Arrays.asList(CGrammar.CONTROL_STATEMENT, CGrammar.ITERATION_STATEMENT);
  }

  @Override
  public void visitFile(AstNode node) {
    String fileContent = getContext().fileContent();
    lines = fileContent == null ? new String[0] : fileContent.split("\\r?\\n", -1);
  }

  @Override
  public void visitNode(AstNode statement) {
    AstNode firstChild = statement.getFirstChild();
    if (firstChild == null) {
      return;
    }

    if (firstChild.is(CKeyword.IF)) {
      checkIfStatement(statement, firstChild);
      return;
    }

    if (firstChild.is(CKeyword.FOR)) {
      checkLoopBody(statement, firstChild, getForBody(statement));
      return;
    }

    if (firstChild.is(CKeyword.WHILE)) {
      checkLoopBody(statement, firstChild, getWhileBody(statement));
      return;
    }

    if (firstChild.is(CKeyword.DO)) {
      checkLoopBody(statement, firstChild, getDoBody(statement));
    }
  }

  private void checkIfStatement(AstNode statement, AstNode ifKeyword) {
    AstNode ifBody = null;
    AstNode elseKeyword = null;
    AstNode elseBody = null;

    boolean pastRightParen = false;
    boolean pastElse = false;
    for (AstNode child : statement.getChildren()) {
      if (!pastRightParen) {
        if (child.is(CGrammar.CONTROL_STATEMENT)) {
          continue;
        }
        if (")".equals(child.getTokenValue())) {
          pastRightParen = true;
        }
        continue;
      }

      if (!pastElse && child.is(CGrammar.STATEMENT)) {
        ifBody = child;
        continue;
      }

      if (child.is(CKeyword.ELSE)) {
        elseKeyword = child;
        pastElse = true;
        continue;
      }

      if (pastElse && child.is(CGrammar.STATEMENT)) {
        elseBody = child;
      }
    }

    checkBranch(statement, ifKeyword, ifBody);

    if (elseKeyword != null && elseBody != null) {
      checkBranch(statement, elseKeyword, elseBody);
    }
  }

  private void checkLoopBody(AstNode statement, AstNode keyword, AstNode body) {
    checkBranch(statement, keyword, body);
  }

  private void checkBranch(AstNode statement, AstNode keyword, AstNode body) {
    if (body == null) {
      return;
    }

    AstNode actualBody = getActualStatement(body);
    if (actualBody == null || actualBody.is(CGrammar.COMPOUND_STATEMENT)) {
      return;
    }

    AstNode nextStatement = getFollowingStatement(statement);
    if (nextStatement == null) {
      return;
    }

    int controlLine = keyword.getTokenLine();
    int bodyLine = actualBody.getTokenLine();
    int nextLine = nextStatement.getTokenLine();

    if (nextLine == controlLine) {
      addIssue(MESSAGE, keyword);
      return;
    }

    if (nextLine > bodyLine
        && indentation(nextStatement.getTokenLine()) > indentation(controlLine)) {
      addIssue(MESSAGE, keyword);
    }
  }

  private AstNode getActualStatement(AstNode statementNode) {
    if (statementNode == null) {
      return null;
    }
    List<AstNode> children = statementNode.getChildren();
    if (children.size() == 1) {
      return children.get(0);
    }
    return statementNode;
  }

  private AstNode getForBody(AstNode statement) {
    List<AstNode> children = statement.getChildren();
    if (children.isEmpty()) {
      return null;
    }
    AstNode last = children.get(children.size() - 1);
    return last.is(CGrammar.STATEMENT) ? last : null;
  }

  private AstNode getWhileBody(AstNode statement) {
    List<AstNode> children = statement.getChildren();
    if (children.isEmpty()) {
      return null;
    }
    AstNode last = children.get(children.size() - 1);
    return last.is(CGrammar.STATEMENT) ? last : null;
  }

  private AstNode getDoBody(AstNode statement) {
    List<AstNode> children = statement.getChildren();
    if (children.size() < 2) {
      return null;
    }
    AstNode second = children.get(1);
    return second.is(CGrammar.STATEMENT) ? second : null;
  }

  private AstNode getFollowingStatement(AstNode statement) {
    AstNode current = statement;
    while (current != null) {
      AstNode next = current.getNextSibling();
      if (next != null) {
        return next;
      }
      current = current.getParent();
    }
    return null;
  }

  private int indentation(int lineNumber) {
    if (lineNumber < 1 || lineNumber > lines.length) {
      return 0;
    }

    String line = lines[lineNumber - 1];
    int indentation = 0;
    for (int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);
      if (ch == ' ' || ch == '\t') {
        indentation++;
        continue;
      }
      break;
    }
    return indentation;
  }
}
