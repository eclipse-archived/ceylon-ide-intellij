package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import java.util.Objects;
import java.util.concurrent.Callable;

import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.ConcurrencyManagerForJava;
import org.jetbrains.annotations.Nullable;

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
import com.redhat.ceylon.ide.common.model.CeylonUnit;
import com.redhat.ceylon.model.typechecker.model.Unit;

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
                } else if (candidateCeylonNode.getClass() == ceylonNode.getClass()
                        && Objects.equals(candidateCeylonNode.getStartIndex(), ceylonNode.getStartIndex())
                        && Objects.equals(candidateCeylonNode.getEndIndex(), ceylonNode.getEndIndex())) {
                    // TODO if this file has never been opened in the editor, the compilation
                    // unit is not the same as the one that contains ceylonNode, so we can't use ==
                    return candidate;
                }
            }
            candidate = candidate.getParent();
        }

        throw new IllegalArgumentException(String.format("No PSI node found for ceylon node of type %s at (%d-%d).%n",
                ceylonNode.getNodeType(), ceylonNode.getStartIndex(), ceylonNode.getEndIndex()));
    }

    @Nullable
    public static PsiFile getDeclaringFile(Unit unit, final Project project) {
        if (unit instanceof CeylonUnit) {
            CeylonUnit ceylonUnit = (CeylonUnit) unit;
            String path = ceylonUnit.getSourceFullPath().toString();
            String protocol = path.contains("!/") ? "jar://" : "file://";
            final VirtualFile vfile = VirtualFileManager.getInstance().findFileByUrl(protocol + path);
            if (vfile != null) {
                return ConcurrencyManagerForJava.needReadAccess(new Callable<PsiFile>() {
                    @Override
                    public PsiFile call() throws Exception {
                        return PsiManager.getInstance(project).findFile(vfile);
                    }
                });
            }
        }

        return null;
    }
}
