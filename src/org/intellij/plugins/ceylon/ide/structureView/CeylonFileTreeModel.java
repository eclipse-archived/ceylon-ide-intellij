package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.NodeProvider;
import com.intellij.psi.PsiFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

class CeylonFileTreeModel extends TextEditorBasedStructureViewModel
        implements StructureViewModel.ElementInfoProvider, StructureViewModel.ExpandInfoProvider {

    private static final Filter[] FILTERS = new Filter[]{new UnsharedDeclarationsFilter()};
    private static final List<NodeProvider> NODE_PROVIDERS = Collections.<NodeProvider>singletonList(
            new CeylonInheritedMembersNodeProvider()
    );

    CeylonFileTreeModel(PsiFile psiFile) {
        super(psiFile);
    }

    @NotNull
    @Override
    public Collection<NodeProvider> getNodeProviders() {
        return NODE_PROVIDERS;
    }

    @NotNull
    @Override
    public StructureViewTreeElement getRoot() {
        return new CeylonFileTreeElement((CeylonFile) getPsiFile());
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return true;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof CeylonFunctionTreeElement || element instanceof CeylonAttributeTreeElement;
    }

    @Override
    public boolean isAutoExpand(@NotNull StructureViewTreeElement element) {
        return !(element instanceof CeylonImportListTreeElement);
    }

    @Override
    public boolean isSmartExpand() {
        return false;
    }

    @NotNull
    @Override
    public Filter[] getFilters() {
        return FILTERS;
    }

    @NotNull
    @Override
    protected Class[] getSuitableClasses() {
        return new Class[] {CeylonPsi.DeclarationPsi.class, CeylonPsi.SpecifierStatementPsi.class};
    }

    // TODO filters for inherited, fields, etc.
}
