# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SonarSource Unisys C Plugin - a static code analyzer for Unisys C language that integrates with SonarQube. The plugin parses Unisys C source files, computes metrics, and reports code quality issues.

## Build Commands

```bash
# Build entire project (skip tests)
mvn clean install -DskipTests

# Build with tests
mvn clean install

# Run unit tests for a specific module
mvn test -pl c-checks

# Run a single test class
mvn test -pl c-checks -Dtest=EmptyStatementCheckTest

## Module Structure

- **c-squid**: Core parsing library using SonarSource SSLR (SonarSource Language Recognizer)
  - `CGrammar.java` - Complete Unisys C grammar definition
  - `CVisitor.java` - AST visitor base class for traversing parsed trees
  - `CCheck.java` - Base interface for implementing code checks

- **c-checks**: All code quality rules (~80 checks)
  - `CheckList.java` - Registry of all available checks
  - Each check extends `CVisitor` and implements `CCheck`

- **sonar-c-plugin**: SonarQube plugin assembly
  - `CPlugin.java` - Plugin entry point, registers extensions
  - `CSquidSensor.java` - Main sensor that orchestrates file analysis

- **sslr-c-toolkit**: Development tool for testing grammar

## Writing Checks

Checks are AST visitors that subscribe to specific grammar nodes:

```java
public class MyCheck extends CVisitor implements CCheck {
  @Override
  public List<AstNodeType> subscribedTo() {
    return Collections.singletonList(CGrammar.IF_STATEMENT);
  }

  @Override
  public void visitNode(AstNode node) {
    // Analyze node and report issues
    addIssue("Message", node);
  }
}
```

Test files use comment annotations for expected issues:
```actionscript
if (true) { } // Noncompliant {{Expected message}}
```

Testing uses `CVerifier`:
```java
CVerifier.verify(new File("src/test/resources/checks/MyCheck.as"), new MyCheck());
```

## Key Dependencies

- **SSLR** (`org.sonarsource.sslr`) - Parser framework
- **sonar-plugin-api** - SonarQube plugin API (provided scope)
- **sonar-analyzer-commons** - Shared analyzer utilities

## Grammar Reference

The grammar in `CGrammar.java` defines all Unisys C constructs. Key entry points:
- `PROGRAM` - Root rule
- `FUNCTION_DEF` - Function declarations
- `STATEMENT` - All statement types
- `ASSIGNMENT_EXPR` - Expression hierarchy root
