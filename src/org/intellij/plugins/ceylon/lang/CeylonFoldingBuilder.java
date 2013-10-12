package org.intellij.plugins.ceylon.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.intellij.plugins.ceylon.psi.CeylonTypes.*;

public class CeylonFoldingBuilder extends FoldingBuilderEx {
    private static final List<IElementType> blockTypes = Arrays.asList(
            TYPE, ABSTRACTED_TYPE, ANY_CLASS, ANY_INTERFACE, ANY_METHOD, FOR_STATEMENT, SWITCH_STATEMENT, IF_STATEMENT, IMPORT_LIST);

    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();

        appendDescriptors(root, descriptors);

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private void appendDescriptors(PsiElement root, List<FoldingDescriptor> descriptors) {
        if (blockTypes.contains(root.getNode().getElementType())) {
            descriptors.add(new FoldingDescriptor(root.getNode(), root.getTextRange()));
        }

        for (PsiElement child : root.getChildren()) {
            appendDescriptors(child, descriptors);
        }
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        return "{...}";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
