package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.NodeProvider;
import com.intellij.psi.PsiFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CeylonFileTreeModel extends TextEditorBasedStructureViewModel implements StructureViewModel.ElementInfoProvider {
    public CeylonFileTreeModel(PsiFile psiFile) {
        super(psiFile);
    }

    @NotNull
    @Override
    public Collection<NodeProvider> getNodeProviders() {
        return super.getNodeProviders();
    }

    @NotNull
    @Override
    public StructureViewTreeElement getRoot() {
        return new CeylonFileTreeElement((CeylonFile) getPsiFile());
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof CeylonMethodTreeElement;
    }

    // TODO filters for inherited, fields, etc.
}
