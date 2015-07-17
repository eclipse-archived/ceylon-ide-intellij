package org.intellij.plugins.ceylon.ide.refactoring;

import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import org.intellij.plugins.ceylon.ide.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsiVisitor;
import org.jetbrains.annotations.Nullable;

public class FindMatchingPsiNodeVisitor extends CeylonPsiVisitor {

    private Node node;
    private Class<? extends CeylonCompositeElement> klass;
    private CeylonCompositeElement psi;

    public FindMatchingPsiNodeVisitor(Node node, Class<? extends CeylonCompositeElement> klass) {
        super(true);
        this.node = node;
        this.klass = klass;
    }

    @Override
    public void visitElement(PsiElement element) {
        super.visitElement(element);

        if (node.getStartIndex().equals(element.getTextOffset())
                && node.getStopIndex() == (element.getTextOffset() + element.getTextLength() - 1)
                && klass.isAssignableFrom(element.getClass())) {
            psi = (CeylonCompositeElement) element;
        }
    }

    @Nullable
    public CeylonCompositeElement getPsi() {
        return psi;
    }
}
