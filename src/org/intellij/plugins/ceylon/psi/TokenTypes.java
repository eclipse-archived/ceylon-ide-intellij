package org.intellij.plugins.ceylon.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.CeylonLanguage;

public enum TokenTypes {

    EOF(-1),
    ABSTRACTED_TYPE(4),
    ADD_SPECIFY(5),
    AIDENTIFIER(6),
    ALIAS(7),
    AND_OP(8),
    AND_SPECIFY(9),
    ASSEMBLY(10),
    ASSERT(11),
    ASSIGN(12),
    ASTRING_LITERAL(13),
    AVERBATIM_STRING(14),
    BACKTICK(15),
    BREAK(16),
    BinaryDigit(17),
    BinaryDigits(18),
    CASE_CLAUSE(19),
    CASE_TYPES(20),
    CATCH_CLAUSE(21),
    CHAR_LITERAL(22),
    CLASS_DEFINITION(23),
    COMMA(24),
    COMPARE_OP(25),
    COMPILER_ANNOTATION(26),
    COMPLEMENT_OP(27),
    COMPLEMENT_SPECIFY(28),
    COMPUTE(29),
    CONTINUE(30),
    CharPart(31),
    DECREMENT_OP(32),
    DIFFERENCE_OP(33),
    DIVIDE_SPECIFY(34),
    DYNAMIC(35),
    Digit(36),
    Digits(37),
    ELLIPSIS(38),
    ELSE_CLAUSE(39),
    ENTRY_OP(40),
    EQUAL_OP(41),
    EXISTS(42),
    EXTENDS(43),
    EscapeSequence(44),
    Exponent(45),
    FINALLY_CLAUSE(46),
    FLOAT_LITERAL(47),
    FOR_CLAUSE(48),
    FUNCTION_MODIFIER(49),
    FractionalMagnitude(50),
    HexDigit(51),
    HexDigits(52),
    IDENTICAL_OP(53),
    IF_CLAUSE(54),
    IMPORT(55),
    INCREMENT_OP(56),
    INTERFACE_DEFINITION(57),
    INTERSECTION_OP(58),
    INTERSECT_SPECIFY(59),
    IN_OP(60),
    IS_OP(61),
    IdentifierPart(62),
    IdentifierStart(63),
    LARGER_OP(64),
    LARGE_AS_OP(65),
    LBRACE(66),
    LBRACKET(67),
    LIDENTIFIER(68),
    LINE_COMMENT(69),
    LIdentifierPrefix(70),
    LPAREN(71),
    Letter(72),
    MEMBER_OP(73),
    MODULE(74),
    MULTIPLY_SPECIFY(75),
    MULTI_COMMENT(76),
    Magnitude(77),
    NATURAL_LITERAL(78),
    NONEMPTY(79),
    NOT_EQUAL_OP(80),
    NOT_OP(81),
    OBJECT_DEFINITION(82),
    OPTIONAL(83),
    OR_OP(84),
    OR_SPECIFY(85),
    OUT(86),
    OUTER(87),
    PACKAGE(88),
    PIDENTIFIER(89),
    POWER_OP(90),
    PRODUCT_OP(91),
    QUOTIENT_OP(92),
    RANGE_OP(93),
    RBRACE(94),
    RBRACKET(95),
    REMAINDER_OP(96),
    REMAINDER_SPECIFY(97),
    RETURN(98),
    RPAREN(99),
    SAFE_MEMBER_OP(100),
    SATISFIES(101),
    SCALE_OP(102),
    SEGMENT_OP(103),
    SEMICOLON(104),
    SMALLER_OP(105),
    SMALL_AS_OP(106),
    SPECIFY(107),
    SPREAD_OP(108),
    STRING_END(109),
    STRING_LITERAL(110),
    STRING_MID(111),
    STRING_START(112),
    SUBTRACT_SPECIFY(113),
    SUM_OP(114),
    SUPER(115),
    SWITCH_CLAUSE(116),
    StringPart(117),
    THEN_CLAUSE(118),
    THIS(119),
    THROW(120),
    TRY_CLAUSE(121),
    TYPE_CONSTRAINT(122),
    UIDENTIFIER(123),
    UIdentifierPrefix(124),
    UNION_OP(125),
    UNION_SPECIFY(126),
    VALUE_MODIFIER(127),
    VERBATIM_STRING(128),
    VOID_MODIFIER(129),
    WHILE_CLAUSE(130),
    WS(131),

    ;

    private final int value;
    private IElementType tokenType;
    
    private TokenTypes(int value) {
        this.value = value;
        tokenType = new IElementType(name(), CeylonLanguage.INSTANCE);
    }

    public IElementType getTokenType() {
        return tokenType;
    }
    
    public static IElementType fromInt(int value) {
        for (TokenTypes tokenType : values()) {
            if (tokenType.value == value) {
                return tokenType.tokenType;
            }
        }

        return null;
    }
}