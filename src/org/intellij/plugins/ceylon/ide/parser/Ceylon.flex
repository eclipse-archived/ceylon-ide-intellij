package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;

%%

%class CeylonFlexLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
%eof}
//%debug

%xstate MCOMMENT, IN_STRING

%{
    int multiCommentLevel = 0;
%}

Digits = {Digit} ("_" | {Digit})*
HexDigits = {HexDigit} ("_" | {HexDigit})*
BinaryDigits = {BinaryDigit} ("_" | {BinaryDigit})*
Exponent = ( "e" | "E" ) ( "+" | "-" )? {Digit}*
Magnitude = "k" | "M" | "G" | "T" | "P"
FractionalMagnitude = "m" | "u" | "n" | "p" | "f"
//EMPTY// FLOAT_LITERAL =
//distinguish a float literal from
//a natural literals followed by a
//member invocation or range op
// todo: do something about this syntactic predicate
NATURAL_LITERAL = {Digits} ( /* ("." ("0".."9")) => */ "." {Digits} ({Exponent}|{Magnitude}|{FractionalMagnitude})? | {FractionalMagnitude} | {Magnitude}? ) | "#" {HexDigits} | "$" {BinaryDigits}
//EMPTY// ASTRING_LITERAL =
//EMPTY// AVERBATIM_STRING =
CHAR_LITERAL = "'" {CharPart} "'"?
VERBATIM_STRING =	"\"\"\"" ([^\"] | "\"" [^\"] | "\"\"" [^\"])* ("\"" ("\"" ("\"" ("\"" "\""?)?)?)?)?
CharPart = ( [^\\'] | {EscapeSequence} )*
EscapeSequence = "\\" ( [^{] | "{" ([^}])* "}"? )
WS = ( " " | "\r" | "\t" | "\f" | "\n" )+
LINE_COMMENT = ("//"|"#!") [^\n\r]* ("\r\n" | "\r" | "\n")?
BACKTICK = "`"
ASSEMBLY = "assembly"
ASSERT = "assert"
ABSTRACTED_TYPE = "abstracts"
ASSIGN = "assign"
ALIAS = "alias"
BREAK = "break"
CASE_CLAUSE = "case"
CATCH_CLAUSE = "catch"
CLASS_DEFINITION = "class"
CONTINUE = "continue"
DYNAMIC = "dynamic"
ELSE_CLAUSE = "else"
EXISTS = "exists"
EXTENDS = "extends"
FINALLY_CLAUSE = "finally"
FOR_CLAUSE = "for"
TYPE_CONSTRAINT = "given"
IF_CLAUSE = "if"
SATISFIES = "satisfies"
IMPORT = "import"
INTERFACE_DEFINITION = "interface"
VALUE_MODIFIER = "value"
FUNCTION_MODIFIER = "function"
LET = "let"
MODULE = "module"
NEW = "new"
PACKAGE = "package"
NONEMPTY = "nonempty"
RETURN = "return"
SUPER = "super"
SWITCH_CLAUSE = "switch"
THEN_CLAUSE = "then"
THIS = "this"
OUTER = "outer"
OBJECT_DEFINITION = "object"
CASE_TYPES = "of"
OUT = "out"
THROW = "throw"
TRY_CLAUSE = "try"
VOID_MODIFIER = "void"
WHILE_CLAUSE = "while"
ELLIPSIS = "..."
RANGE_OP = ".."
SEGMENT_OP = ":"
MEMBER_OP = "."
LPAREN = "("
RPAREN = ")"
LBRACE = "{"
RBRACE = "}"
LBRACKET = "["
RBRACKET = "]"
SEMICOLON = ";"
COMMA = ","
SPECIFY = "="
COMPUTE = "=>"
SAFE_MEMBER_OP = "?."
OPTIONAL = "?"
NOT_OP = "!"
COMPLEMENT_OP = "~"
EQUAL_OP = "=="
IDENTICAL_OP = "==="
AND_OP = "&&"
OR_OP = "||"
INCREMENT_OP = "++"
DECREMENT_OP = "--"
SUM_OP = "+"
DIFFERENCE_OP = "-"
SPREAD_OP = "*."
SCALE_OP = "**"
PRODUCT_OP = "*"
QUOTIENT_OP = "/"
INTERSECTION_OP = "&"
UNION_OP = "|"
REMAINDER_OP = "%"
NOT_EQUAL_OP = "!="
LARGER_OP = ">"
SMALLER_OP = "<"
LARGE_AS_OP = ">="
SMALL_AS_OP = "<="
ENTRY_OP = "->"
COMPARE_OP = "<=>"
IN_OP = "in"
IS_OP = "is"
POWER_OP = "^"
ADD_SPECIFY = "+="
SUBTRACT_SPECIFY = "-="
MULTIPLY_SPECIFY = "*="
DIVIDE_SPECIFY = "/="
INTERSECT_SPECIFY = "&="
UNION_SPECIFY = "|="
COMPLEMENT_SPECIFY = "~="
REMAINDER_SPECIFY = "%="
AND_SPECIFY = "&&="
OR_SPECIFY = "||="
COMPILER_ANNOTATION = "@"
//EMPTY// PIDENTIFIER =
//EMPTY// AIDENTIFIER =
LIDENTIFIER = "_"? [a-z] {IdentifierPart}* | {LIdentifierPrefix} {IdentifierPart}+
UIDENTIFIER = "_"? [A-Z] {IdentifierPart}* | {UIdentifierPrefix} {IdentifierPart}+
LIdentifierPrefix = "\\i"
UIdentifierPrefix = "\\I"
IdentifierPart = "_" | {Digit} | {Letter}
Letter = [a-z] | [A-Z] | [\u00c0-\ufffe]
Digit = [0-9]
HexDigit = [0-9] | [A-F] | [a-f]
BinaryDigit = [01]

%%

{ABSTRACTED_TYPE} { return CeylonTokens.ABSTRACTED_TYPE; }
{ADD_SPECIFY} { return CeylonTokens.ADD_SPECIFY; }
//{AIDENTIFIER} { return CeylonTokens.AIDENTIFIER; }
{ALIAS} { return CeylonTokens.ALIAS; }
{AND_OP} { return CeylonTokens.AND_OP; }
{AND_SPECIFY} { return CeylonTokens.AND_SPECIFY; }
{ASSEMBLY} { return CeylonTokens.ASSEMBLY; }
{ASSERT} { return CeylonTokens.ASSERT; }
{ASSIGN} { return CeylonTokens.ASSIGN; }
//{ASTRING_LITERAL} { return CeylonTokens.ASTRING_LITERAL; }
//{AVERBATIM_STRING} { return CeylonTokens.AVERBATIM_STRING; }
<YYINITIAL> {
    {BACKTICK} { return CeylonTokens.BACKTICK; }
}
{BREAK} { return CeylonTokens.BREAK; }
//{BinaryDigit} { return CeylonTokens.BinaryDigit; }
//{BinaryDigits} { return CeylonTokens.BinaryDigits; }
{CASE_CLAUSE} { return CeylonTokens.CASE_CLAUSE; }
{CASE_TYPES} { return CeylonTokens.CASE_TYPES; }
{CATCH_CLAUSE} { return CeylonTokens.CATCH_CLAUSE; }
{CHAR_LITERAL} { return CeylonTokens.CHAR_LITERAL; }
{CLASS_DEFINITION} { return CeylonTokens.CLASS_DEFINITION; }
{COMMA} { return CeylonTokens.COMMA; }
{COMPARE_OP} { return CeylonTokens.COMPARE_OP; }
{COMPILER_ANNOTATION} { return CeylonTokens.COMPILER_ANNOTATION; }
{COMPLEMENT_OP} { return CeylonTokens.COMPLEMENT_OP; }
{COMPLEMENT_SPECIFY} { return CeylonTokens.COMPLEMENT_SPECIFY; }
{COMPUTE} { return CeylonTokens.COMPUTE; }
{CONTINUE} { return CeylonTokens.CONTINUE; }
//{CharPart} { return CeylonTokens.CharPart; }
{DECREMENT_OP} { return CeylonTokens.DECREMENT_OP; }
{DIFFERENCE_OP} { return CeylonTokens.DIFFERENCE_OP; }
{DIVIDE_SPECIFY} { return CeylonTokens.DIVIDE_SPECIFY; }
{DYNAMIC} { return CeylonTokens.DYNAMIC; }
//{Digit} { return CeylonTokens.Digit; }
//{Digits} { return CeylonTokens.Digits; }
{ELLIPSIS} { return CeylonTokens.ELLIPSIS; }
{ELSE_CLAUSE} { return CeylonTokens.ELSE_CLAUSE; }
{ENTRY_OP} { return CeylonTokens.ENTRY_OP; }
{EQUAL_OP} { return CeylonTokens.EQUAL_OP; }
{EXISTS} { return CeylonTokens.EXISTS; }
{EXTENDS} { return CeylonTokens.EXTENDS; }
//{EscapeSequence} { return CeylonTokens.EscapeSequence; }
//{Exponent} { return CeylonTokens.Exponent; }
{FINALLY_CLAUSE} { return CeylonTokens.FINALLY_CLAUSE; }
//{FLOAT_LITERAL} { return CeylonTokens.FLOAT_LITERAL; }
{FOR_CLAUSE} { return CeylonTokens.FOR_CLAUSE; }
{FUNCTION_MODIFIER} { return CeylonTokens.FUNCTION_MODIFIER; }
//{FractionalMagnitude} { return CeylonTokens.FractionalMagnitude; }
//{HexDigit} { return CeylonTokens.HexDigit; }
//{HexDigits} { return CeylonTokens.HexDigits; }
{IDENTICAL_OP} { return CeylonTokens.IDENTICAL_OP; }
{IF_CLAUSE} { return CeylonTokens.IF_CLAUSE; }
{IMPORT} { return CeylonTokens.IMPORT; }
{INCREMENT_OP} { return CeylonTokens.INCREMENT_OP; }
{INTERFACE_DEFINITION} { return CeylonTokens.INTERFACE_DEFINITION; }
{INTERSECTION_OP} { return CeylonTokens.INTERSECTION_OP; }
{INTERSECT_SPECIFY} { return CeylonTokens.INTERSECT_SPECIFY; }
{IN_OP} { return CeylonTokens.IN_OP; }
{IS_OP} { return CeylonTokens.IS_OP; }
//{IdentifierPart} { return CeylonTokens.IdentifierPart; }
//{IdentifierStart} { return CeylonTokens.IdentifierStart; }
{LARGER_OP} { return CeylonTokens.LARGER_OP; }
{LARGE_AS_OP} { return CeylonTokens.LARGE_AS_OP; }
{LBRACE} { return CeylonTokens.LBRACE; }
{LBRACKET} { return CeylonTokens.LBRACKET; }
{LET} { return CeylonTokens.LET; }
{LINE_COMMENT} { return CeylonTokens.LINE_COMMENT; }
//{LIdentifierPrefix} { return CeylonTokens.LIdentifierPrefix; }
{LPAREN} { return CeylonTokens.LPAREN; }
//{Letter} { return CeylonTokens.Letter; }
{MEMBER_OP} { return CeylonTokens.MEMBER_OP; }
{MODULE} { return CeylonTokens.MODULE; }
{MULTIPLY_SPECIFY} { return CeylonTokens.MULTIPLY_SPECIFY; }
//{Magnitude} { return CeylonTokens.Magnitude; }
{NATURAL_LITERAL} { return CeylonTokens.NATURAL_LITERAL; }
{NEW} { return CeylonTokens.NEW; }
{NONEMPTY} { return CeylonTokens.NONEMPTY; }
{NOT_EQUAL_OP} { return CeylonTokens.NOT_EQUAL_OP; }
{NOT_OP} { return CeylonTokens.NOT_OP; }
{OBJECT_DEFINITION} { return CeylonTokens.OBJECT_DEFINITION; }
{OPTIONAL} { return CeylonTokens.OPTIONAL; }
{OR_OP} { return CeylonTokens.OR_OP; }
{OR_SPECIFY} { return CeylonTokens.OR_SPECIFY; }
{OUT} { return CeylonTokens.OUT; }
{OUTER} { return CeylonTokens.OUTER; }
{PACKAGE} { return CeylonTokens.PACKAGE; }
//{PIDENTIFIER} { return CeylonTokens.PIDENTIFIER; }
{POWER_OP} { return CeylonTokens.POWER_OP; }
{PRODUCT_OP} { return CeylonTokens.PRODUCT_OP; }
{QUOTIENT_OP} { return CeylonTokens.QUOTIENT_OP; }
{RANGE_OP} { return CeylonTokens.RANGE_OP; }
{RBRACE} { return CeylonTokens.RBRACE; }
{RBRACKET} { return CeylonTokens.RBRACKET; }
{REMAINDER_OP} { return CeylonTokens.REMAINDER_OP; }
{REMAINDER_SPECIFY} { return CeylonTokens.REMAINDER_SPECIFY; }
{RETURN} { return CeylonTokens.RETURN; }
{RPAREN} { return CeylonTokens.RPAREN; }
{SAFE_MEMBER_OP} { return CeylonTokens.SAFE_MEMBER_OP; }
{SATISFIES} { return CeylonTokens.SATISFIES; }
{SCALE_OP} { return CeylonTokens.SCALE_OP; }
{SEGMENT_OP} { return CeylonTokens.SEGMENT_OP; }
{SEMICOLON} { return CeylonTokens.SEMICOLON; }
{SMALLER_OP} { return CeylonTokens.SMALLER_OP; }
{SMALL_AS_OP} { return CeylonTokens.SMALL_AS_OP; }
{SPECIFY} { return CeylonTokens.SPECIFY; }
{SPREAD_OP} { return CeylonTokens.SPREAD_OP; }
{SUBTRACT_SPECIFY} { return CeylonTokens.SUBTRACT_SPECIFY; }
{SUM_OP} { return CeylonTokens.SUM_OP; }
{SUPER} { return CeylonTokens.SUPER; }
{SWITCH_CLAUSE} { return CeylonTokens.SWITCH_CLAUSE; }
//{StringPart} { return CeylonTokens.StringPart; }
{THEN_CLAUSE} { return CeylonTokens.THEN_CLAUSE; }
{THIS} { return CeylonTokens.THIS; }
{THROW} { return CeylonTokens.THROW; }
{TRY_CLAUSE} { return CeylonTokens.TRY_CLAUSE; }
{TYPE_CONSTRAINT} { return CeylonTokens.TYPE_CONSTRAINT; }
//{UIdentifierPrefix} { return CeylonTokens.UIdentifierPrefix; }
{UNION_OP} { return CeylonTokens.UNION_OP; }
{UNION_SPECIFY} { return CeylonTokens.UNION_SPECIFY; }
{VALUE_MODIFIER} { return CeylonTokens.VALUE_MODIFIER; }
{VERBATIM_STRING} { return CeylonTokens.VERBATIM_STRING; }
{VOID_MODIFIER} { return CeylonTokens.VOID_MODIFIER; }
{WHILE_CLAUSE} { return CeylonTokens.WHILE_CLAUSE; }
{WS} { return CeylonTokens.WS; }

{LIDENTIFIER} { return CeylonTokens.LIDENTIFIER; }
{UIDENTIFIER} { return CeylonTokens.UIDENTIFIER; }

"\"" { yybegin(IN_STRING); return CeylonTokens.STRING_LITERAL; }

<IN_STRING> {
    "``" [^``]* "``"? { return CeylonTokens.STRING_TEMPLATE; }
    "`" [^`\"]* "`"? { return CeylonTokens.STRING_INTERP; }
    [^\\]? "\"" { yybegin(YYINITIAL); return CeylonTokens.STRING_LITERAL; }
    ([^`\"] | "\\\"")* { return CeylonTokens.STRING_LITERAL; }
}

// Nested multiline comments.
// This creates multiple tokens per comment, which shouldn't be a problem
// since this lexer is not used for parsing.
<MCOMMENT> {
    "/"+ "*"    { ++multiCommentLevel; return CeylonTokens.MULTI_COMMENT; }

    [^*/]+      { return CeylonTokens.MULTI_COMMENT; }

    "*"+ [^*/]* { return CeylonTokens.MULTI_COMMENT; }

    "/"+ [^*/]* { return CeylonTokens.MULTI_COMMENT; }

    "*"+ "/"    {
                    --multiCommentLevel;
                    if (multiCommentLevel <= 0) {
                        yybegin(YYINITIAL);
                    }
                    return CeylonTokens.MULTI_COMMENT;
                }
}

"/*"        { yybegin(MCOMMENT); multiCommentLevel = 1; return CeylonTokens.MULTI_COMMENT; }

. { return TokenType.BAD_CHARACTER; }