package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.ide.actions.QualifiedNameProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.Nullable;

public class CeylonQualifiedNameProvider implements QualifiedNameProvider {
    @Nullable
    @Override
    public PsiElement adjustElementToCopy(PsiElement element) {
        if (element instanceof CeylonCompositeElement) {
            CeylonCompositeElement ceylonElement = (CeylonCompositeElement) element;
            if (ceylonElement instanceof CeylonPsi.IdentifierPsi) {
                ceylonElement = (CeylonCompositeElement) element.getParent();
            }
            return ceylonElement;
        }
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedName(PsiElement element) {
        if (element instanceof CeylonCompositeElement) {
            CeylonCompositeElement ceylonElement = (CeylonCompositeElement) element;
            Node ceylonNode = ceylonElement.getCeylonNode();
            if (ceylonNode instanceof Tree.Declaration) {
                Tree.Declaration declaration = (Tree.Declaration) ceylonNode;
                return declaration.getDeclarationModel().getQualifiedNameString();
            }
        }
        return null;
    }

    @Override
    public PsiElement qualifiedNameToElement(String fqn, Project project) {
        //TODO: Copy reference #144. Implement after https://github.com/ceylon/ceylon-ide-common/issues/57 .
        return null;
    }

    @Override
    public void insertQualifiedName(String fqn, PsiElement element, Editor editor, Project project) {
        //TODO: Copy reference #144. Implement after https://github.com/ceylon/ceylon-ide-common/issues/57 .
    }
}
