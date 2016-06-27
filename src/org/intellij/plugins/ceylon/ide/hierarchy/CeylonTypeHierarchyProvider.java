package org.intellij.plugins.ceylon.ide.hierarchy;

import com.intellij.ide.hierarchy.HierarchyBrowser;
import com.intellij.ide.hierarchy.HierarchyProvider;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonTypeHierarchyProvider implements HierarchyProvider {

    @Override
    public void browserActivated(@NotNull HierarchyBrowser hierarchyBrowser) {
        ((CeylonTypeHierarchyBrowser)hierarchyBrowser)
                .changeView(CeylonTypeHierarchyBrowser.TYPE_HIERARCHY_TYPE);
    }

    @Nullable
    @Override
    public PsiElement getTarget(@NotNull DataContext dataContext) {
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (project == null) return null;
        PsiElement psi = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
        if (psi instanceof CeylonPsi.ClassOrInterfacePsi
         || psi instanceof CeylonPsi.ObjectDefinitionPsi
         || psi instanceof CeylonPsi.ObjectArgumentPsi) {
            return psi;
        }
        Caret caret = CommonDataKeys.CARET.getData(dataContext);
        PsiFile file = CommonDataKeys.PSI_FILE.getData(dataContext);
        psi = file.findElementAt(caret.getOffset());
        while (!(psi == null
              || psi instanceof CeylonPsi.ClassOrInterfacePsi
              || psi instanceof CeylonPsi.ObjectDefinitionPsi
              || psi instanceof CeylonPsi.ObjectExpressionPsi
              || psi instanceof CeylonPsi.ObjectArgumentPsi)) {
            psi = psi.getParent();
        }
        return psi;
    }

    @NotNull
    @Override
    public HierarchyBrowser createHierarchyBrowser(PsiElement psiElement) {
        return new CeylonTypeHierarchyBrowser(psiElement.getProject(), psiElement);
    }
}
