package org.intellij.plugins.ceylon.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.psi.PsiFile;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonTypedMethodOrAttributeDeclaration;
import org.jetbrains.annotations.NotNull;

public class CeylonFileTreeModel extends TextEditorBasedStructureViewModel implements StructureViewModel.ElementInfoProvider {
    public CeylonFileTreeModel(PsiFile psiFile) {
        super(psiFile);
    }

    @NotNull
    @Override
    public StructureViewTreeElement getRoot() {
        return new CeylonFileTreeElement((CeylonFile) getPsiFile());
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return element instanceof CeylonClassTreeElement;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element.getValue() instanceof CeylonTypedMethodOrAttributeDeclaration;
    }

    // TODO filters for inherited, fields, etc.
}
