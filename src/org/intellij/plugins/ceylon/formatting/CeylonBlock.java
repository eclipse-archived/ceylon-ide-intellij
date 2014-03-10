package org.intellij.plugins.ceylon.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.psi.CeylonTokens;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CeylonBlock implements Block {
    private final ASTNode node;
    private final Indent indent;

    protected static final Spacing NO_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
    protected static final Spacing EMPTY_LINE_SPACING = Spacing.createSpacing(0, 0, 2, true, 0);
    protected static final Spacing GOTO_LINE_SPACING = Spacing.createSpacing(0, 0, 1, true, 1);
    protected static final Spacing SINGLE_SPACE_SPACING = Spacing.createSpacing(1, 1, 0, true, 1);

    protected static final List<IElementType> BLOCK_TYPES =
            Arrays.asList(CeylonTypes.IMPORT_MODULE_LIST, CeylonTypes.BLOCK, CeylonTypes.CLASS_BODY, CeylonTypes.INTERFACE_BODY, CeylonTypes.NAMED_ARGUMENT_LIST, CeylonTypes.SEQUENCE_ENUMERATION);

    public CeylonBlock(ASTNode node, Indent indent) {
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

        for (ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (!FormatterUtil.containsWhiteSpacesOnly(child) && child.getTextLength() > 0) {
                if (BLOCK_TYPES.contains(node.getElementType()) && child.getElementType() != CeylonTokens.RBRACE) {
                    blocks.add(new CeylonBlock(child, Indent.getNormalIndent()));
                } else {
//                    System.out.println("No ident for " + child.getElementType());
                    blocks.add(new CeylonBlock(child, Indent.getNoneIndent()));
                }
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
        CeylonBlock block1 = (CeylonBlock) child1;
        CeylonBlock block2 = (CeylonBlock) child2;

        Collection<IElementType> typesRequiringNoLeftSpacing = Arrays.asList(
                CeylonTypes.PARAMETER_LIST, CeylonTokens.RPAREN, CeylonTokens.COMMA, CeylonTokens.SEMICOLON,
                CeylonTypes.MEMBER_OP, CeylonTokens.MEMBER_OP,
                CeylonTypes.TYPE_PARAMETER_LIST,
                CeylonTokens.UNION_OP, CeylonTypes.UNION_OP, CeylonTokens.INTERSECTION_OP, CeylonTypes.INTERSECTION_OP,
                CeylonTypes.TYPE_ARGUMENT_LIST,
                CeylonTypes.SEQUENCED_TYPE, CeylonTokens.OPTIONAL, CeylonTypes.POSITIONAL_ARGUMENT_LIST, CeylonTokens.RBRACKET,
                CeylonTypes.INCREMENT_OP, CeylonTypes.DECREMENT_OP, CeylonTypes.POSTFIX_INCREMENT_OP, CeylonTypes.POSTFIX_DECREMENT_OP, 
                CeylonTypes.SAFE_MEMBER_OP,
                CeylonTypes.RANGE_OP, CeylonTokens.RANGE_OP,
                CeylonTokens.LBRACKET
        );
        Collection<IElementType> typesRequiringNoRightSpacing = Arrays.asList(
                CeylonTokens.LPAREN,
                CeylonTypes.MEMBER_OP, CeylonTokens.MEMBER_OP,
                CeylonTokens.UNION_OP, CeylonTypes.UNION_OP, CeylonTokens.INTERSECTION_OP, CeylonTypes.INTERSECTION_OP,
                CeylonTypes.NOT_OP, CeylonTypes.TYPE_ARGUMENT_LIST,
                CeylonTypes.INCREMENT_OP, CeylonTypes.DECREMENT_OP,
                CeylonTypes.SEQUENCED_TYPE, CeylonTokens.LBRACKET, CeylonTypes.SAFE_MEMBER_OP,
                CeylonTypes.RANGE_OP, CeylonTokens.RANGE_OP,
                CeylonTypes.NOT_OP,
                CeylonTypes.SPREAD_OP
        );
        Collection<IElementType> typesRequiringNewLine = Arrays.asList(
                CeylonTypes.DECLARATION, CeylonTypes.IMPORT_MODULE
        );
        Collection<IElementType> typesRequiringEmptyLine = Arrays.asList(
                CeylonTypes.IMPORT_LIST, CeylonTypes.DECLARATION
        );

        IElementType type1 = block1 == null ? null : block1.node.getElementType();
        final IElementType type2 = block2.node.getElementType();

        Spacing result = null;
        if (block1 != null) {
            if (typesRequiringEmptyLine.contains(type1)) {
                result = EMPTY_LINE_SPACING;
            }
            if (typesRequiringNewLine.contains(type1) || (type1 == CeylonTokens.LBRACE &&
                    (block1.node.getTreeParent().getElementType() == CeylonTypes.BLOCK || block1.node.getTreeParent().getElementType() == CeylonTypes.IMPORT_MODULE_LIST))) {
                result = GOTO_LINE_SPACING;
            }
            if (typesRequiringNoRightSpacing.contains(type1) || typesRequiringNoLeftSpacing.contains(type2)
                    || (type1 == CeylonTypes.SMALLER_OP && type2 == CeylonTypes.TYPE_PARAMETER_DECLARATION)
                    || (type1 == CeylonTypes.TYPE_PARAMETER_LITERAL && type2 == CeylonTypes.LARGER_OP)) {
                result = NO_SPACING;
            }
        }
        
        if (result == null) {
//            final List<IElementType> bothTypes = Arrays.asList(type1, type2);
            if (Arrays.asList(CeylonTypes.TYPE_ARGUMENT_LIST, CeylonTypes.TYPE_PARAMETER_LIST, CeylonTypes.SEQUENCED_TYPE, CeylonTypes.INDEX_EXPRESSION).contains(node.getElementType())) {
                result = NO_SPACING;
            }
            if (BLOCK_TYPES.contains(node.getElementType()) && type1 == CeylonTokens.LBRACE && type2 == CeylonTokens.RBRACE) {
                result = NO_SPACING;
            }
            if (CeylonTypes.SPREAD_ARGUMENT.equals(node.getElementType()) && CeylonTokens.PRODUCT_OP.equals(type1)) {
                return NO_SPACING;
            }
        }

        if (result == null) {
            result = SINGLE_SPACE_SPACING;
        }

//        System.out.printf("Spacing between %s and %s in %s: %s%n", type1, type2, node.getElementType(), getSpacingName(result));
        return result;
    }

    private Object getSpacingName(Spacing result) {
        return result == EMPTY_LINE_SPACING ? "EMPTY_LINE_SPACING"
                : result == GOTO_LINE_SPACING ? "GOTO_LINE_SPACING"
                : result == NO_SPACING ? "NO_SPACING"
                : result == SINGLE_SPACE_SPACING ? "SINGLE_SPACE_SPACING"
                : result;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Override
    public boolean isIncomplete() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isLeaf() {
        return node.getFirstChildNode() == null;
    }
}
