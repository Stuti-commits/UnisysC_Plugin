
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
package org.sonar.flex;

import com.sonar.sslr.api.GenericTokenType;

import org.sonar.flex.api.CKeyword;
import org.sonar.flex.api.CPunctuator;
import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerlessGrammarBuilder;
import org.sonar.sslr.parser.LexerlessGrammar;

import java.util.List;
import static org.sonar.flex.api.CKeyword.AS;
import static org.sonar.flex.api.CKeyword.ASM;
import static org.sonar.flex.api.CKeyword.BREAK;
import static org.sonar.flex.api.CKeyword.CASE;
import static org.sonar.flex.api.CKeyword.CATCH;
import static org.sonar.flex.api.CKeyword.CLASS;
import static org.sonar.flex.api.CKeyword.CONST;
import static org.sonar.flex.api.CKeyword.CONTINUE;
import static org.sonar.flex.api.CKeyword.DEFAULT;
import static org.sonar.flex.api.CKeyword.DELETE;
import static org.sonar.flex.api.CKeyword.DO;
import static org.sonar.flex.api.CKeyword.DYNAMIC;
import static org.sonar.flex.api.CKeyword.EACH;
import static org.sonar.flex.api.CKeyword.ELSE;
import static org.sonar.flex.api.CKeyword.EXTENDS;
import static org.sonar.flex.api.CKeyword.FALSE;
import static org.sonar.flex.api.CKeyword.FINALLY;
import static org.sonar.flex.api.CKeyword.FOR;
import static org.sonar.flex.api.CKeyword.FUNCTION;
import static org.sonar.flex.api.CKeyword.GET;
import static org.sonar.flex.api.CKeyword.IF;
import static org.sonar.flex.api.CKeyword.IMPLEMENTS;
import static org.sonar.flex.api.CKeyword.IMPORT;
import static org.sonar.flex.api.CKeyword.IN;
import static org.sonar.flex.api.CKeyword.INCLUDE;
import static org.sonar.flex.api.CKeyword.INSTANCEOF;
import static org.sonar.flex.api.CKeyword.INTERFACE;
import static org.sonar.flex.api.CKeyword.INTERNAL;
import static org.sonar.flex.api.CKeyword.IS;
import static org.sonar.flex.api.CKeyword.NAMESPACE;
import static org.sonar.flex.api.CKeyword.NEW;
import static org.sonar.flex.api.CKeyword.NULL;
import static org.sonar.flex.api.CKeyword.PACKAGE;
import static org.sonar.flex.api.CKeyword.PRIVATE;
import static org.sonar.flex.api.CKeyword.PROTECTED;
import static org.sonar.flex.api.CKeyword.PUBLIC;
import static org.sonar.flex.api.CKeyword.RETURN;
import static org.sonar.flex.api.CKeyword.SET;
import static org.sonar.flex.api.CKeyword.STATIC;
import static org.sonar.flex.api.CKeyword.SUPER;
import static org.sonar.flex.api.CKeyword.SWITCH;
import static org.sonar.flex.api.CKeyword.THIS;
import static org.sonar.flex.api.CKeyword.THROW;
import static org.sonar.flex.api.CKeyword.TRUE;
import static org.sonar.flex.api.CKeyword.TRY;
import static org.sonar.flex.api.CKeyword.TYPEOF;
import static org.sonar.flex.api.CKeyword.USE;
import static org.sonar.flex.api.CKeyword.VAR;
import static org.sonar.flex.api.CKeyword.VOID;
import static org.sonar.flex.api.CKeyword.WHILE;
import static org.sonar.flex.api.CKeyword.WITH;
import static org.sonar.flex.api.CKeyword.XML;
import static org.sonar.flex.api.CPunctuator.AND;
import static org.sonar.flex.api.CPunctuator.ANDAND;
import static org.sonar.flex.api.CPunctuator.ANDAND_EQU;
import static org.sonar.flex.api.CPunctuator.AND_EQU;
import static org.sonar.flex.api.CPunctuator.AT_SIGN;
import static org.sonar.flex.api.CPunctuator.COLON;
import static org.sonar.flex.api.CPunctuator.COMMA;
import static org.sonar.flex.api.CPunctuator.DIV;
import static org.sonar.flex.api.CPunctuator.DIV_EQU;
import static org.sonar.flex.api.CPunctuator.DOT;
import static org.sonar.flex.api.CPunctuator.DOUBLE_COLON;
import static org.sonar.flex.api.CPunctuator.DOUBLE_DOT;
import static org.sonar.flex.api.CPunctuator.DOUBLE_MINUS;
import static org.sonar.flex.api.CPunctuator.DOUBLE_PLUS;
import static org.sonar.flex.api.CPunctuator.EQUAL1;
import static org.sonar.flex.api.CPunctuator.EQUAL2;
import static org.sonar.flex.api.CPunctuator.EQUAL3;
import static org.sonar.flex.api.CPunctuator.GE;
import static org.sonar.flex.api.CPunctuator.GT;
import static org.sonar.flex.api.CPunctuator.LBRAKET;
import static org.sonar.flex.api.CPunctuator.LCURLYBRACE;
import static org.sonar.flex.api.CPunctuator.LE;
import static org.sonar.flex.api.CPunctuator.LPARENTHESIS;
import static org.sonar.flex.api.CPunctuator.LT;
import static org.sonar.flex.api.CPunctuator.MINUS;
import static org.sonar.flex.api.CPunctuator.MINUS_EQU;
import static org.sonar.flex.api.CPunctuator.MOD;
import static org.sonar.flex.api.CPunctuator.MOD_EQU;
import static org.sonar.flex.api.CPunctuator.NOT;
import static org.sonar.flex.api.CPunctuator.NOTEQUAL1;
import static org.sonar.flex.api.CPunctuator.NOTEQUAL2;
import static org.sonar.flex.api.CPunctuator.OR;
import static org.sonar.flex.api.CPunctuator.OROR;
import static org.sonar.flex.api.CPunctuator.OROR_EQU;
import static org.sonar.flex.api.CPunctuator.OR_EQU;
import static org.sonar.flex.api.CPunctuator.PLUS;
import static org.sonar.flex.api.CPunctuator.PLUS_EQU;
import static org.sonar.flex.api.CPunctuator.QUERY;
import static org.sonar.flex.api.CPunctuator.RBRAKET;
import static org.sonar.flex.api.CPunctuator.RCURLYBRACE;
import static org.sonar.flex.api.CPunctuator.RPARENTHESIS;
import static org.sonar.flex.api.CPunctuator.SEMICOLON;
import static org.sonar.flex.api.CPunctuator.SL;
import static org.sonar.flex.api.CPunctuator.SL_EQU;
import static org.sonar.flex.api.CPunctuator.SR;
import static org.sonar.flex.api.CPunctuator.SR2;
import static org.sonar.flex.api.CPunctuator.SR_EQU;
import static org.sonar.flex.api.CPunctuator.SR_EQU2;
import static org.sonar.flex.api.CPunctuator.STAR;
import static org.sonar.flex.api.CPunctuator.STAR_EQU;
import static org.sonar.flex.api.CPunctuator.TILD;
import static org.sonar.flex.api.CPunctuator.TRIPLE_DOTS;
import static org.sonar.flex.api.CPunctuator.XOR;
import static org.sonar.flex.api.CPunctuator.XORXOR_EQU;
import static org.sonar.flex.api.CPunctuator.XOR_EQU;
import static org.sonar.flex.api.CPunctuator.ARROW;

public enum FlexGrammar implements GrammarRuleKey {

        WHITESPACE,
        SPACING,
        SPACING_NO_LB,
        NEXT_NOT_LB,

        EOS,
        EOS_NO_LB,

        STRING,
        NUMBER,
        DECIMAL,
        HEXADECIMAL,
        OCTAL,

        /**
         * EXPRESSIONS
         */
        // <editor-fold defaultstate="collapsed" desc="Expression">
        PRIMARY_EXPR,
        RESERVED_NAMESPACE,
        PARENTHESIZED_EXPR,
        PARENTHESIZED_LIST_EXPR,
        FUNCTION_EXPR,
        OBJECT_INITIALISER,
        FIELD_NAME,
        LITERAL_FIELD,
        ARRAY_INITIALISER,
        ELEMENT_LIST,
        LITERAL_ELEMENT,
        CONDITIONAL_EXPR,
        CONDITIONAL_EXPR_NO_IN,
        POSTFIX_EXPR,
        COMPOUND_ASSIGNMENT,
        LOGICAL_ASSIGNMENT,
        SUPER_EXPR,
        // Identifiers
        PROPERTY_IDENTIFIER,
        QUALIFIER,
        SIMPLE_QUALIFIED_IDENTIFIER,
        EXPR_QUALIFIED_IDENTIFIER,
        NON_ATTRIBUTE_QUALIFIED_IDENTIFIER,
        QUALIFIED_IDENTIFIER,
        BRACKETS,
        IDENTIFIER,
        IDENTIFIER_PART,
        // New expressions
        FULL_NEW_EXPR,
        FULL_NEW_SUB_EXPR,
        SHORT_NEW_EXPR,
        SHORT_NEW_SUB_EXPR,
        PROPERTY_OPERATOR,
        QUERY_OPERATOR,
        // Call expression
        ARGUMENTS,
        // Unary expression
        UNARY_EXPR,
        // Binary expression
        MULTIPLICATIVE_EXPR,
        ADDITIVE_EXPR,
        ADDITIVE_OPERATOR,
        SHIFT_EXPR,
        RELATIONAL_EXPR,
        RELATIONAL_EXPR_NO_IN,
        RELATIONAL_OPERATOR,
        RELATIONAL_OPERATOR_NO_IN,
        EQUALITY_EXPR,
        EQUALITY_EXPR_NO_IN,
        EQUALITY_OPERATOR,
        BITEWISE_AND_EXPR,
        BITEWISE_AND_EXPR_NO_IN,
        BITEWISE_XOR_EXPR,
        BITEWISE_XOR_EXPR_NO_IN,
        BITEWISE_OR_EXPR,
        BITEWISE_OR_EXPR_NO_IN,
        LOGICAL_AND_EXPR,
        LOGICAL_AND_EXPR_NO_IN,
        LOGICAL_AND_OPERATOR,
        LOGICAL_OR_EXPR,
        LOGICAL_OR_EXPR_NO_IN,
        LOGICAL_OR_OPERATOR,
        // Assignment expression
        ASSIGNMENT_EXPR,
        ASSIGNMENT_EXPR_NO_IN,
        ASSIGNMENT_OPERATOR,
        // List expression
        LIST_EXPRESSION,
        LIST_EXPRESSION_NO_IN,
        // Non assignment expression
        NON_ASSIGNMENT_EXPR,
        NON_ASSIGNMENT_EXPR_NO_IN,
        // Type expression
        TYPE_EXPR,
        TYPE_EXPR_NO_IN,
        TYPE_APPLICATION,
        VECTOR_LITERAL_EXPRESSION,
        // XML Initialiser
        XML_INITIALISER,
        XML_MARKUP,
        XML_ELEMENT,
        XML_TAG_CONTENT,
        XML_WHITESPACE,
        XML_TAG_NAME,
        XML_ATTRIBUTE,
        XML_ATTRIBUTES,
        XML_ATTRIBUTE_VALUE,
        XML_NAME,
        XML_ELEMENT_CONTENT,
        XML_TEXT,
        XML_COMMENT,
        XML_CDATA,
        XML_PI,
        KEYWORDS,
        REGULAR_EXPRESSION,
        // </editor-fold>

        /**
         * DEFINITIONS
         */
        // <editor-fold defaultstate="collapsed" desc="Definitions">
        // Variable
        VARIABLE_DEF,
        VARIABLE_DEF_NO_IN,
        VARIABLE_DEF_KIND,
        VARIABLE_BINDING_LIST,
        VARIABLE_BINDING_LIST_NO_IN,
        VARIABLE_BINDING,
        VARIABLE_BINDING_NO_IN,
        VARIABLE_INITIALISATION,
        VARIABLE_INITIALISATION_NO_IN,
        TYPED_IDENTIFIER,
        TYPED_IDENTIFIER_NO_IN,
        VARIABLE_INITIALISER,
        VARIABLE_INITIALISER_NO_IN,
        // Function
        FUNCTION_DEF,
        FUNCTION_NAME,
        FUNCTION_COMMON,
        FUNCTION_SIGNATURE,
        RESULT_TYPE,
        PARAMETERS,
        PARAMETER,
        REST_PARAMETERS,
        // Class
        CLASS_DEF,
        CLASS_NAME,
        INHERITENCE,
        CLASS_IDENTIFIERS,
        TYPE_EXPRESSION_LIST,
        // Interface
        INTERFACE_DEF,
        EXTENDS_LIST,
        // Package
        PACKAGE_DEF,
        PACKAGE_NAME,
        // Namespace
        NAMESPACE_DEF,
        NAMESPACE_BINDING,
        NAMESPACE_INITIALISATION,
        // Program
        PROGRAM,
        // </editor-fold>

        /**
         * STATEMENTS
         */
        // <editor-fold defaultstate="collapsed" desc="Statements">
        STATEMENT,
        SUPER_STATEMENT,
        SWITCH_STATEMENT,
        IF_STATEMENT,
        DO_STATEMENT,
        WHILE_STATEMENT,
        FOR_STATEMENT,
        WITH_STATEMENT,
        CONTINUE_STATEMENT,
        BREAK_STATEMENT,
        RETURN_STATEMENT,
        THROW_STATEMENT,
        TRY_STATEMENT,
        EXPRESSION_STATEMENT,
        EXPRESSION,
        LABELED_STATEMENT,
        METADATA_STATEMENT,
        DEFAULT_XML_NAMESPACE_DIRECTIVE,
        SUB_STATEMENT,
        EMPTY_STATEMENT,
        VARIABLE_DECLARATION_STATEMENT,
        BLOCK,
        CASE_ELEMENT,
        CASE_LABEL,
        FOR_INITIALISER,
        FOR_IN_BINDING,
        CATCH_CLAUSE,
        CATCH_CLAUSES,
        DECIMAL_DIGITS,
        EXPONENT_PART,
        DECIMAL_INTEGER,
        // </editor-fold>

        /**
         * DIRECTIVES
         */
        // <editor-fold defaultstate="collapsed" desc="Directives">
        DIRECTIVES,
        DIRECTIVE,
        CONFIG_CONDITION,
        ANNOTABLE_DIRECTIVE,
        USE_DIRECTIVE,
        IMPORT_DIRECTIVE,
        INCLUDE_DIRECTIVE,
        ATTRIBUTES,
        ATTRIBUTE,
        ATTRIBUTE_COMBINATION,
        ATTRIBUTE_EXPR,
        // </editor-fold>

        C_PRIMARY_EXPR,
        C_POSTFIX_EXPR,
        C_ARG_EXPR_LIST,
        C_UNARY_OPERATOR,
        C_UNARY_EXPR,
        C_CAST_EXPR,
        C_TYPE_NAME,
        C_MULTIPLICATIVE_EXPR,
        C_ADDITIVE_EXPR,
        C_SHIFT_EXPR,
        C_RELATIONAL_EXPR,
        C_EQUALITY_EXPR,
        C_AND_EXPR,
        C_XOR_EXPR,
        C_OR_EXPR,
        C_LOGICAL_AND_EXPR,
        C_LOGICAL_OR_EXPR,
        C_CONDITIONAL_EXPR,
        C_ASSIGNMENT_OPERATOR,
        C_ASSIGNMENT_EXPR,
        C_EXPRESSION,
        C_IDENTIFIER,
        C_INTEGER_CONSTANT,
        C_FLOAT_CONSTANT,
        C_CHAR_CONSTANT,
        C_STRING_LITERAL,
        C_CONSTANT,
        C_TYPE_SPECIFIER,
        C_INTEGER_TYPE_SPECIFIER,
        C_TYPE_QUALIFIER,
        C_STORAGE_CLASS_SPECIFIER,
        C_DECLARATION_SPECIFIERS,
        C_INIT_DECLARATOR,
        C_INIT_DECLARATOR_LIST,
        C_INITIALIZER,
        C_INITIALIZER_LIST,
        C_DECLARATOR,
        C_DIRECT_DECLARATOR,
        C_POINTER_DECLARATOR,
        C_DECLARATION,
        C_FAR_NEAR_QUALIFIER,
        C_FUNCTION_DEFINITION,
        C_FUNCTION_BODY,
        C_BLOCK_ITEM,
        C_BLOCK_ITEM_LIST, GOTO_STATEMENT;

        private static final String UNICODE_LETTER = "\\p{Lu}\\p{Ll}\\p{Lt}\\p{Lm}\\p{Lo}\\p{Nl}";
        private static final String UNICODE_DIGIT = "\\p{Nd}";
        private static final String UNICODE_COMBINING_MARK = "\\p{Mn}\\p{Mc}";
        private static final String UNICODE_CONNECTOR_PUNCTUATION = "\\p{Pc}";

        private static final String UNICODE_ESCAPE_SEQUENCE_REGEXP = "u[0-9a-fA-F]{4,4}";
        private static final String IDENTIFIER_START_REGEXP = "(?:[$_" + UNICODE_LETTER + "]|\\\\"
                        + UNICODE_ESCAPE_SEQUENCE_REGEXP + ")";
        private static final String IDENTIFIER_PART_REGEXP = "(?:" +
                        IDENTIFIER_START_REGEXP + "|[" + UNICODE_COMBINING_MARK + UNICODE_DIGIT
                        + UNICODE_CONNECTOR_PUNCTUATION
                        + "])";

        private static final String EXPONENT_PART_REGEXP = "([eE][-+]?[0-9]++)?";
        private static final String DECIMAL_INTEGER_REGEXP = "(0|([1-9][0-9]*+))";
        private static final String DECIMAL_DIGITS_REGEXP = "([0-9]*+)";
        private static final String DECIMAL_REGEXP = DECIMAL_INTEGER_REGEXP + "\\." + DECIMAL_DIGITS_REGEXP + "?"
                        + EXPONENT_PART_REGEXP +
                        "|\\." + DECIMAL_DIGITS_REGEXP + EXPONENT_PART_REGEXP +
                        "|" + DECIMAL_INTEGER_REGEXP + EXPONENT_PART_REGEXP;

        private static final String SINGLE_LINE_COMMENT_REGEXP = "//[^\\n\\r]*+";

        private static final String MULTI_LINE_COMMENT_REGEXP = "/\\*[\\s\\S]*?\\*/";
        private static final String MULTI_LINE_COMMENT_NO_LB_REGEXP = "/\\*[^\\n\\r]*?\\*/";

        /**
         * LF, CR, LS, PS
         */
        private static final String LINE_TERMINATOR_REGEXP = "\\n\\r\\p{Zl}\\p{Zp}";

        /**
         * tab, vertical tab, form feed, space, no-break space, Byte Order Mark, any
         * other Unicode "space character"
         */
        private static final String WHITESPACE_REGEXP = "\\t\\v\\f\\u0020\\u00A0\\uFEFF\\p{Zs}";

        public static final String STRING_REGEXP = "(?:\"([^\"\\\\]*+(\\\\[\\s\\S])?+)*+\"|\'([^\'\\\\]*+(\\\\[\\s\\S])?+)*+\')";

        private static final String NEWLINE_REGEXP = "(?:\\n|\\r\\n|\\r)";

        public static LexerlessGrammar createGrammar() {
                LexerlessGrammarBuilder b = LexerlessGrammarBuilder.create();

                b.rule(WHITESPACE).is(b.regexp("[" + LINE_TERMINATOR_REGEXP + WHITESPACE_REGEXP + "]*+"));

                b.rule(SPACING).is(
                                b.skippedTrivia(WHITESPACE),
                                b.zeroOrMore(
                                                b.commentTrivia(
                                                                b.regexp("(?:" + SINGLE_LINE_COMMENT_REGEXP + "|"
                                                                                + MULTI_LINE_COMMENT_REGEXP + ")")),
                                                b.skippedTrivia(WHITESPACE)))
                                .skip();

                b.rule(SPACING_NO_LB).is(b.zeroOrMore(b.firstOf(
                                b.skippedTrivia(b.regexp("[\\s&&[^\n\r]]++")),
                                b.commentTrivia(
                                                b.regexp("(?:" + SINGLE_LINE_COMMENT_REGEXP + "|"
                                                                + MULTI_LINE_COMMENT_NO_LB_REGEXP + ")")))))
                                .skip();
                b.rule(NEXT_NOT_LB).is(b.nextNot(b.regexp("(?:" + "[\n\r]" + "|" + MULTI_LINE_COMMENT_REGEXP + ")")))
                                .skip();

                b.rule(EOS).is(b.firstOf(
                                b.sequence(SPACING, ";"),
                                b.sequence(SPACING_NO_LB, b.regexp(NEWLINE_REGEXP)),
                                b.sequence(SPACING_NO_LB, b.next("}")),
                                b.sequence(SPACING, b.endOfInput())));
                b.rule(EOS_NO_LB).is(b.firstOf(
                                b.sequence(SPACING_NO_LB, ";"),
                                b.sequence(SPACING_NO_LB, b.regexp(NEWLINE_REGEXP)),
                                b.sequence(SPACING_NO_LB, b.next("}")),
                                b.sequence(SPACING_NO_LB, b.endOfInput())));

                punctuators(b);
                keywords(b);
                literals(b);
                expressions(b);
                statements(b);
                directives(b);
                definitions(b);
                xml(b);
                cExpressions(b);
                cDeclarations(b);
                cFunctionDefinitions(b);
                b.setRootRule(PROGRAM);

                return b.build();
        }

        // ============================================================
        // UNISYS C EXPRESSION GRAMMAR
        // Appendix G, Phrase Structure Grammar — C Reference Manual
        // Uses existing FlexPunctuator and FlexKeyword constants.
        // New token added: FlexPunctuator.ARROW ("->")
        // New keyword added: FlexKeyword.TYPEDEF
        // ============================================================
        private static void cExpressions(LexerlessGrammarBuilder b) {

                // C_IDENTIFIER: letter/underscore then alphanumerics, must not be a C keyword
                b.rule(C_IDENTIFIER).is(
                                SPACING,
                                b.nextNot(b.firstOf(
                                                CKeyword.INT, CKeyword.CHAR, CKeyword.FLOAT,
                                                CKeyword.DOUBLE, CKeyword.VOID, CKeyword.STRUCT,
                                                CKeyword.sizeof, CKeyword.IF, CKeyword.WHILE,
                                                CKeyword.FOR, CKeyword.DO, CKeyword.RETURN,
                                                CKeyword.ELSE, CKeyword.GOTO, CKeyword.EXTERN,
                                                CKeyword.STATIC, CKeyword.CONST, CKeyword.VOLATILE,
                                                CKeyword.TYPEDEF)),
                                b.regexp("[a-zA-Z_][a-zA-Z_0-9]*+"));

                // C_INTEGER_CONSTANT: hex 0xFF, octal 077, decimal 42, with u/l suffix
                b.rule(C_INTEGER_CONSTANT).is(
                                SPACING,
                                b.regexp("(0[xX][0-9a-fA-F]++|0[0-7]*+|[1-9][0-9]*+)[uUlL]*+"));

                // C_FLOAT_CONSTANT: 3.14, 1.5e-3, .5f
                b.rule(C_FLOAT_CONSTANT).is(
                                SPACING,
                                b.regexp("[0-9]*+\\.[0-9]++([eE][+-]?+[0-9]++)?+[fFlL]?+" +
                                                "|[0-9]++[eE][+-]?+[0-9]++[fFlL]?+"));

                // C_CHAR_CONSTANT: 'a', '\n', L'x'
                b.rule(C_CHAR_CONSTANT).is(
                                SPACING,
                                b.regexp("L?+'([^'\\\\\\n]|\\\\.)++'"));

                // C_CONSTANT: float before integer so "3.14" doesn't match as just "3"
                b.rule(C_CONSTANT).is(b.firstOf(
                                C_FLOAT_CONSTANT,
                                C_INTEGER_CONSTANT,
                                C_CHAR_CONSTANT));

                // C_STRING_LITERAL: "hello", L"wide string"
                b.rule(C_STRING_LITERAL).is(
                                SPACING,
                                b.regexp("L?+\"([^\"\\\\\\n]|\\\\.)*+\""));

                // C_TYPE_NAME: basic C types — used in sizeof(type) and cast (type)expr
                // PDF: type-name used inside cast-expression and sizeof
                b.rule(C_TYPE_NAME).is(b.firstOf(
                                CKeyword.INT, CKeyword.CHAR,
                                CKeyword.FLOAT, CKeyword.DOUBLE,
                                CKeyword.VOID, CKeyword.LONG,
                                CKeyword.SHORT, CKeyword.unsigned,
                                CKeyword.signed));

                // PDF p.G-4: primary-expression
                // constant | identifier | string-literal | ( expression )
                b.rule(C_PRIMARY_EXPR).is(b.firstOf(
                                C_CONSTANT,
                                C_STRING_LITERAL,
                                C_IDENTIFIER,
                                b.sequence(LPARENTHESIS, C_EXPRESSION, RPARENTHESIS)));

                // PDF p.G-5: postfix-expression
                // a[i] f() a.b a->b a++ a--
                b.rule(C_POSTFIX_EXPR).is(
                                C_PRIMARY_EXPR,
                                b.zeroOrMore(b.firstOf(
                                                b.sequence(LBRAKET, C_EXPRESSION, RBRAKET), // a[i]
                                                b.sequence(LPARENTHESIS, b.optional(C_ARG_EXPR_LIST), RPARENTHESIS), // f()
                                                b.sequence(DOT, C_IDENTIFIER), // a.b
                                                b.sequence(ARROW, C_IDENTIFIER), // a->b
                                                DOUBLE_PLUS, // a++
                                                DOUBLE_MINUS // a--
                                )));

                // PDF p.G-4: argument-expression-list
                b.rule(C_ARG_EXPR_LIST).is(
                                C_ASSIGNMENT_EXPR,
                                b.zeroOrMore(COMMA, C_ASSIGNMENT_EXPR));

                // PDF p.G-5: unary-operator & * + - ~ !
                b.rule(C_UNARY_OPERATOR).is(b.firstOf(
                                AND, // & (address-of)
                                STAR, // * (dereference)
                                PLUS, // +
                                MINUS, // -
                                TILD, // ~ (bitwise NOT)
                                NOT // ! (logical NOT)
                ));

                // PDF p.G-5: unary-expression
                b.rule(C_UNARY_EXPR).is(b.firstOf(
                                b.sequence(DOUBLE_PLUS, C_UNARY_EXPR), // ++x
                                b.sequence(DOUBLE_MINUS, C_UNARY_EXPR), // --x
                                b.sequence(C_UNARY_OPERATOR, C_CAST_EXPR), // -x, *p, &x
                                b.sequence(CKeyword.sizeof, C_UNARY_EXPR), // sizeof x
                                b.sequence(CKeyword.sizeof, LPARENTHESIS, C_TYPE_NAME, RPARENTHESIS), // sizeof(int)
                                C_POSTFIX_EXPR));

                // PDF p.G-4: cast-expression
                // (type-name) cast-expression | unary-expression
                b.rule(C_CAST_EXPR).is(b.firstOf(
                                b.sequence(LPARENTHESIS, C_TYPE_NAME, RPARENTHESIS, C_CAST_EXPR), // (int)x
                                C_UNARY_EXPR));

                // PDF p.G-5: multiplicative-expression * / %
                b.rule(C_MULTIPLICATIVE_EXPR).is(
                                C_CAST_EXPR,
                                b.zeroOrMore(b.firstOf(STAR, DIV, MOD), C_CAST_EXPR));

                // PDF p.G-4: additive-expression + -
                b.rule(C_ADDITIVE_EXPR).is(
                                C_MULTIPLICATIVE_EXPR,
                                b.zeroOrMore(b.firstOf(PLUS, MINUS), C_MULTIPLICATIVE_EXPR));

                // PDF p.G-5: shift-expression << >>
                b.rule(C_SHIFT_EXPR).is(
                                C_ADDITIVE_EXPR,
                                b.zeroOrMore(b.firstOf(SL, SR), C_ADDITIVE_EXPR));

                // PDF p.G-5: relational-expression < > <= >=
                // longer tokens first: <= before <, >= before >
                b.rule(C_RELATIONAL_EXPR).is(
                                C_SHIFT_EXPR,
                                b.zeroOrMore(b.firstOf(LE, GE, LT, GT), C_SHIFT_EXPR));

                // PDF p.G-4: equality-expression == !=
                b.rule(C_EQUALITY_EXPR).is(
                                C_RELATIONAL_EXPR,
                                b.zeroOrMore(b.firstOf(EQUAL2, NOTEQUAL1), C_RELATIONAL_EXPR));

                // PDF p.G-4: AND-expression & (bitwise)
                b.rule(C_AND_EXPR).is(
                                C_EQUALITY_EXPR,
                                b.zeroOrMore(AND, C_EQUALITY_EXPR));

                // PDF p.G-4: exclusive-OR-expression ^
                b.rule(C_XOR_EXPR).is(
                                C_AND_EXPR,
                                b.zeroOrMore(XOR, C_AND_EXPR));

                // PDF p.G-4: inclusive-OR-expression |
                b.rule(C_OR_EXPR).is(
                                C_XOR_EXPR,
                                b.zeroOrMore(OR, C_XOR_EXPR));

                // PDF p.G-4: logical-AND-expression &&
                b.rule(C_LOGICAL_AND_EXPR).is(
                                C_OR_EXPR,
                                b.zeroOrMore(ANDAND, C_OR_EXPR));

                // PDF p.G-4: logical-OR-expression ||
                b.rule(C_LOGICAL_OR_EXPR).is(
                                C_LOGICAL_AND_EXPR,
                                b.zeroOrMore(OROR, C_LOGICAL_AND_EXPR));

                // PDF p.G-4: conditional-expression a ? b : c
                b.rule(C_CONDITIONAL_EXPR).is(
                                C_LOGICAL_OR_EXPR,
                                b.optional(QUERY, C_EXPRESSION, COLON, C_CONDITIONAL_EXPR));

                // PDF p.G-4: assignment-operator
                // compound operators BEFORE plain = (longest match wins)
                b.rule(C_ASSIGNMENT_OPERATOR).is(b.firstOf(
                                SL_EQU, // <<=
                                SR_EQU, // >>=
                                AND_EQU, // &=
                                XOR_EQU, // ^=
                                OR_EQU, // |=
                                STAR_EQU, // *=
                                DIV_EQU, // /=
                                MOD_EQU, // %=
                                PLUS_EQU, // +=
                                MINUS_EQU, // -=
                                EQUAL1 // = plain assignment LAST
                ));

                // PDF p.G-4: assignment-expression ← ROOT of all C expressions
                b.rule(C_ASSIGNMENT_EXPR).is(b.firstOf(
                                b.sequence(C_UNARY_EXPR, C_ASSIGNMENT_OPERATOR, C_ASSIGNMENT_EXPR),
                                C_CONDITIONAL_EXPR));

                // PDF p.G-4: expression (comma-separated list)
                b.rule(C_EXPRESSION).is(
                                C_ASSIGNMENT_EXPR,
                                b.zeroOrMore(COMMA, C_ASSIGNMENT_EXPR));
        }

        private static void cDeclarations(LexerlessGrammarBuilder b) {

                // ── INTEGER_TYPE_SPECIFIER ──────────────────────────────────────────
                // All Unisys C integer type combos — LONGER alternatives FIRST.
                // Manual §2: all are 48 bits on Unisys MCP.
                b.rule(C_INTEGER_TYPE_SPECIFIER).is(b.firstOf(
                                b.sequence(CKeyword.signed, CKeyword.LONG, CKeyword.INT),
                                b.sequence(CKeyword.signed, CKeyword.SHORT, CKeyword.INT),
                                b.sequence(CKeyword.unsigned, CKeyword.LONG, CKeyword.INT),
                                b.sequence(CKeyword.unsigned, CKeyword.SHORT, CKeyword.INT),
                                b.sequence(CKeyword.signed, CKeyword.LONG),
                                b.sequence(CKeyword.signed, CKeyword.SHORT),
                                b.sequence(CKeyword.unsigned, CKeyword.LONG),
                                b.sequence(CKeyword.unsigned, CKeyword.SHORT),
                                b.sequence(CKeyword.signed, CKeyword.INT),
                                b.sequence(CKeyword.unsigned, CKeyword.INT),
                                b.sequence(CKeyword.LONG, CKeyword.INT),
                                b.sequence(CKeyword.SHORT, CKeyword.INT),
                                CKeyword.LONG,
                                CKeyword.SHORT,
                                CKeyword.INT,
                                CKeyword.signed,
                                CKeyword.unsigned));

                // ── C_FAR_NEAR_QUALIFIER ────────────────────────────────────────────
                // Unisys MCP extension — §3: requires FARHEAP compiler option.
                // Can appear before or after type: "__near char nc;" OR "char __far fc;"
                b.rule(C_FAR_NEAR_QUALIFIER).is(b.firstOf(
                                CKeyword.__FAR,
                                CKeyword.__NEAR));

                // ── C_TYPE_QUALIFIER ────────────────────────────────────────────────
                // Manual §3: const, volatile, plus Unisys __far/__near
                b.rule(C_TYPE_QUALIFIER).is(b.firstOf(
                                CKeyword.CONST,
                                CKeyword.VOLATILE,
                                C_FAR_NEAR_QUALIFIER));

                // ── C_STORAGE_CLASS_SPECIFIER ───────────────────────────────────────
                // Manual §3 Table 3-1: all valid storage class specifiers.
                // NOTE: AUTO is already in CKeyword as AS — check your keyword enum.
                b.rule(C_STORAGE_CLASS_SPECIFIER).is(b.firstOf(
                                CKeyword.AUTO,
                                CKeyword.EXTERN,
                                CKeyword.REGISTER,
                                CKeyword.STATIC,
                                CKeyword.TYPEDEF,
                                CKeyword.inline,
                                CKeyword.asm // Unisys MCP: export from library
                ));

                // ── C_TYPE_SPECIFIER ────────────────────────────────────────────────
                // Manual §3: all valid type specifiers.
                // long double = 96 bits on Unisys; plain double = 48 bits (same as float).
                b.rule(C_TYPE_SPECIFIER).is(b.firstOf(
                                C_INTEGER_TYPE_SPECIFIER,
                                b.sequence(CKeyword.LONG, CKeyword.DOUBLE), // 96-bit, before plain DOUBLE
                                b.sequence(CKeyword.signed, CKeyword.CHAR),
                                b.sequence(CKeyword.unsigned, CKeyword.CHAR),
                                CKeyword.DOUBLE,
                                CKeyword.FLOAT,
                                CKeyword.CHAR,
                                CKeyword.VOID
                // STRUCT_SPECIFIER, UNION_SPECIFIER, ENUM_SPECIFIER — add when those rules
                // exist
                ));

                // ── C_DECLARATION_SPECIFIERS ────────────────────────────────────────
                // Manual §3: opening of any declaration.
                // One storage class specifier max (enforced by semantic check, not grammar).
                b.rule(C_DECLARATION_SPECIFIERS).is(
                                b.oneOrMore(b.firstOf(
                                                C_STORAGE_CLASS_SPECIFIER,
                                                C_TYPE_QUALIFIER,
                                                C_TYPE_SPECIFIER)));

                // ── C_INITIALIZER / C_INITIALIZER_LIST ─────────────────────────────
                // Manual §3: "declarator = value" or "declarator = { list }"
                // Trailing comma allowed: { 1, 2, 3, }
                b.rule(C_INITIALIZER_LIST).is(
                                C_INITIALIZER,
                                b.zeroOrMore(b.sequence(COMMA, C_INITIALIZER)));
                b.rule(C_INITIALIZER).is(b.firstOf(
                                b.sequence(LCURLYBRACE, C_INITIALIZER_LIST, b.optional(COMMA), RCURLYBRACE),
                                C_ASSIGNMENT_EXPR // already defined in cExpressions()
                ));

                // ── C_DIRECT_DECLARATOR / C_DECLARATOR ─────────────────────────────
                // Manual §3 declarator syntax: identifier, array[n], array[], func(params)
                b.rule(C_DIRECT_DECLARATOR).is(
                                b.firstOf(
                                                C_IDENTIFIER,
                                                b.sequence(LPARENTHESIS, C_DECLARATOR, RPARENTHESIS)),
                                b.zeroOrMore(b.firstOf(
                                                b.sequence(LBRAKET, b.optional(C_EXPRESSION), RBRAKET), // array
                                                                                                        // subscript
                                                b.sequence(LPARENTHESIS, RPARENTHESIS) // function ()
                                )));
                // Pointer declarator: *qualifier* declarator (e.g., "int *const ptr")
                b.rule(C_POINTER_DECLARATOR).is(
                                b.oneOrMore(b.sequence(STAR, b.zeroOrMore(C_TYPE_QUALIFIER))),
                                C_DIRECT_DECLARATOR);
                b.rule(C_DECLARATOR).is(b.firstOf(
                                C_POINTER_DECLARATOR,
                                C_DIRECT_DECLARATOR));

                // ── C_INIT_DECLARATOR / C_INIT_DECLARATOR_LIST ─────────────────────
                // Manual §3: "declarator" or "declarator = initializer"
                b.rule(C_INIT_DECLARATOR).is(b.firstOf(
                                b.sequence(C_DECLARATOR, EQUAL1, C_INITIALIZER),
                                C_DECLARATOR));
                b.rule(C_INIT_DECLARATOR_LIST).is(
                                C_INIT_DECLARATOR,
                                b.zeroOrMore(b.sequence(COMMA, C_INIT_DECLARATOR)));

                // ── C_DECLARATION ───────────────────────────────────────────────────
                // Manual §3: the complete declaration statement.
                // "int x = 0;" "static unsigned long a, b;" "const int *p;"
                b.rule(C_DECLARATION).is(
                                C_DECLARATION_SPECIFIERS,
                                b.optional(C_INIT_DECLARATOR_LIST),
                                SEMICOLON);
        }

        private static void cFunctionDefinitions(LexerlessGrammarBuilder b) {

                // function-body: { block-item-list(opt) }
                // block items = local declarations OR statements
                b.rule(C_BLOCK_ITEM).is(b.firstOf(
                                C_DECLARATION,
                                STATEMENT)); // C_STATEMENT already exists from cExpressions/statements

                b.rule(C_BLOCK_ITEM_LIST).is(b.oneOrMore(C_BLOCK_ITEM));

                b.rule(C_FUNCTION_BODY).is(
                                LCURLYBRACE,
                                b.optional(C_BLOCK_ITEM_LIST),
                                RCURLYBRACE);

                // function-definition:
                // declaration-specifiers(opt) declarator function-body
                //
                // This covers:
                // main(){} <- no specifier (implicit int per manual §7)
                // int main(){} <- explicit int
                // void main(){} <- explicit void
                b.rule(C_FUNCTION_DEFINITION).is(
                                b.optional(C_DECLARATION_SPECIFIERS), // ← the key fix: OPTIONAL
                                C_DECLARATOR,
                                C_FUNCTION_BODY);
        }

        private static void literals(LexerlessGrammarBuilder b) {
                b.rule(STRING).is(SPACING, b.regexp(STRING_REGEXP));

                b.rule(HEXADECIMAL).is(SPACING, b.regexp("0[xX][0-9a-fA-F]++"));
                b.rule(OCTAL).is(SPACING, b.regexp("0[0-7]++"));
                b.rule(DECIMAL).is(SPACING, b.regexp(DECIMAL_REGEXP));
                b.rule(NUMBER).is(b.firstOf(OCTAL, DECIMAL, HEXADECIMAL));

                // Regular expression according to ECMA 262
                b.rule(REGULAR_EXPRESSION).is(SPACING, b.regexp(
                                "/"
                                                // Regular expression first char
                                                + "([^\\n\\r\\*\\\\/]|(\\\\[^\\n\\r]))"
                                                // Regular expression chars
                                                + "([^\\n\\r\\\\/]|(\\\\[^\\n\\r]))*"
                                                + "/"
                                                // Regular expression flags
                                                + IDENTIFIER_PART_REGEXP + "*+"));
        }

        private static void expressions(LexerlessGrammarBuilder b) {
                // Identifiers
                b.rule(IDENTIFIER).is(b.firstOf(
                                DYNAMIC,
                                EACH,
                                GET,
                                INCLUDE,
                                NAMESPACE,
                                SET,
                                STATIC,
                                b.sequence(SPACING, b.nextNot(KEYWORDS),
                                                b.regexp(IDENTIFIER_START_REGEXP + IDENTIFIER_PART_REGEXP + "*+"))));
                b.rule(IDENTIFIER_PART).is(b.regexp(IDENTIFIER_PART_REGEXP));

                b.rule(PROPERTY_IDENTIFIER).is(b.firstOf(
                                IDENTIFIER,
                                STAR));

                b.rule(QUALIFIER).is(b.firstOf(
                                PROPERTY_IDENTIFIER,
                                RESERVED_NAMESPACE));

                b.rule(SIMPLE_QUALIFIED_IDENTIFIER).is(b.firstOf(
                                b.sequence(QUALIFIER, DOUBLE_COLON, PROPERTY_IDENTIFIER),
                                b.sequence(QUALIFIER, DOUBLE_COLON, BRACKETS),
                                PROPERTY_IDENTIFIER));

                b.rule(EXPR_QUALIFIED_IDENTIFIER).is(b.firstOf(
                                b.sequence(PARENTHESIZED_EXPR, DOUBLE_COLON, PROPERTY_IDENTIFIER),
                                b.sequence(PARENTHESIZED_EXPR, BRACKETS)));

                b.rule(NON_ATTRIBUTE_QUALIFIED_IDENTIFIER).is(b.firstOf(
                                SIMPLE_QUALIFIED_IDENTIFIER,
                                EXPR_QUALIFIED_IDENTIFIER));

                b.rule(QUALIFIED_IDENTIFIER).is(b.firstOf(
                                b.sequence(AT_SIGN, BRACKETS),
                                b.sequence(AT_SIGN, NON_ATTRIBUTE_QUALIFIED_IDENTIFIER),
                                NON_ATTRIBUTE_QUALIFIED_IDENTIFIER));

                b.rule(PRIMARY_EXPR).is(b.firstOf(
                                NULL,
                                TRUE,
                                FALSE,
                                HEXADECIMAL,
                                NUMBER,
                                STRING,
                                THIS,
                                REGULAR_EXPRESSION,
                                XML_INITIALISER,
                                QUALIFIED_IDENTIFIER,
                                RESERVED_NAMESPACE,
                                PARENTHESIZED_EXPR,
                                ARRAY_INITIALISER,
                                OBJECT_INITIALISER,
                                FUNCTION_EXPR));

                b.rule(RESERVED_NAMESPACE).is(b.firstOf(PUBLIC, PRIVATE, PROTECTED, INTERNAL));

                b.rule(PARENTHESIZED_EXPR).is(LPARENTHESIS, ASSIGNMENT_EXPR, RPARENTHESIS);
                b.rule(PARENTHESIZED_LIST_EXPR).is(LPARENTHESIS, LIST_EXPRESSION, RPARENTHESIS);

                b.rule(FUNCTION_EXPR).is(b.firstOf(
                                b.sequence(FUNCTION, FUNCTION_COMMON),
                                b.sequence(FUNCTION, IDENTIFIER, FUNCTION_COMMON)));

                b.rule(OBJECT_INITIALISER).is(LCURLYBRACE,
                                b.optional(LITERAL_FIELD, b.zeroOrMore(COMMA, LITERAL_FIELD)),
                                RCURLYBRACE);
                b.rule(LITERAL_FIELD).is(FIELD_NAME, COLON, ASSIGNMENT_EXPR);
                b.rule(FIELD_NAME).is(b.firstOf(
                                NON_ATTRIBUTE_QUALIFIED_IDENTIFIER,
                                STRING,
                                NUMBER));

                // Array initialiser
                b.rule(ARRAY_INITIALISER).is(LBRAKET, b.optional(ELEMENT_LIST), RBRAKET);
                b.rule(ELEMENT_LIST).is(b.optional(COMMA), LITERAL_ELEMENT, b.zeroOrMore(COMMA, LITERAL_ELEMENT),
                                b.optional(COMMA));
                b.rule(LITERAL_ELEMENT).is(ASSIGNMENT_EXPR);

                // Assignement expressions
                b.rule(ASSIGNMENT_EXPR).is(b.firstOf(
                                b.sequence(POSTFIX_EXPR, ASSIGNMENT_OPERATOR, ASSIGNMENT_EXPR),
                                CONDITIONAL_EXPR));
                b.rule(ASSIGNMENT_EXPR_NO_IN).is(b.firstOf(
                                b.sequence(POSTFIX_EXPR, ASSIGNMENT_OPERATOR, ASSIGNMENT_EXPR_NO_IN),
                                CONDITIONAL_EXPR));
                b.rule(ASSIGNMENT_OPERATOR).is(b.firstOf(EQUAL1, COMPOUND_ASSIGNMENT, LOGICAL_ASSIGNMENT));
                b.rule(COMPOUND_ASSIGNMENT).is(
                                b.firstOf(STAR_EQU, DIV_EQU, MOD_EQU, PLUS_EQU, MINUS_EQU, SL_EQU, SR_EQU, SR_EQU2,
                                                AND_EQU, XOR_EQU,
                                                OR_EQU));
                b.rule(LOGICAL_ASSIGNMENT).is(b.firstOf(ANDAND_EQU, XORXOR_EQU, OROR_EQU));

                // Super expression
                b.rule(SUPER_EXPR).is(b.firstOf(
                                b.sequence(SUPER, ARGUMENTS),
                                SUPER));

                b.rule(POSTFIX_EXPR).is(b.firstOf(
                                FULL_NEW_EXPR,
                                b.sequence(SUPER_EXPR, b.optional(PROPERTY_OPERATOR)),
                                PRIMARY_EXPR,
                                SHORT_NEW_EXPR),
                                b.zeroOrMore(b.firstOf(
                                                PROPERTY_OPERATOR,
                                                ARGUMENTS,
                                                QUERY_OPERATOR,
                                                /* No line break */ b.sequence(SPACING_NO_LB, NEXT_NOT_LB, DOUBLE_PLUS),
                                                /* No line break */ b.sequence(SPACING_NO_LB, NEXT_NOT_LB,
                                                                DOUBLE_MINUS))));

                // New expressions
                b.rule(FULL_NEW_EXPR).is(NEW, b.firstOf(FULL_NEW_SUB_EXPR, VECTOR_LITERAL_EXPRESSION), ARGUMENTS);
                b.rule(FULL_NEW_SUB_EXPR).is(b.firstOf(
                                PRIMARY_EXPR,
                                b.sequence(FULL_NEW_EXPR, PROPERTY_OPERATOR),
                                FULL_NEW_EXPR,
                                b.sequence(SUPER_EXPR, PROPERTY_OPERATOR)));

                b.rule(SHORT_NEW_EXPR).is(NEW, b.firstOf(SHORT_NEW_SUB_EXPR, VECTOR_LITERAL_EXPRESSION));
                b.rule(SHORT_NEW_SUB_EXPR).is(b.firstOf(
                                FULL_NEW_SUB_EXPR,
                                SHORT_NEW_EXPR));

                // Property accessors
                b.rule(PROPERTY_OPERATOR).is(b.firstOf(
                                b.sequence(DOT, QUALIFIED_IDENTIFIER),
                                // not in specs:
                                TYPE_APPLICATION,
                                BRACKETS));
                b.rule(BRACKETS).is(LBRAKET, LIST_EXPRESSION, RBRAKET);

                // Query operators
                b.rule(QUERY_OPERATOR).is(b.firstOf(
                                b.sequence(DOUBLE_DOT, QUALIFIED_IDENTIFIER),
                                b.sequence(DOT, LPARENTHESIS, LIST_EXPRESSION, RPARENTHESIS)));

                // Call expresions
                b.rule(ARGUMENTS).is(LPARENTHESIS, b.optional(LIST_EXPRESSION), RPARENTHESIS);

                // Unary expression
                b.rule(UNARY_EXPR).is(b.firstOf(
                                b.sequence(b.firstOf(DELETE, DOUBLE_PLUS, DOUBLE_MINUS), POSTFIX_EXPR),
                                b.sequence(b.firstOf(VOID, TYPEOF, PLUS, MINUS, NOT, TILD), UNARY_EXPR),
                                POSTFIX_EXPR)).skipIfOneChild();

                // Binary expressions
                b.rule(MULTIPLICATIVE_EXPR).is(UNARY_EXPR, b.zeroOrMore(b.firstOf(STAR, DIV, MOD), UNARY_EXPR))
                                .skipIfOneChild();
                b.rule(ADDITIVE_EXPR).is(MULTIPLICATIVE_EXPR, b.zeroOrMore(ADDITIVE_OPERATOR, MULTIPLICATIVE_EXPR))
                                .skipIfOneChild();
                b.rule(ADDITIVE_OPERATOR).is(b.firstOf(PLUS, MINUS, /* Action Script 2: */ word(b, "add")));
                b.rule(SHIFT_EXPR).is(ADDITIVE_EXPR, b.zeroOrMore(b.firstOf(SL, SR2, SR), ADDITIVE_EXPR))
                                .skipIfOneChild();

                b.rule(RELATIONAL_EXPR).is(SHIFT_EXPR, b.zeroOrMore(RELATIONAL_OPERATOR, SHIFT_EXPR)).skipIfOneChild();
                b.rule(RELATIONAL_EXPR_NO_IN).is(SHIFT_EXPR, b.zeroOrMore(RELATIONAL_OPERATOR_NO_IN, SHIFT_EXPR))
                                .skipIfOneChild();
                b.rule(RELATIONAL_OPERATOR).is(b.firstOf(LE, GE, LT, GT, IN, INSTANCEOF, IS, AS,
                                /* Action Script 2: */ word(b, "le"), word(b, "ge"), word(b, "lt"), word(b, "gt")));
                b.rule(RELATIONAL_OPERATOR_NO_IN).is(b.firstOf(LE, GE, LT, GT, INSTANCEOF, IS, AS,
                                /* Action Script 2: */ word(b, "le"), word(b, "ge"), word(b, "lt"), word(b, "gt")));

                b.rule(EQUALITY_EXPR).is(RELATIONAL_EXPR, b.zeroOrMore(EQUALITY_OPERATOR, RELATIONAL_EXPR))
                                .skipIfOneChild();
                b.rule(EQUALITY_EXPR_NO_IN)
                                .is(RELATIONAL_EXPR_NO_IN, b.zeroOrMore(EQUALITY_OPERATOR, RELATIONAL_EXPR_NO_IN))
                                .skipIfOneChild();
                b.rule(EQUALITY_OPERATOR).is(b.firstOf(
                                NOTEQUAL2,
                                EQUAL3,
                                EQUAL2,
                                NOTEQUAL1,
                                /* ActionScript 2: */
                                b.sequence(SPACING, "<>"),
                                word(b, "eq"),
                                word(b, "ne")));

                b.rule(BITEWISE_AND_EXPR).is(EQUALITY_EXPR, b.zeroOrMore(AND, EQUALITY_EXPR)).skipIfOneChild();
                b.rule(BITEWISE_AND_EXPR_NO_IN).is(EQUALITY_EXPR_NO_IN, b.zeroOrMore(AND, EQUALITY_EXPR_NO_IN))
                                .skipIfOneChild();

                b.rule(BITEWISE_XOR_EXPR).is(BITEWISE_AND_EXPR, b.zeroOrMore(XOR, BITEWISE_AND_EXPR)).skipIfOneChild();
                b.rule(BITEWISE_XOR_EXPR_NO_IN).is(BITEWISE_AND_EXPR_NO_IN, b.zeroOrMore(XOR, BITEWISE_AND_EXPR_NO_IN))
                                .skipIfOneChild();

                b.rule(BITEWISE_OR_EXPR).is(BITEWISE_XOR_EXPR, b.zeroOrMore(OR, BITEWISE_XOR_EXPR)).skipIfOneChild();
                b.rule(BITEWISE_OR_EXPR_NO_IN).is(BITEWISE_XOR_EXPR_NO_IN, b.zeroOrMore(OR, BITEWISE_XOR_EXPR_NO_IN))
                                .skipIfOneChild();

                b.rule(LOGICAL_AND_EXPR).is(BITEWISE_OR_EXPR, b.zeroOrMore(LOGICAL_AND_OPERATOR, BITEWISE_XOR_EXPR))
                                .skipIfOneChild();
                b.rule(LOGICAL_AND_EXPR_NO_IN)
                                .is(BITEWISE_OR_EXPR_NO_IN, b.zeroOrMore(LOGICAL_AND_OPERATOR, BITEWISE_XOR_EXPR_NO_IN))
                                .skipIfOneChild();
                b.rule(LOGICAL_AND_OPERATOR).is(b.firstOf(
                                ANDAND,
                                /* ActionScript 2: */
                                b.sequence(SPACING, "and", b.nextNot(IDENTIFIER_PART))));

                b.rule(LOGICAL_OR_EXPR).is(LOGICAL_AND_EXPR, b.zeroOrMore(LOGICAL_OR_OPERATOR, LOGICAL_AND_EXPR))
                                .skipIfOneChild();
                b.rule(LOGICAL_OR_EXPR_NO_IN)
                                .is(LOGICAL_AND_EXPR_NO_IN, b.zeroOrMore(LOGICAL_OR_OPERATOR, LOGICAL_AND_EXPR_NO_IN))
                                .skipIfOneChild();
                b.rule(LOGICAL_OR_OPERATOR).is(b.firstOf(
                                OROR,
                                /* ActionScript 2: */
                                b.sequence(SPACING, "or", b.nextNot(IDENTIFIER_PART))));

                // Conditional expression
                b.rule(CONDITIONAL_EXPR).is(LOGICAL_OR_EXPR, b.optional(QUERY, ASSIGNMENT_EXPR, COLON, ASSIGNMENT_EXPR))
                                .skipIfOneChild();
                b.rule(CONDITIONAL_EXPR_NO_IN)
                                .is(LOGICAL_OR_EXPR_NO_IN,
                                                b.optional(QUERY, ASSIGNMENT_EXPR_NO_IN, COLON, ASSIGNMENT_EXPR_NO_IN))
                                .skipIfOneChild();

                // Non assignment expression
                b.rule(NON_ASSIGNMENT_EXPR)
                                .is(LOGICAL_OR_EXPR, b.optional(QUERY, NON_ASSIGNMENT_EXPR, COLON, NON_ASSIGNMENT_EXPR))
                                .skipIfOneChild();
                b.rule(NON_ASSIGNMENT_EXPR_NO_IN)
                                .is(LOGICAL_OR_EXPR_NO_IN,
                                                b.optional(QUERY, NON_ASSIGNMENT_EXPR_NO_IN, COLON,
                                                                NON_ASSIGNMENT_EXPR_NO_IN))
                                .skipIfOneChild();

                b.rule(LIST_EXPRESSION).is(ASSIGNMENT_EXPR, b.zeroOrMore(b.sequence(COMMA, ASSIGNMENT_EXPR)));
                b.rule(LIST_EXPRESSION_NO_IN).is(ASSIGNMENT_EXPR_NO_IN,
                                b.zeroOrMore(b.sequence(COMMA, ASSIGNMENT_EXPR_NO_IN)));

                b.rule(TYPE_EXPR).is(b.firstOf(
                                STAR,
                                b.sequence(/* Godin: not sure about QUALIFIED_IDENTIFIER, but it works: */QUALIFIED_IDENTIFIER,
                                                b.zeroOrMore(DOT, QUALIFIED_IDENTIFIER),
                                                b.optional(TYPE_APPLICATION))));
                b.rule(TYPE_APPLICATION).is(DOT, LT, TYPE_EXPRESSION_LIST, GT);
                b.rule(TYPE_EXPR_NO_IN).is(TYPE_EXPR);

                b.rule(VECTOR_LITERAL_EXPRESSION).is(LT, TYPE_EXPR, GT, BRACKETS);
        }

        private static void statements(LexerlessGrammarBuilder b) {
                b.rule(STATEMENT).is(b.firstOf(
                                C_DECLARATION,
                                METADATA_STATEMENT,
                                SUPER_STATEMENT,
                                BLOCK,
                                IF_STATEMENT,
                                SWITCH_STATEMENT,
                                DO_STATEMENT,
                                WHILE_STATEMENT,
                                FOR_STATEMENT,
                                WITH_STATEMENT,
                                CONTINUE_STATEMENT,
                                BREAK_STATEMENT,
                                RETURN_STATEMENT,
                                THROW_STATEMENT,
                                TRY_STATEMENT,
                                EXPRESSION_STATEMENT,
                                LABELED_STATEMENT));

                b.rule(SUB_STATEMENT).is(b.firstOf(
                                EMPTY_STATEMENT,
                                STATEMENT,
                                VARIABLE_DECLARATION_STATEMENT));

                b.rule(EXPRESSION_STATEMENT).is(EXPRESSION, EOS);

                // Not in spec:
                b.rule(METADATA_STATEMENT).is(LBRAKET, ASSIGNMENT_EXPR, RBRAKET);

                b.rule(VARIABLE_DECLARATION_STATEMENT).is(VARIABLE_DEF, EOS);

                b.rule(EMPTY_STATEMENT).is(SEMICOLON);

                b.rule(SUPER_STATEMENT).is(SUPER, ARGUMENTS, EOS);

                b.rule(BLOCK).is(LCURLYBRACE, DIRECTIVES, RCURLYBRACE);

                b.rule(LABELED_STATEMENT).is(IDENTIFIER, COLON, SUB_STATEMENT);

                b.rule(IF_STATEMENT).is(IF, PARENTHESIZED_LIST_EXPR, SUB_STATEMENT, b.optional(ELSE, SUB_STATEMENT));

                b.rule(SWITCH_STATEMENT).is(SWITCH, PARENTHESIZED_LIST_EXPR, LCURLYBRACE, b.zeroOrMore(CASE_ELEMENT),
                                RCURLYBRACE);
                b.rule(CASE_ELEMENT).is(b.oneOrMore(CASE_LABEL), b.zeroOrMore(DIRECTIVE));
                b.rule(CASE_LABEL).is(b.firstOf(DEFAULT, b.sequence(CASE, LIST_EXPRESSION)), COLON);

                b.rule(DO_STATEMENT).is(DO, SUB_STATEMENT, WHILE, PARENTHESIZED_LIST_EXPR, EOS);

                b.rule(WHILE_STATEMENT).is(WHILE, PARENTHESIZED_LIST_EXPR, SUB_STATEMENT);

                b.rule(FOR_STATEMENT).is(b.firstOf(
                                b.sequence(FOR, LPARENTHESIS, b.optional(FOR_INITIALISER), SEMICOLON,
                                                b.optional(LIST_EXPRESSION),
                                                SEMICOLON,
                                                b.optional(LIST_EXPRESSION), RPARENTHESIS, SUB_STATEMENT),
                                b.sequence(FOR, LPARENTHESIS, FOR_IN_BINDING, IN, LIST_EXPRESSION, RPARENTHESIS,
                                                SUB_STATEMENT),
                                b.sequence(FOR,
                                                b.sequence(/* No line break */ SPACING_NO_LB, NEXT_NOT_LB, EACH,
                                                                LPARENTHESIS, FOR_IN_BINDING,
                                                                IN, LIST_EXPRESSION, RPARENTHESIS, SUB_STATEMENT))));
                b.rule(FOR_INITIALISER).is(b.firstOf(LIST_EXPRESSION_NO_IN, VARIABLE_DEF_NO_IN));
                b.rule(FOR_IN_BINDING).is(b.firstOf(
                                b.sequence(VARIABLE_DEF_KIND, VARIABLE_BINDING_NO_IN),
                                POSTFIX_EXPR));

                b.rule(CONTINUE_STATEMENT).is(CONTINUE, b.firstOf(
                                b.sequence(/* No line break */ SPACING_NO_LB, NEXT_NOT_LB, IDENTIFIER, EOS),
                                EOS_NO_LB));

                b.rule(BREAK_STATEMENT).is(BREAK, b.firstOf(
                                b.sequence(/* No line break */ SPACING_NO_LB, NEXT_NOT_LB, IDENTIFIER, EOS),
                                EOS_NO_LB));

                b.rule(WITH_STATEMENT).is(WITH, PARENTHESIZED_LIST_EXPR, SUB_STATEMENT);

                b.rule(RETURN_STATEMENT).is(RETURN, b.firstOf(
                                b.sequence(/* No line break */ SPACING_NO_LB, NEXT_NOT_LB, LIST_EXPRESSION, EOS),
                                EOS_NO_LB));

                b.rule(THROW_STATEMENT).is(THROW, b.firstOf(
                                b.sequence(/* No line break */ SPACING_NO_LB, NEXT_NOT_LB, LIST_EXPRESSION, EOS),
                                EOS_NO_LB));

                b.rule(TRY_STATEMENT).is(TRY, BLOCK, b.firstOf(
                                b.sequence(CATCH_CLAUSES, b.optional(FINALLY, BLOCK)),
                                b.sequence(FINALLY, BLOCK)));
                b.rule(CATCH_CLAUSES).is(CATCH_CLAUSE, b.zeroOrMore(CATCH_CLAUSE));
                b.rule(CATCH_CLAUSE).is(CATCH, LPARENTHESIS, PARAMETER, RPARENTHESIS, BLOCK);

                b.rule(EXPRESSION).is(b.nextNot(b.firstOf(FUNCTION, LCURLYBRACE)), LIST_EXPRESSION);
        }

        private static void directives(LexerlessGrammarBuilder b) {
                b.rule(DIRECTIVE).is(b.firstOf(
                                C_FUNCTION_DEFINITION,
                                C_DECLARATION,
                                CONFIG_CONDITION,
                                EMPTY_STATEMENT,
                                ANNOTABLE_DIRECTIVE,
                                STATEMENT,
                                DEFAULT_XML_NAMESPACE_DIRECTIVE,
                                b.sequence(ATTRIBUTES, /* No line break */ SPACING_NO_LB, NEXT_NOT_LB,
                                                ANNOTABLE_DIRECTIVE),
                                b.sequence(INCLUDE_DIRECTIVE, /* No line break */ EOS_NO_LB),
                                b.sequence(IMPORT_DIRECTIVE, /* No line break */ EOS_NO_LB),
                                b.sequence(USE_DIRECTIVE, /* No line break */ EOS_NO_LB)));

                b.rule(CONFIG_CONDITION).is(IDENTIFIER, DOUBLE_COLON, IDENTIFIER, LCURLYBRACE, DIRECTIVES, RCURLYBRACE);

                b.rule(ANNOTABLE_DIRECTIVE).is(b.firstOf(
                                VARIABLE_DECLARATION_STATEMENT,
                                FUNCTION_DEF,
                                CLASS_DEF,
                                INTERFACE_DEF,
                                NAMESPACE_DEF));

                b.rule(DIRECTIVES).is(b.zeroOrMore(DIRECTIVE));

                b.rule(ATTRIBUTES).is(b.oneOrMore(ATTRIBUTE));
                b.rule(ATTRIBUTE_COMBINATION).is(ATTRIBUTE, /* No line break */ SPACING_NO_LB, NEXT_NOT_LB, ATTRIBUTES);
                b.rule(ATTRIBUTE).is(b.firstOf(
                                b.sequence(/* hack: */b.nextNot(NAMESPACE), ATTRIBUTE_EXPR),
                                RESERVED_NAMESPACE,
                                b.sequence(LBRAKET, ASSIGNMENT_EXPR, RBRAKET)));
                b.rule(ATTRIBUTE_EXPR).is(IDENTIFIER, b.zeroOrMore(PROPERTY_OPERATOR));

                b.rule(IMPORT_DIRECTIVE).is(IMPORT, PACKAGE_NAME, b.optional(DOT, STAR));

                b.rule(INCLUDE_DIRECTIVE).is(INCLUDE, /* No line break */ SPACING_NO_LB, NEXT_NOT_LB, STRING);

                b.rule(USE_DIRECTIVE).is(USE, NAMESPACE, LIST_EXPRESSION);

                b.rule(DEFAULT_XML_NAMESPACE_DIRECTIVE).is(DEFAULT, /* No line break */ SPACING_NO_LB, NEXT_NOT_LB, XML,
                                /* No line break */ SPACING_NO_LB, NEXT_NOT_LB, NAMESPACE, EQUAL1, NON_ASSIGNMENT_EXPR,
                                EOS);
        }

        private static void definitions(LexerlessGrammarBuilder b) {
                b.rule(VARIABLE_DEF).is(VARIABLE_DEF_KIND, VARIABLE_BINDING_LIST);
                b.rule(VARIABLE_DEF_NO_IN).is(VARIABLE_DEF_KIND, VARIABLE_BINDING_LIST_NO_IN);

                b.rule(VARIABLE_DEF_KIND).is(b.firstOf(VAR, CONST));

                b.rule(VARIABLE_BINDING_LIST).is(VARIABLE_BINDING, b.zeroOrMore(COMMA, VARIABLE_BINDING));
                b.rule(VARIABLE_BINDING_LIST_NO_IN).is(VARIABLE_BINDING_NO_IN,
                                b.zeroOrMore(COMMA, VARIABLE_BINDING_NO_IN));

                b.rule(VARIABLE_BINDING).is(TYPED_IDENTIFIER, b.optional(VARIABLE_INITIALISATION));
                b.rule(VARIABLE_BINDING_NO_IN).is(TYPED_IDENTIFIER_NO_IN, b.optional(VARIABLE_INITIALISATION_NO_IN));

                b.rule(VARIABLE_INITIALISATION).is(EQUAL1, VARIABLE_INITIALISER);
                b.rule(VARIABLE_INITIALISATION_NO_IN).is(EQUAL1, VARIABLE_INITIALISER_NO_IN);

                b.rule(VARIABLE_INITIALISER).is(b.firstOf(
                                ASSIGNMENT_EXPR,
                                ATTRIBUTE_COMBINATION));
                b.rule(VARIABLE_INITIALISER_NO_IN).is(b.firstOf(
                                ASSIGNMENT_EXPR_NO_IN,
                                ATTRIBUTE_COMBINATION));

                b.rule(TYPED_IDENTIFIER).is(b.firstOf(
                                b.sequence(IDENTIFIER, COLON, TYPE_EXPR),
                                IDENTIFIER));
                b.rule(TYPED_IDENTIFIER_NO_IN).is(b.firstOf(
                                b.sequence(IDENTIFIER, COLON, TYPE_EXPR_NO_IN),
                                IDENTIFIER));

                b.rule(FUNCTION_DEF).is(FUNCTION, FUNCTION_NAME, FUNCTION_COMMON);
                b.rule(FUNCTION_NAME).is(b.firstOf(
                                b.sequence(GET, /* No line break */ SPACING_NO_LB, NEXT_NOT_LB, IDENTIFIER),
                                b.sequence(SET, /* No line break */ SPACING_NO_LB, NEXT_NOT_LB, IDENTIFIER),
                                IDENTIFIER));

                b.rule(FUNCTION_COMMON).is(b.firstOf(
                                b.sequence(FUNCTION_SIGNATURE, BLOCK),
                                b.sequence(FUNCTION_SIGNATURE, EOS)));

                b.rule(FUNCTION_SIGNATURE)
                                .is(b.sequence(LPARENTHESIS, b.optional(PARAMETERS), RPARENTHESIS,
                                                b.optional(RESULT_TYPE)));

                b.rule(PARAMETERS).is(b.firstOf(
                                b.sequence(PARAMETER, b.zeroOrMore(COMMA, PARAMETER),
                                                b.optional(COMMA, REST_PARAMETERS)),
                                REST_PARAMETERS));

                b.rule(PARAMETER).is(b.firstOf(
                                b.sequence(TYPED_IDENTIFIER, EQUAL1, ASSIGNMENT_EXPR),
                                TYPED_IDENTIFIER));

                b.rule(REST_PARAMETERS).is(b.firstOf(
                                b.sequence(TRIPLE_DOTS, TYPED_IDENTIFIER),
                                TRIPLE_DOTS));

                b.rule(RESULT_TYPE).is(COLON, b.firstOf(VOID, TYPE_EXPR));

                b.rule(CLASS_DEF).is(CLASS, CLASS_NAME, b.optional(INHERITENCE), BLOCK);
                b.rule(CLASS_NAME).is(CLASS_IDENTIFIERS);
                b.rule(CLASS_IDENTIFIERS).is(IDENTIFIER, b.zeroOrMore(b.sequence(DOT, IDENTIFIER)));
                b.rule(INHERITENCE).is(b.firstOf(
                                b.sequence(IMPLEMENTS, TYPE_EXPRESSION_LIST),
                                b.sequence(EXTENDS, TYPE_EXPR, IMPLEMENTS, TYPE_EXPRESSION_LIST),
                                b.sequence(EXTENDS, TYPE_EXPR)));

                b.rule(TYPE_EXPRESSION_LIST).is(TYPE_EXPR, b.zeroOrMore(b.sequence(COMMA, TYPE_EXPR)));

                b.rule(INTERFACE_DEF).is(INTERFACE, CLASS_NAME, b.optional(EXTENDS_LIST), BLOCK);
                b.rule(EXTENDS_LIST).is(EXTENDS, TYPE_EXPRESSION_LIST);

                b.rule(PACKAGE_DEF).is(PACKAGE, b.optional(PACKAGE_NAME), BLOCK);
                b.rule(PACKAGE_NAME).is(IDENTIFIER, b.zeroOrMore(DOT, IDENTIFIER));

                b.rule(NAMESPACE_DEF).is(NAMESPACE, NAMESPACE_BINDING, EOS);
                b.rule(NAMESPACE_BINDING).is(IDENTIFIER, b.optional(NAMESPACE_INITIALISATION));
                b.rule(NAMESPACE_INITIALISATION).is(EQUAL1, ASSIGNMENT_EXPR);

                b.rule(PROGRAM).is(
                                b.firstOf(
                                                b.sequence(PACKAGE_DEF, PROGRAM),
                                                DIRECTIVES),
                                SPACING,
                                b.token(GenericTokenType.EOF, b.endOfInput()));
        }

        private static void xml(LexerlessGrammarBuilder b) {
                b.rule(XML_INITIALISER).is(b.firstOf(
                                XML_MARKUP,
                                XML_ELEMENT,
                                b.sequence(LT, GT, XML_ELEMENT_CONTENT, LT, DIV, GT)));

                b.rule(XML_ELEMENT).is(b.firstOf(
                                b.sequence(LT, XML_TAG_CONTENT, b.optional(XML_WHITESPACE), DIV, GT),
                                b.sequence(LT, XML_TAG_CONTENT, b.optional(XML_WHITESPACE), XML_ELEMENT_CONTENT, LT,
                                                DIV, XML_TAG_NAME,
                                                b.optional(XML_WHITESPACE), GT)));

                b.rule(XML_TAG_CONTENT).is(XML_TAG_NAME, XML_ATTRIBUTES);

                b.rule(XML_TAG_NAME).is(b.firstOf(
                                b.sequence(LCURLYBRACE, EXPRESSION, RCURLYBRACE),
                                XML_NAME));

                b.rule(XML_ATTRIBUTES).is(b.optional(b.firstOf(
                                b.sequence(XML_ATTRIBUTE, XML_ATTRIBUTES),
                                b.sequence(XML_WHITESPACE, LCURLYBRACE, EXPRESSION, RCURLYBRACE))));

                b.rule(XML_ATTRIBUTE).is(b.firstOf(
                                b.sequence(b.zeroOrMore(XML_WHITESPACE), XML_NAME,
                                                b.zeroOrMore(XML_WHITESPACE), EQUAL1, b.zeroOrMore(XML_WHITESPACE),
                                                LCURLYBRACE, EXPRESSION, RCURLYBRACE),
                                b.sequence(b.zeroOrMore(XML_WHITESPACE), XML_NAME,
                                                b.zeroOrMore(XML_WHITESPACE), EQUAL1, b.zeroOrMore(XML_WHITESPACE),
                                                XML_ATTRIBUTE_VALUE)));

                b.rule(XML_ELEMENT_CONTENT).is(b.optional(
                                b.firstOf(
                                                b.sequence(LCURLYBRACE, EXPRESSION, RCURLYBRACE, XML_ELEMENT_CONTENT),
                                                b.sequence(XML_MARKUP, XML_ELEMENT_CONTENT),
                                                b.sequence(XML_TEXT, XML_ELEMENT_CONTENT),
                                                b.sequence(XML_ELEMENT, XML_ELEMENT_CONTENT))));

                b.rule(XML_MARKUP).is(b.firstOf(
                                XML_COMMENT,
                                XML_CDATA,
                                XML_PI));

                b.rule(XML_COMMENT).is(SPACING, b.regexp("<!--(?:(?!--)[\\s\\S])*?-->"));
                b.rule(XML_CDATA).is(SPACING, b.regexp("<!\\[CDATA\\[(?:(?!]])[\\s\\S])*?]]>"));
                b.rule(XML_PI).is(SPACING, b.regexp("<\\?(?:(?!\\?>)[\\s\\S])*?\\?>"));
                b.rule(XML_TEXT).is(SPACING, b.regexp("[^{<]++"));
                b.rule(XML_NAME).is(SPACING,
                                b.regexp("[" + UNICODE_LETTER + "_:" + "]" + "[" + UNICODE_LETTER + UNICODE_DIGIT
                                                + "\\.\\-_:" + "]*"));
                b.rule(XML_ATTRIBUTE_VALUE).is(b.regexp("(\"([^\"]*[//s//S]*)\")|(\'([^\']*[//s//S]*)\')"));
                b.rule(XML_WHITESPACE).is(b.regexp("[ \\t\\r\\n]+"));
        }

        private static void keywords(LexerlessGrammarBuilder b) {
                for (CKeyword k : CKeyword.values()) {
                        b.rule(k).is(SPACING, k.getValue(), b.nextNot(IDENTIFIER_PART));
                }

                List<CKeyword> keywords = CKeyword.keywords();
                Object[] rest = new Object[keywords.size() - 2];
                for (int i = 2; i < keywords.size(); i++) {
                        rest[i - 2] = keywords.get(i);
                }
                b.rule(KEYWORDS).is(b.firstOf(keywords.get(0), keywords.get(1), rest));
        }

        private static void punctuators(LexerlessGrammarBuilder b) {
                for (CPunctuator p : CPunctuator.values()) {
                        b.rule(p).is(SPACING, p.getValue());
                }
        }

        private static Object word(LexerlessGrammarBuilder b, String word) {
                return b.sequence(SPACING, word, b.nextNot(IDENTIFIER_PART));
        }

}
