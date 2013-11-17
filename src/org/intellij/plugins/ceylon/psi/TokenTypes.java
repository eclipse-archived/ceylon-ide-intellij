package org.intellij.plugins.ceylon.psi;

import com.intellij.psi.tree.IElementType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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
    LET(68),
    LIDENTIFIER(69),
    LINE_COMMENT(70),
    LIdentifierPrefix(71),
    LPAREN(72),
    Letter(73),
    MEMBER_OP(74),
    MODULE(75),
    MULTIPLY_SPECIFY(76),
    MULTI_COMMENT(77),
    Magnitude(78),
    NATURAL_LITERAL(79),
    NEW(80),
    NONEMPTY(81),
    NOT_EQUAL_OP(82),
    NOT_OP(83),
    OBJECT_DEFINITION(84),
    OPTIONAL(85),
    OR_OP(86),
    OR_SPECIFY(87),
    OUT(88),
    OUTER(89),
    PACKAGE(90),
    PIDENTIFIER(91),
    POWER_OP(92),
    PRODUCT_OP(93),
    QUOTIENT_OP(94),
    RANGE_OP(95),
    RBRACE(96),
    RBRACKET(97),
    REMAINDER_OP(98),
    REMAINDER_SPECIFY(99),
    RETURN(100),
    RPAREN(101),
    SAFE_MEMBER_OP(102),
    SATISFIES(103),
    SCALE_OP(104),
    SEGMENT_OP(105),
    SEMICOLON(106),
    SMALLER_OP(107),
    SMALL_AS_OP(108),
    SPECIFY(109),
    SPREAD_OP(110),
    STRING_END(111),
    STRING_LITERAL(112),
    STRING_MID(113),
    STRING_START(114),
    SUBTRACT_SPECIFY(115),
    SUM_OP(116),
    SUPER(117),
    SWITCH_CLAUSE(118),
    StringPart(119),
    THEN_CLAUSE(120),
    THIS(121),
    THROW(122),
    TRY_CLAUSE(123),
    TYPE_CONSTRAINT(124),
    UIDENTIFIER(125),
    UIdentifierPrefix(126),
    UNION_OP(127),
    UNION_SPECIFY(128),
    VALUE_MODIFIER(129),
    VERBATIM_STRING(130),
    VOID_MODIFIER(131),
    WHILE_CLAUSE(132),
    WS(133),

    ;

    static final Map<IElementType, TokenTypes> byIElementType = new HashMap<>();
    static final Map<Integer, TokenTypes> byIndex = new HashMap<>();

    static {
        final Field[] fields = CeylonTokens.class.getDeclaredFields();
        final Map<String, IElementType> ceylonTokensFields = new HashMap<>();
        for (Field tokenField : fields) {
            try {
                final IElementType elt = (IElementType) tokenField.get(null);
                ceylonTokensFields.put(tokenField.getName(), elt);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accesing CeylonTypes fields.", e);
            }
        }
        for (TokenTypes tokenType : values()) {
            tokenType.tokenType = ceylonTokensFields.get(tokenType.name());
            byIElementType.put(tokenType.tokenType, tokenType);
            byIndex.put(tokenType.getValue(), tokenType);
        }
    }

    private final int value;
    private IElementType tokenType;
    
    private TokenTypes(int value) {
        this.value = value;
    }

    public IElementType getTokenType() {
        return tokenType;
    }
    
    public int getValue() {
        return value;
    }

    public static IElementType fromInt(int value) {
        final TokenTypes tt = byIndex.get(value);
        assertNotNull(value, tt);
        return tt.tokenType;
    }

    public static TokenTypes get(IElementType key) {
        final TokenTypes tt = byIElementType.get(key);
        assertNotNull(key, tt);
        return tt;
    }

    private static void assertNotNull(Object key, TokenTypes tt) {
        if (tt == null) {
            throw new IllegalArgumentException("Unknown token type: " + key);
        }
    }
}