package org.intellij.plugins.ceylon.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import com.intellij.psi.TokenType;

%%

%class _CeylonLexer
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
CharacterLiteral = "`" {Character} "`"
Character = {EscapeSequence} | [^`\\]*

EscapeSequence = [\\] ("b" | "t" | "n" | "f" | "r" | "\" | """ | "'" | "`" | "{" {CharacterCode} "}")
CharacterCode = {HexDigit}{4} | {HexDigit}{8}
HexDigit = [0-9A-Fa-f]

// String literals
StringLiteral = "\"" {StringCharacter}* "\""
StringCharacter = [^\\\"]* | {EscapeSequence}

QuotedLiteral = "'" {QuotedLiteralCharacter}* "'"
QuotedLiteralCharacter = [^']*

//%debug

%%

/* Keywords */
"import"        { return CeylonTypes.KW_IMPORT; }
"class"         { return CeylonTypes.KW_CLASS; }
"interface"     { return CeylonTypes.KW_INTERFACE; }
"object"        { return CeylonTypes.KW_OBJECT; }
"given"         { return CeylonTypes.KW_GIVEN; }
"value"         { return CeylonTypes.KW_VALUE; }
"assign"        { return CeylonTypes.KW_ASSIGN; }
"void"          { return CeylonTypes.KW_VOID; }
"function"      { return CeylonTypes.KW_FUNCTION; }
"of"            { return CeylonTypes.KW_OF; }
"extends"       { return CeylonTypes.KW_EXTENDS; }
"satisfies"     { return CeylonTypes.KW_SATISFIES; }
"adapts"        { return CeylonTypes.KW_ADAPTS; }
"abstracts"     { return CeylonTypes.KW_ABSTRACTS; }
"in"            { return CeylonTypes.KW_IN; }
"out"           { return CeylonTypes.KW_OUT; }
"return"        { return CeylonTypes.KW_RETURN; }
"break"         { return CeylonTypes.KW_BREAK; }
"continue"      { return CeylonTypes.KW_CONTINUE; }
"throw"         { return CeylonTypes.KW_THROW; }
"if"            { return CeylonTypes.KW_IF; }
"else"          { return CeylonTypes.KW_ELSE; }
"switch"        { return CeylonTypes.KW_SWITCH; }
"case"          { return CeylonTypes.KW_CASE; }
"for"           { return CeylonTypes.KW_FOR; }
"while"         { return CeylonTypes.KW_WHILE; }
"try"           { return CeylonTypes.KW_TRY; }
"catch"         { return CeylonTypes.KW_CATCH; }
"finally"       { return CeylonTypes.KW_FINALLY; }
"then"          { return CeylonTypes.KW_THEN; }
"this"          { return CeylonTypes.KW_THIS; }
"outer"         { return CeylonTypes.KW_OUTER; }
"super"         { return CeylonTypes.KW_SUPER; }
"is"            { return CeylonTypes.KW_IS; }
"exists"        { return CeylonTypes.KW_EXISTS; }
"nonempty"      { return CeylonTypes.KW_NONEMPTY; }
"module"        { return CeylonTypes.KW_MODULE; }

//"shared"        { return CeylonTypes.KW_SHARED; }
//"abstract"      { return CeylonTypes.KW_ABSTRACT; }
//"formal"        { return CeylonTypes.KW_FORMAL; }
//"default"       { return CeylonTypes.KW_DEFAULT; }
//"actual"        { return CeylonTypes.KW_ACTUAL; }
//"variable"      { return CeylonTypes.KW_VARIABLE; }
//"deprecated"    { return CeylonTypes.KW_DEPRECATED; }
//"literal"       { return CeylonTypes.KW_LITERAL; }
//"small"         { return CeylonTypes.KW_SMALL; }


","     { return CeylonTypes.OP_COMMA; }
";"     { return CeylonTypes.OP_SEMI_COLUMN; }
"..."   { return CeylonTypes.OP_ELLIPSIS; }
"#"     { return CeylonTypes.OP_SHARP; }
"{"     { return CeylonTypes.OP_LBRACE; }
"}"     { return CeylonTypes.OP_RBRACE; }
"("     { return CeylonTypes.OP_LPAREN; }
")"     { return CeylonTypes.OP_RPAREN; }
"["     { return CeylonTypes.OP_LBRACKET; }
"]"     { return CeylonTypes.OP_RBRACKET; }
"[]"    { return CeylonTypes.OP_BRACKETS; }
"."     { return CeylonTypes.OP_DOT; }
"?."    { return CeylonTypes.OP_DOT_QUESTION; }
"[]."   { return CeylonTypes.OP_BRACKETS_DOT; }
"="     { return CeylonTypes.OP_EQUALS; }
"+"     { return CeylonTypes.OP_PLUS; }
"-"     { return CeylonTypes.OP_MINUS; }
"/"     { return CeylonTypes.OP_DIVIDE; }
"*"     { return CeylonTypes.OP_MULTIPLY; }
"%"     { return CeylonTypes.OP_MODULO; }
"**"    { return CeylonTypes.OP_MULT_MULT; }
"++"    { return CeylonTypes.OP_PLUS_PLUS; }
"--"    { return CeylonTypes.OP_MIN_MIN; }
".."    { return CeylonTypes.OP_DOT_DOT; }
"->"    { return CeylonTypes.OP_ARROW; }
"?"     { return CeylonTypes.OP_QUESTION; }
"!"     { return CeylonTypes.OP_NOT; }
"&&"    { return CeylonTypes.OP_LOGICAL_AND; }
"||"    { return CeylonTypes.OP_LOGICAL_OR; }
"~"     { return CeylonTypes.OP_TIDLE; }
"&"     { return CeylonTypes.OP_INTERSECTION; }
"|"     { return CeylonTypes.OP_UNION; }
"^"     { return CeylonTypes.OP_XOR; }
"==="   { return CeylonTypes.OP_EQ_EQ_EQ; }
"=="    { return CeylonTypes.OP_EQ_EQ; }
"!="    { return CeylonTypes.OP_EXCL_EQ; }
"<"     { return CeylonTypes.OP_LT; }
">"     { return CeylonTypes.OP_GT; }
"<="    { return CeylonTypes.OP_LTE; }
">="    { return CeylonTypes.OP_GTE; }
"<=>"   { return CeylonTypes.OP_DIFFERENT; }
":="    { return CeylonTypes.OP_ASSIGN; }
".="    { return CeylonTypes.OP_DOT_EQ; }
"+="    { return CeylonTypes.OP_PLUS_EQ; }
"-="    { return CeylonTypes.OP_MINUS_EQ; }
"/="    { return CeylonTypes.OP_DIV_EQ; }
"*="    { return CeylonTypes.OP_MULT_EQ; }
"%="    { return CeylonTypes.OP_MOD_EQ; }
"|="    { return CeylonTypes.OP_OR_EQ; }
"&="    { return CeylonTypes.OP_AND_EQ; }
"^="    { return CeylonTypes.OP_XOR_EQ; }
"~="    { return CeylonTypes.OP_NOT_EQ; }
"||="   { return CeylonTypes.OP_LOG_OR_EQ; }
"&&="   { return CeylonTypes.OP_LOG_AND_EQ; }
"@"     { return CeylonTypes.OP_ANNOTATION; }

{LineComment}                   { return CeylonTypes.LINE_COMMENT; }
{MultilineComment}              { return CeylonTypes.MULTI_LINE_COMMENT; }
{LIdentifier}                   { return CeylonTypes.LIDENTIFIER; }
{UIdentifier}                   { return CeylonTypes.UIDENTIFIER; }
{IntegerLiteral}                { return CeylonTypes.NATURAL_LITERAL; }
{FloatLiteral}                  { return CeylonTypes.FLOAT_LITERAL; }
{CharacterLiteral}              { return CeylonTypes.CHAR_LITERAL; }
{StringLiteral}                 { return CeylonTypes.STRING_LITERAL; }
{QuotedLiteral}                 { return CeylonTypes.QUOTED_LITERAL; }
{Whitespace}                    { return TokenType.WHITE_SPACE; }
.                               { return TokenType.BAD_CHARACTER; }
