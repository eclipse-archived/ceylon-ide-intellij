package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CeylonImportListTreeElement extends PsiTreeElementBase<PsiElement> implements SortableTreeElement {

    private CeylonPsi.ImportPsi[] imports;

    protected CeylonImportListTreeElement(CeylonPsi.ImportPsi[] imports) {
        super(imports[0].getParent());
        this.imports = imports;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        List<StructureViewTreeElement> nodes = new ArrayList<>();

        for (CeylonPsi.ImportPsi child : imports) {
            nodes.add(new CeylonImportTreeElement(child));
        }
        return nodes;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return "Imports";
    }

    @Override
    public Icon getIcon(boolean open) {
        return ideaIcons_.get_().getImports();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        return "";
    }
}

class CeylonImportTreeElement extends PsiTreeElementBase<CeylonPsi.ImportPsi> implements SortableTreeElement {

    protected CeylonImportTreeElement(CeylonPsi.ImportPsi psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return getElement().getText().substring(6, getElement().getText().indexOf('{')).trim();
//        return "import " + getElement().getCeylonNode().getImportPath().getModel().getNameAsString();
    }

    @Override
    public Icon getIcon(boolean open) {
        return ideaIcons_.get_().getSingleImport();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        return getElement().getText();
    }

}