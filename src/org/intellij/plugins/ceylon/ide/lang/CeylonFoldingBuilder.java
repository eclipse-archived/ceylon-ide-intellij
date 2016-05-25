package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class CeylonFoldingBuilder extends FoldingBuilderEx {

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

        for (PsiElement child : root.getChildren()) {
            appendDescriptors(child, descriptors);
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return node.getElementType() == CeylonTypes.IMPORT_LIST ? "..." : "{...}";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return node.getElementType() == CeylonTypes.IMPORT_LIST;
    }
}
