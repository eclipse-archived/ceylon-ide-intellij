package org.intellij.plugins.ceylon.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.psi.CeylonTokens;

%%

%class CeylonFlexLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}
//%debug

%state IN_IMPORT

Tab = "\t"
Formfeed = "\f"
Newline = "\n"
Return = "\r"
Whitespace = " " | {Tab} | {Formfeed} | {Newline} | {Return}

// Comments
LineComment = ("//" | "#!") [^\n\r]* ("\n" | "\r" | "\r\n")?
MultilineComment = "/*" !([^]* "*/" [^]*) ("*/")?
// "/*" ( {MultilineCommmentCharacter} | {MultilineComment} )* "*/"
MultilineCommmentCharacter =  ~("/" | "*") | ("/" ~"*") => "/" | ("*" ~"/") => "*"

// Identifiers
LowercaseChar = [a-z_]
UppercaseChar = [A-Z]
Number = [0-9]
IdentifierChar = {LowercaseChar} | {UppercaseChar} | {Number}
LIdentifier = {LowercaseChar} {IdentifierChar}* | "\\i" {IdentifierChar}+
UIdentifier = {UppercaseChar} {IdentifierChar}* | "\\I" {IdentifierChar}+
PIdentifier = ("\\i" | "\\I")? {LowercaseChar}+

// Numbers
IntegerLiteral = {Digits} {Magnitude}?
FloatLiteral = {Digits} ("." {FractionalDigits} ({Exponent} | {Magnitude} | {FractionalMagnitude})? | {FractionalMagnitude})
Digits = {Digit}+ | {Digit}{1,3} ("_" {Digit}{3})+
FractionalDigits = {Digit}+ | ({Digit}{3} "_")+ {Digit}{1,3}
Digit = [0-9]
Exponent = ("E"|"e") ("+"|"-")? {Digits}
Magnitude = "k" | "M" | "G" | "T" | "P"
FractionalMagnitude = "m" | "u" | "n" | "p" | "f"

// Character literals
CharacterLiteral = "'" {Character} "'"
Character = ({EscapeSequence} | [^'\\])*

EscapeSequence = [\\] ([\\] | "b" | "t" | "n" | "f" | "r" | "\" | """ | "'" | "`" | "{" {CharacterCode} "}")
CharacterCode = {HexDigit}{4} | {HexDigit}{8}
HexDigit = [0-9A-Fa-f]

// String literals
StringLiteral = "\"" {StringCharacter}* "\""?
StringCharacter = [^\\\"]* | {EscapeSequence}

//QuotedLiteral = "'" {QuotedLiteralCharacter}* "'"?
//QuotedLiteralCharacter = [^']*

//%debug

%%

"`"                    { return CeylonTokens.BACKTICK ; }
"assembly"             { return CeylonTokens.ASSEMBLY ; }
"assert"               { return CeylonTokens.ASSERT ; }
"abstracts"            { return CeylonTokens.ABSTRACTED_TYPE; }
"assign"               { return CeylonTokens.ASSIGN; }
"alias"                { return CeylonTokens.ALIAS; }
"break"                { return CeylonTokens.BREAK; }
"case"                 { return CeylonTokens.CASE_CLAUSE; }
"catch"                { return CeylonTokens.CATCH_CLAUSE; }
"class"                { return CeylonTokens.CLASS_DEFINITION; }
"continue"             { return CeylonTokens.CONTINUE; }
"dynamic"              { return CeylonTokens.DYNAMIC; }
"else"                 { return CeylonTokens.ELSE_CLAUSE; }
"exists"               { return CeylonTokens.EXISTS; }
"extends"              { return CeylonTokens.EXTENDS; }
"finally"              { return CeylonTokens.FINALLY_CLAUSE; }
"for"                  { return CeylonTokens.FOR_CLAUSE; }
"given"                { return CeylonTokens.TYPE_CONSTRAINT; }
"if"                   { return CeylonTokens.IF_CLAUSE; }
"satisfies"            { return CeylonTokens.SATISFIES; }
"import"               { return CeylonTokens.IMPORT; }
"interface"            { return CeylonTokens.INTERFACE_DEFINITION; }
"value"                { return CeylonTokens.VALUE_MODIFIER; }
"function"             { return CeylonTokens.FUNCTION_MODIFIER; }
"module"               { return CeylonTokens.MODULE; }
"package"              { return CeylonTokens.PACKAGE; }
"nonempty"             { return CeylonTokens.NONEMPTY; }
"return"               { return CeylonTokens.RETURN; }
"super"                { return CeylonTokens.SUPER; }
"switch"               { return CeylonTokens.SWITCH_CLAUSE; }
"then"                 { return CeylonTokens.THEN_CLAUSE; }
"this"                 { return CeylonTokens.THIS; }
"outer"                { return CeylonTokens.OUTER; }
"object"               { return CeylonTokens.OBJECT_DEFINITION; }
"of"                   { return CeylonTokens.CASE_TYPES; }
"out"                  { return CeylonTokens.OUT; }
"throw"                { return CeylonTokens.THROW; }
"try"                  { return CeylonTokens.TRY_CLAUSE; }
"void"                 { return CeylonTokens.VOID_MODIFIER; }
"while"                { return CeylonTokens.WHILE_CLAUSE; }
"..."                  { return CeylonTokens.ELLIPSIS; }
".."                   { return CeylonTokens.RANGE_OP; }
":"                    { return CeylonTokens.SEGMENT_OP; }
"."                    { return CeylonTokens.MEMBER_OP ; }
"("                    { return CeylonTokens.LPAREN; }
")"                    { return CeylonTokens.RPAREN; }
"{"                    { return CeylonTokens.LBRACE; }
"}"                    { return CeylonTokens.RBRACE; }
"["                    { return CeylonTokens.LBRACKET; }
"]"                    { return CeylonTokens.RBRACKET; }
";"                    { return CeylonTokens.SEMICOLON; }
","                    { return CeylonTokens.COMMA; }
"="                    { return CeylonTokens.SPECIFY; }
"=>"                   { return CeylonTokens.COMPUTE; }
"?."                   { return CeylonTokens.SAFE_MEMBER_OP; }
"?"                    { return CeylonTokens.OPTIONAL ; }
"!"                    { return CeylonTokens.NOT_OP; }
"~"                    { return CeylonTokens.COMPLEMENT_OP; }
"=="                   { return CeylonTokens.EQUAL_OP; }
"==="                  { return CeylonTokens.IDENTICAL_OP; }
"&&"                   { return CeylonTokens.AND_OP; }
"||"                   { return CeylonTokens.OR_OP; }
"++"                   { return CeylonTokens.INCREMENT_OP; }
"--"                   { return CeylonTokens.DECREMENT_OP; }
"+"                    { return CeylonTokens.SUM_OP; }
"-"                    { return CeylonTokens.DIFFERENCE_OP; }
"*."                   { return CeylonTokens.SPREAD_OP ; }
"**"                   { return CeylonTokens.SCALE_OP ; }
"*"                    { return CeylonTokens.PRODUCT_OP; }
"/"                    { return CeylonTokens.QUOTIENT_OP; }
"&"                    { return CeylonTokens.INTERSECTION_OP; }
"|"                    { return CeylonTokens.UNION_OP; }
"%"                    { return CeylonTokens.REMAINDER_OP; }
"!="                   { return CeylonTokens.NOT_EQUAL_OP; }
">"                    { return CeylonTokens.LARGER_OP; }
"<"                    { return CeylonTokens.SMALLER_OP; }
">="                   { return CeylonTokens.LARGE_AS_OP; }
"<="                   { return CeylonTokens.SMALL_AS_OP; }
"->"                   { return CeylonTokens.ENTRY_OP; }
"<=>"                  { return CeylonTokens.COMPARE_OP; }
"in"                   { return CeylonTokens.IN_OP; }
"is"                   { return CeylonTokens.IS_OP; }
"^"                    { return CeylonTokens.POWER_OP; }
"+="                   { return CeylonTokens.ADD_SPECIFY; }
"-="                   { return CeylonTokens.SUBTRACT_SPECIFY; }
"*="                   { return CeylonTokens.MULTIPLY_SPECIFY; }
"/="                   { return CeylonTokens.DIVIDE_SPECIFY; }
"&="                   { return CeylonTokens.INTERSECT_SPECIFY; }
"|="                   { return CeylonTokens.UNION_SPECIFY; }
"~="                   { return CeylonTokens.COMPLEMENT_SPECIFY; }
"%="                   { return CeylonTokens.REMAINDER_SPECIFY; }
"&&="                  { return CeylonTokens.AND_SPECIFY; }
"||="                  { return CeylonTokens.OR_SPECIFY; }
"@"                    { return CeylonTokens.COMPILER_ANNOTATION; }


{LineComment}                   { return CeylonTokens.LINE_COMMENT; }
{MultilineComment}              { return CeylonTokens.MULTI_COMMENT; }
{LIdentifier}                   { return CeylonTokens.LIDENTIFIER; }
{UIdentifier}                   { return CeylonTokens.UIDENTIFIER; }
{IntegerLiteral}                { return CeylonTokens.NATURAL_LITERAL; }
{FloatLiteral}                  { return CeylonTokens.FLOAT_LITERAL; }
{CharacterLiteral}              { return CeylonTokens.CHAR_LITERAL; }
{StringLiteral}                 { return CeylonTokens.STRING_LITERAL; }
//{QuotedLiteral}                 { return CeylonTokens.QUOTED_LITERAL; }
{Whitespace}                    { return TokenType.WHITE_SPACE; }
.                               { return TokenType.BAD_CHARACTER; }
