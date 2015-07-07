package org.intellij.plugins.ceylon.ide.structureView;

import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.FileStructureFilter;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import org.intellij.plugins.ceylon.ide.CeylonBundle;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;

/**
 * Filters out declarations that are not shared in the tool window.
 */
public class UnsharedDeclarationsFilter implements /* FileStructureFilter*/ Filter {

    @Override
    public boolean isVisible(TreeElement treeNode) {
        if (treeNode instanceof PsiTreeElementBase) {
            PsiElement element = ((PsiTreeElementBase) treeNode).getElement();

            if (element instanceof CeylonPsi.DeclarationPsi) {
                Declaration model = ((CeylonPsi.DeclarationPsi) element).getCeylonNode().getDeclarationModel();

                return model.isShared();
            }
        }
        return true;
    }

    @Override
    public boolean isReverted() {
        return false;
    }

    @NotNull
    @Override
    public ActionPresentation getPresentation() {
        return new ActionPresentationData(CeylonBundle.message("ceylon.structure.hide.unshared"), null, PlatformIcons.PUBLIC_ICON);
    }

    @NotNull
    @Override
    public String getName() {
        return "CEYLON_HIDE_UNSHARED";
    }

    @NotNull
//    @Override
    public String getCheckBoxText() {
        return CeylonBundle.message("ceylon.structure.hide.unshared");
    }

    @NotNull
//    @Override
    public Shortcut[] getShortcut() {
        return new Shortcut[] {
                KeyboardShortcut.fromString(SystemInfo.isMac ? "meta U" : "control U")
        };
    }
}
