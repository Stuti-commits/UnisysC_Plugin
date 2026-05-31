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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonar.c.CCheck;
import org.sonar.c.CGrammar;
import org.sonar.c.checks.utils.Function;
import org.sonar.check.Rule;

@Rule(key = "S1117")
public class ShadowVariablesCheck extends CCheck {

  private static final String MESSAGE = "Rename this variable \"{0}\" to avoid shadowing an outer-scope variable.";

  private final Deque<Set<String>> scopes = new ArrayDeque<>();

  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.emptyList();
  }

  @Override
  public void visitFile(@Nullable AstNode tree) {
    scopes.clear();
    if (tree != null) {
      traverse(tree);
    }
  }

  private void traverse(AstNode node) {
    boolean newScope = isScopeNode(node);
    if (newScope) {
      scopes.push(new HashSet<>());
    }

    if (node.is(CGrammar.FUNCTION_DEF)) {
      declareFunctionParameters(node);
    } else if (node.is(CGrammar.DECLARATION)) {
      declareFromDeclaration(node);
    } else if (node.is(CGrammar.VARIABLE_DEF)) {
      declareFromVariableDef(node, CGrammar.IDENTIFIER_LIST);
    } else if (node.is(CGrammar.VARIABLE_DEF_NO_IN)) {
      declareFromVariableDef(node, CGrammar.IDENTIFIER_LIST);
    }

    for (AstNode child : node.getChildren()) {
      traverse(child);
    }

    if (newScope) {
      scopes.pop();
    }
  }

  private boolean isScopeNode(AstNode node) {
    return node.is(CGrammar.PROGRAM)
        || node.is(CGrammar.FUNCTION_DEF)
        || node.is(CGrammar.COMPOUND_STATEMENT)
        || node.is(CGrammar.FOR_STATEMENT);
  }

  private void declareFunctionParameters(AstNode functionDef) {
    for (AstNode parameterIdentifier : collectFunctionParameterIdentifiers(functionDef)) {
      declare(parameterIdentifier);
    }
  }

  private List<AstNode> collectFunctionParameterIdentifiers(AstNode functionDef) {
    List<AstNode> identifiers = new ArrayList<>();

    AstNode declarator = functionDef.getFirstChild(CGrammar.DECLARATOR);
    if (declarator == null) {
      return identifiers;
    }

    AstNode directDeclarator = declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
    if (directDeclarator == null) {
      return identifiers;
    }

    identifiers.addAll(Function.getParametersIdentifiers(functionDef));

    AstNode identifierList = directDeclarator.getFirstChild(CGrammar.IDENTIFIER_LIST);
    if (identifierList != null) {
      identifiers.addAll(identifierList.getChildren(CGrammar.IDENTIFIER));
    }

    return identifiers;
  }

  private void declareFromDeclaration(AstNode declaration) {
    if (isTypedefDeclaration(declaration)) {
      return;
    }

    AstNode initDeclList = declaration.getFirstChild(CGrammar.INIT_DECLARATOR_LIST);
    if (initDeclList == null) {
      return;
    }

    for (AstNode initDecl : initDeclList.getChildren(CGrammar.INIT_DECLARATOR)) {
      AstNode declarator = initDecl.getFirstChild(CGrammar.DECLARATOR);
      if (declarator == null) {
        continue;
      }

      AstNode directDeclarator = declarator.getFirstChild(CGrammar.DIRECT_DECLARATOR);
      if (directDeclarator == null) {
        continue;
      }

      AstNode identifier = directDeclarator.getFirstChild(CGrammar.IDENTIFIER);
      if (identifier != null) {
        declare(identifier);
      }
    }
  }

  private boolean isTypedefDeclaration(AstNode declaration) {
    AstNode declarationSpecifiers = declaration.getFirstChild(CGrammar.DECLARATION_SPECIFIERS);
    if (declarationSpecifiers == null) {
      return false;
    }

    return declarationSpecifiers.hasDescendant(CGrammar.TYPEDEF_NAME);
  }

  private void declareFromVariableDef(AstNode variableDef, AstNodeType bindingListType) {
    AstNode bindingList = variableDef.getFirstChild(bindingListType);
    if (bindingList == null) {
      return;
    }

    for (AstNode binding : bindingList.getChildren(CGrammar.VARIABLE_BINDING)) {
      AstNode identifier = binding.getFirstChild(CGrammar.IDENTIFIER);
      if (identifier != null) {
        declare(identifier);
      }
    }
  }

  private void declare(AstNode identifier) {
    if (scopes.isEmpty()) {
      return;
    }

    String name = identifier.getTokenValue();
    if (name == null || name.isEmpty()) {
      return;
    }

    Set<String> currentScope = scopes.peek();
    if (currentScope.contains(name)) {
      return;
    }

    if (isDefinedInOuterScope(name)) {
      addIssue(formatMessage(name), identifier);
    }

    currentScope.add(name);
  }

  private boolean isDefinedInOuterScope(String name) {
    boolean skipCurrentScope = true;
    for (Set<String> scope : scopes) {
      if (skipCurrentScope) {
        skipCurrentScope = false;
        continue;
      }
      if (scope.contains(name)) {
        return true;
      }
    }
    return false;
  }

  private String formatMessage(String name) {
    return MESSAGE.replace("{0}", name);
  }
}
