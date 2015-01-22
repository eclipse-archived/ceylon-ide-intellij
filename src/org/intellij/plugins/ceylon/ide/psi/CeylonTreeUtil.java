package org.intellij.plugins.ceylon.ide.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class CeylonTreeUtil {

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
}
