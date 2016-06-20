package org.intellij.plugins.ceylon.ide.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTypes;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.CeylonStubTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class CeylonBlock implements Block {
    private final ASTNode node;
    private final Indent indent;

    private static final ChildAttributes NORMAL_INDENT_CHILDATTR = new ChildAttributes(Indent.getNormalIndent(), null);
    private static final ChildAttributes NO_INDENT_CHILDATTR = new ChildAttributes(Indent.getNoneIndent(), null);

    private static final Spacing NO_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
    private static final Spacing NO_SPACE_ALLOW_NEWLINE = Spacing.createSpacing(0, 0, 0, true, 0);
    private static final Spacing EMPTY_LINE_SPACING = Spacing.createSpacing(0, 0, 2, true, 0);
    private static final Spacing NEW_LINE_SPACING = Spacing.createSpacing(0, 0, 1, true, 1);
    private static final Spacing NEW_LINE_SPACING_STRICT = Spacing.createSpacing(0, 0, 1, false, 0);
    private static final Spacing SINGLE_SPACE_SPACING = Spacing.createSpacing(1, 1, 0, true, 0);
    private static final Spacing SINGLE_SPACE_SPACING_INLINE = Spacing.createSpacing(1, 1, 0, false, 0);
    private static final Spacing KEEP_SOME_SPACE = Spacing.createSpacing(1, 1, 0, true, 1);

    private static final List<IElementType> SMALLER_OP = Arrays.asList(CeylonTypes.SMALLER_OP, CeylonTokens.SMALLER_OP);
    private static final List<IElementType> LARGER_OP = Arrays.asList(CeylonTypes.LARGER_OP, CeylonTokens.LARGER_OP);
    private static final List<IElementType> INDENT_CHILDREN_NORMAL = Arrays.asList(
            CeylonTypes.IMPORT_MODULE_LIST, CeylonTypes.BLOCK, CeylonTypes.CLASS_BODY, CeylonTypes.INTERFACE_BODY,
            CeylonTypes.NAMED_ARGUMENT_LIST, CeylonTypes.SEQUENCED_ARGUMENT, CeylonTypes.IMPORT_MEMBER_OR_TYPE_LIST,
            CeylonTypes.CONDITION_LIST
    );
    private static final List<IElementType> INDENT_CHILDREN_NONE = Arrays.asList(
            CeylonStubTypes.CEYLON_FILE, CeylonTypes.COMPILATION_UNIT,
            CeylonTypes.SWITCH_STATEMENT, CeylonTypes.SWITCH_CASE_LIST, CeylonTypes.SWITCH_CLAUSE, CeylonTypes.CASE_CLAUSE,
            CeylonTypes.SEQUENCE_ENUMERATION, CeylonTypes.ANNOTATION_LIST, CeylonTypes.TYPE_CONSTRAINT_LIST,
            CeylonTypes.IF_STATEMENT, CeylonTypes.ELSE_CLAUSE
    );
    private static final List<IElementType> INDENT_CHILDREN_CONTINUE = Arrays.asList(
            CeylonTypes.EXTENDED_TYPE, CeylonTypes.SATISFIED_TYPES, CeylonTypes.CASE_TYPES,
            CeylonTypes.LAZY_SPECIFIER_EXPRESSION, CeylonTypes.SPECIFIER_EXPRESSION
    );

    private static final Collection<IElementType> TYPES_REQUIRING_NO_LEFT_SPACING = Arrays.asList(
            CeylonTypes.PARAMETER_LIST, CeylonTokens.RPAREN, CeylonTokens.COMMA, CeylonTokens.SEMICOLON,
            CeylonTypes.TYPE_PARAMETER_LIST, CeylonTypes.INVOCATION_EXPRESSION,
            CeylonTokens.UNION_OP, CeylonTypes.UNION_OP, CeylonTokens.INTERSECTION_OP, CeylonTypes.INTERSECTION_OP,
            CeylonTypes.TYPE_ARGUMENT_LIST,
            CeylonTypes.SEQUENCED_TYPE, CeylonTokens.OPTIONAL, CeylonTokens.RBRACKET,
            CeylonTypes.INCREMENT_OP, CeylonTypes.DECREMENT_OP, CeylonTypes.POSTFIX_INCREMENT_OP, CeylonTypes.POSTFIX_DECREMENT_OP,
            CeylonTokens.LBRACKET,
            CeylonTokens.ENTRY_OP, CeylonTypes.ENTRY_OP,
            CeylonTokens.SMALL_AS_OP, CeylonTokens.SMALLER_OP,
            CeylonTokens.LARGE_AS_OP, CeylonTokens.LARGER_OP
    );
    private static final Collection<IElementType> TYPES_REQUIRING_NO_RIGHT_SPACING = Arrays.asList(
            CeylonTokens.LPAREN,
            CeylonTypes.MEMBER_OP, CeylonTokens.MEMBER_OP,
            CeylonTokens.UNION_OP, CeylonTypes.UNION_OP, CeylonTokens.INTERSECTION_OP, CeylonTypes.INTERSECTION_OP,
            CeylonTypes.NOT_OP, CeylonTypes.TYPE_ARGUMENT_LIST,
            CeylonTypes.INCREMENT_OP, CeylonTypes.DECREMENT_OP,
            CeylonTokens.LBRACKET, CeylonTypes.SAFE_MEMBER_OP,
            CeylonTypes.SPREAD_OP,
            CeylonTokens.ENTRY_OP, CeylonTypes.ENTRY_OP,
            CeylonTypes.POSITIVE_OP, CeylonTypes.NEGATIVE_OP,
            CeylonTokens.SMALL_AS_OP, CeylonTokens.SMALLER_OP,
            CeylonTokens.LARGE_AS_OP, CeylonTokens.LARGER_OP
    );
    private static final Collection<IElementType> TYPES_REQUIRING_EMPTY_LINE = Arrays.asList(
            CeylonTypes.IMPORT_LIST, CeylonTypes.CLASS_DEFINITION, CeylonTypes.METHOD_DEFINITION
    );
    private static final Collection<IElementType> COMMENTS = Arrays.asList(CeylonTokens.LINE_COMMENT, CeylonTokens.MULTI_COMMENT);

    CeylonBlock(ASTNode node, Indent indent) {
//        System.out.printf("Indent for %s: %s%n", node.getElementType(), indent);
        this.node = node;
        this.indent = indent;
    }

    @NotNull
    @Override
    public TextRange getTextRange() {
        return node.getTextRange();
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks() {
        List<Block> blocks = new ArrayList<>();

        final IElementType nodeType = node.getElementType();
        final Indent normalChildIndent = INDENT_CHILDREN_NORMAL.contains(nodeType) ? Indent.getNormalIndent()
                : INDENT_CHILDREN_NONE.contains(nodeType) ? Indent.getNoneIndent()
                : INDENT_CHILDREN_CONTINUE.contains(nodeType) ? Indent.getContinuationIndent()
                : Indent.getNoneIndent();
//        System.out.printf("normalChildIndent for %s: %s%n", nodeType, indent);
        IElementType prevChildType = null;
        for (ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (!FormatterUtil.containsWhiteSpacesOnly(child) && child.getTextLength() > 0) {
                Indent indent = normalChildIndent;
                if (child.getElementType() == CeylonTokens.RBRACE || child.getElementType() == CeylonTokens.LBRACE) {
                    indent = Indent.getNoneIndent();
                }
                if (prevChildType == CeylonTypes.ANNOTATION_LIST && indent == null) {
                    indent = Indent.getNoneIndent();
                }
                if (child.getElementType() == CeylonTokens.MEMBER_OP) {
                    indent = Indent.getNormalIndent();
                }
                if (child.getElementType() == CeylonTypes.LISTED_ARGUMENT) {
                    indent = Indent.getNormalIndent();
                }
                blocks.add(new CeylonBlock(child, indent));
            }
            if (child.getElementType() != TokenType.WHITE_SPACE) {
                prevChildType = child.getElementType();
            }
        }

        return blocks;
    }

    @Nullable
    @Override
    public Wrap getWrap() {
        return null;
    }

    @Nullable
    @Override
    public Indent getIndent() {
        return indent;
    }

    @Nullable
    @Override
    public Alignment getAlignment() {
        return null;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        final CeylonBlock block1 = (CeylonBlock) child1;
        final CeylonBlock block2 = (CeylonBlock) child2;

        final IElementType type1 = block1 == null ? null : block1.node.getElementType();
        final IElementType type2 = block2.node.getElementType();

        final IElementType nodeType = node.getElementType();

        Collection<IElementType> bothTypes = Arrays.asList(type1, type2);

        Spacing result;

        if (block1 == null || COMMENTS.contains(type1)) {
            result = NO_SPACING;
        } else if (COMMENTS.contains(type2)) {
            result = SINGLE_SPACE_SPACING_INLINE;
        } else if (TYPES_REQUIRING_EMPTY_LINE.contains(type1) && type2 != CeylonTokens.RBRACE
                || TYPES_REQUIRING_EMPTY_LINE.contains(type2) && !Arrays.asList(CeylonTokens.LBRACE, CeylonTypes.ANNOTATION_LIST).contains(type1)) {
            result = EMPTY_LINE_SPACING;
        } else if (CeylonTypes.IMPORT_MODULE.equals(type1)) {
            result = NEW_LINE_SPACING;
        } else if (CeylonTypes.IMPORT_MEMBER.equals(type2)) {
            result = NEW_LINE_SPACING;
        } else if ((type1 == CeylonTokens.UNION_OP || type2 == CeylonTokens.UNION_OP)
                && nodeType == CeylonTypes.CASE_TYPES){
            result = SINGLE_SPACE_SPACING;
        } else if (TYPES_REQUIRING_NO_RIGHT_SPACING.contains(type1) || TYPES_REQUIRING_NO_LEFT_SPACING.contains(type2)
                || (type1 == CeylonTypes.SMALLER_OP && type2 == CeylonTypes.TYPE_PARAMETER_DECLARATION)
                || (type1 == CeylonTypes.TYPE_PARAMETER_LITERAL && type2 == CeylonTypes.LARGER_OP)
                || (type2 == CeylonTypes.POSITIONAL_ARGUMENT_LIST && nodeType != CeylonTypes.ANNOTATION)
                ) {
            result = NO_SPACING;
        } else if ((SMALLER_OP.contains(type1) && isType(block2))
                || (LARGER_OP.contains(type2) && isType(block1))) {
            result = NO_SPACING;
        } else if (Arrays.asList(CeylonTypes.MEMBER_OP, CeylonTokens.MEMBER_OP, CeylonTypes.SAFE_MEMBER_OP).contains(type2)) {
            result = nodeType == CeylonTypes.QUALIFIED_MEMBER_EXPRESSION ? NO_SPACE_ALLOW_NEWLINE : NO_SPACING;
        } else if (Arrays.asList(CeylonTypes.TYPE_ARGUMENT_LIST, CeylonTypes.TYPE_PARAMETER_LIST, CeylonTypes.SEQUENCED_TYPE, CeylonTypes.INDEX_EXPRESSION).contains(nodeType)) {
            result = NO_SPACING;
        } else if (type1 == CeylonTokens.LBRACE && type2 == CeylonTokens.RBRACE) {
            result = NO_SPACING;
        } else if (CeylonTypes.SPREAD_ARGUMENT.equals(nodeType) && CeylonTokens.PRODUCT_OP.equals(type1)) {
            result = NO_SPACING;
        } else if (CeylonTypes.FUNCTION_TYPE.equals(nodeType)) {
            result = CeylonTokens.COMMA.equals(type1) ? SINGLE_SPACE_SPACING : NO_SPACING;
        } else if (type1 == CeylonTypes.ANNOTATION_LIST) {
            result = KEEP_SOME_SPACE;
        } else if (Arrays.asList(CeylonTypes.IMPORT_MEMBER, CeylonTypes.THROW, CeylonTypes.IMPORT, CeylonTypes.ASSERTION, CeylonTypes.SATISFIED_TYPES, CeylonTypes.EXTENDED_TYPE).contains(nodeType)) {
            result = SINGLE_SPACE_SPACING_INLINE;
        } else if (nodeType == CeylonTypes.ANNOTATION_LIST && type1 == CeylonTypes.STRING_LITERAL) {
            result = NEW_LINE_SPACING;
        } else if (CeylonTypes.METHOD_DEFINITION.equals(nodeType) && type2 == CeylonTypes.BLOCK) {
            result = SINGLE_SPACE_SPACING_INLINE;
        } else if (bothTypes.contains(CeylonTypes.IDENTIFIER) && Arrays.asList(CeylonTypes.CLASS_DEFINITION, CeylonTypes.INTERFACE_DEFINITION).contains(nodeType)) {
            result = SINGLE_SPACE_SPACING_INLINE;
        } else if (INDENT_CHILDREN_NORMAL.contains(nodeType)
                && (type2 == CeylonTokens.RBRACE && type1 != CeylonTokens.LBRACE)) {
            result = NEW_LINE_SPACING_STRICT;
        } else if (INDENT_CHILDREN_NORMAL.contains(nodeType)
                && (type1 == CeylonTokens.LBRACE && type2 != CeylonTokens.RBRACE)) {
            result = NEW_LINE_SPACING;
        } else if (isStatement(block1) && isStatement(block2)) {
            result = NEW_LINE_SPACING;
        } else if (isMetaLiteral(this) && bothTypes.contains(CeylonTokens.BACKTICK)) {
            result = NO_SPACING;
        } else if (nodeType == CeylonTypes.ANNOTATION && type1 == CeylonTypes.IDENTIFIER) {
            result = SINGLE_SPACE_SPACING;
        } else if ((bothTypes.contains(CeylonTokens.ELSE_CLAUSE) || bothTypes.contains(CeylonTypes.ELSE_CLAUSE))) {
            result = nodeType == CeylonTypes.SWITCH_CASE_LIST ? NEW_LINE_SPACING_STRICT : SINGLE_SPACE_SPACING_INLINE;
        } else if (nodeType == CeylonTypes.STRING_TEMPLATE) {
            result = NO_SPACE_ALLOW_NEWLINE; // TODO: spacing for longer expressions
        } else if (nodeType == CeylonTypes.ITERABLE_TYPE) {
            result = NO_SPACING;
        } else {
            result = SINGLE_SPACE_SPACING;
        }

//        if (NO_SPACING != result) {
//            System.out.printf("Spacing between %s and %s in %s: %s%n", type1, type2, nodeType, getSpacingName(result));
//        }
        return result;
    }

    private boolean isStatement(CeylonBlock block) {
        return block.node.getPsi() instanceof CeylonPsi.StatementPsi;
    }

    private static boolean isType(CeylonBlock block) {
        return block.node.getPsi() instanceof CeylonPsi.TypePsi;
    }

    private static boolean isMetaLiteral(CeylonBlock block) {
        return block.node.getPsi() instanceof CeylonPsi.MetaLiteralPsi;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return INDENT_CHILDREN_NONE.contains(node.getElementType()) ? NO_INDENT_CHILDATTR : NORMAL_INDENT_CHILDATTR;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return node.getFirstChildNode() == null;
    }
}
