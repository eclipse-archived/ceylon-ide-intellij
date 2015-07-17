package org.intellij.plugins.ceylon.ide.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Unit;
import org.intellij.plugins.ceylon.ide.CeylonLanguage;
import org.jetbrains.annotations.Nullable;

public class CeylonTreeUtil {

    public static CeylonPsi.DeclarationPsi createDeclarationFromText(Project project, String code) {
        PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(CeylonLanguage.INSTANCE, code);
        return PsiTreeUtil.getParentOfType(file.findElementAt(0), CeylonPsi.DeclarationPsi.class);
    }

    /**
     * Finds a PSI element corresponding to the original Node in a given file.
     *
     * @param ceylonNode the node to look for
     * @param file where the PSI node is expected to be
     * @return the corresponding PSI element
     */
    public static PsiElement findPsiElement(Node ceylonNode, PsiFile file) {
        if (ceylonNode == null) {
            return null;
        }

        PsiElement candidate = PsiUtilCore.getElementAtOffset(file, ceylonNode.getStartIndex());

        while (!(candidate instanceof PsiFile)) {
            if (candidate instanceof CeylonCompositeElement) {
                Node candidateCeylonNode = ((CeylonCompositeElement) candidate).getCeylonNode();
                if (candidateCeylonNode instanceof Tree.ParameterDeclaration) {
                    candidateCeylonNode = ((Tree.ParameterDeclaration) candidateCeylonNode).getTypedDeclaration();
                }
                if (candidateCeylonNode == ceylonNode) {
                    return candidate;
                }
            }
            candidate = candidate.getParent();
        }

        throw new IllegalArgumentException(String.format("No PSI node found for ceylon node of type %s at (%d-%d).%n",
                ceylonNode.getNodeType(), ceylonNode.getStartIndex(), ceylonNode.getStopIndex()));
    }

    @Nullable
    public static PsiFile getDeclaringFile(Unit unit, Project project) {
        String protocol = unit.getFullPath().contains("!/") ? "jar://" : "file://";
        VirtualFile vfile = VirtualFileManager.getInstance().findFileByUrl(protocol + unit.getFullPath());
        if (vfile != null) {
            return PsiManager.getInstance(project).findFile(vfile);
        }

        return null;
    }
}
