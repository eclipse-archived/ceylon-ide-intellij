package org.intellij.plugins.ceylon.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CeylonBlock implements Block {
    private final ASTNode node;
    private final Indent indent;
    protected static final Spacing NO_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
    protected static final Spacing EMPTY_LINE_SPACING = Spacing.createSpacing(0, 0, 2, true, 0);
    protected static final Spacing GOTO_LINE_SPACING = Spacing.createSpacing(0, 0, 1, true, 1);
    protected static final Spacing SINGLE_SPACE_SPACING = Spacing.createSpacing(1, 1, 0, true, 1);

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
        List<Block> blocks = new ArrayList<Block>();

        ASTNode child = node.getFirstChildNode();

        while (child != null) {
            if (!FormatterUtil.containsWhiteSpacesOnly(child) && child.getTextLength() > 0) {
                if ((node.getElementType() == CeylonTypes.BLOCK || node.getElementType() == CeylonTypes.IMPORT_MODULE_LIST) && !(child.getElementType() == CeylonTypes.OP_RBRACE)) {
                    blocks.add(new CeylonBlock(child, Indent.getNormalIndent()));
                } else {
                    blocks.add(new CeylonBlock(child, Indent.getNoneIndent()));
                }
            }
            child = child.getTreeNext();
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

        List<IElementType> typesRequiringNoLeftSpacing = Arrays.asList(
                CeylonTypes.PARAMETERS, CeylonTypes.OP_RPAREN, CeylonTypes.OP_COMMA, CeylonTypes.OP_SEMI_COLUMN,
                CeylonTypes.OP_DOT
        );
        List<IElementType> typesRequiringNoRightSpacing = Arrays.asList(
                CeylonTypes.OP_LPAREN, CeylonTypes.OP_DOT
        );
        List<IElementType> typesRequiringNewLine = Arrays.asList(
                CeylonTypes.DECLARATION_OR_STATEMENT, CeylonTypes.IMPORT_MODULE
        );
        List<IElementType> typesRequiringEmptyLine = Arrays.asList(
                CeylonTypes.IMPORT_LIST, CeylonTypes.DECLARATION
        );

        if (block1 != null) {
            IElementType type1 = block1.node.getElementType();
            if (typesRequiringEmptyLine.contains(type1)) {
                return EMPTY_LINE_SPACING;
            }
            if (typesRequiringNewLine.contains(type1) || (type1 == CeylonTypes.OP_LBRACE &&
                    (block1.node.getTreeParent().getElementType() == CeylonTypes.BLOCK || block1.node.getTreeParent().getElementType() == CeylonTypes.IMPORT_MODULE_LIST))) {
                return GOTO_LINE_SPACING;
            }
            if (typesRequiringNoRightSpacing.contains(type1) || typesRequiringNoLeftSpacing.contains(block2.node.getElementType())) {
                return NO_SPACING;
            }
        }

        return SINGLE_SPACE_SPACING;
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
