package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CeylonFoldingBuilder extends FoldingBuilderEx {

    private static final List<com.intellij.psi.tree.IElementType> BLOCK_ELEMENT_TYPES
            = Arrays.asList(CeylonTypes.BLOCK, CeylonTypes.CLASS_BODY, CeylonTypes.INTERFACE_BODY,
                            CeylonTypes.IMPORT_MODULE_LIST);

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        appendDescriptors(root, descriptors);

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private void appendDescriptors(PsiElement root, List<FoldingDescriptor> descriptors) {
        if (root instanceof CeylonPsi.BodyPsi) {
            descriptors.add(new FoldingDescriptor(root.getNode(), root.getTextRange()));
        } else if (root instanceof CeylonPsi.ImportModuleListPsi) {
            descriptors.add(new FoldingDescriptor(root.getNode(), root.getTextRange()));
        } else if (root.getNode().getElementType() == CeylonTokens.MULTI_COMMENT) {
            foldAfterFirstLine(root, descriptors);
        } else if (root.getNode().getElementType() == CeylonTokens.LINE_COMMENT) {
            PsiElement prev = PsiTreeUtil.prevVisibleLeaf(root);
            if (prev == null || prev.getNode().getElementType() != CeylonTokens.LINE_COMMENT) {
                PsiElement lastComment = root;
                PsiElement next;

                do {
                    next = PsiTreeUtil.nextVisibleLeaf(lastComment);
                    if (next != null
                            && next.getNode().getElementType() == CeylonTokens.LINE_COMMENT) {
                        lastComment = next;
                    } else {
                        next = null;
                    }
                } while (next != null);

                if (lastComment != root) {
                    TextRange range = new TextRange(
                            root.getTextRange().getEndOffset() - 1,
                            lastComment.getTextRange().getEndOffset() - 1
                    );
                    descriptors.add(new FoldingDescriptor(root.getNode(), range));
                }
            }
        } else if (root instanceof CeylonPsi.StringLiteralPsi) {
            foldAfterFirstLine(root, descriptors);
        } else if (root instanceof CeylonPsi.ImportListPsi && root.getTextLength() > 0) {
            int start = root.getTextRange().getStartOffset();
            if (root.getText().startsWith("import ")) {
                start += "import ".length();
            }
            TextRange range = TextRange.create(
                    start,
                    root.getTextRange().getEndOffset()
            );
            descriptors.add(new FoldingDescriptor(root.getNode(), range));
        }

        PsiElement child = root.getFirstChild();

        while (child != null) {
            appendDescriptors(child, descriptors);
            child = child.getNextSibling();
        }
    }

    private void foldAfterFirstLine(PsiElement root, List<FoldingDescriptor> descriptors) {
        int newLine = root.getText().indexOf('\n');
        if (newLine > 0) {
            TextRange range = new TextRange(root.getTextRange().getStartOffset() + newLine,
                    root.getTextRange().getEndOffset());
            descriptors.add(new FoldingDescriptor(root.getNode(), range));
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        if (BLOCK_ELEMENT_TYPES.contains(node.getElementType())) {
            String text = normalize(node.getText());
            return text.length() < 40 ? text : "{...}";
        }
        else {
            return "...";
        }
    }

    @NotNull
    private String normalize(@NotNull String text) {
        return ceylon.language.String.instance(text).getNormalized().trim().toString();
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return node.getElementType() == CeylonTypes.IMPORT_LIST;
    }
}
