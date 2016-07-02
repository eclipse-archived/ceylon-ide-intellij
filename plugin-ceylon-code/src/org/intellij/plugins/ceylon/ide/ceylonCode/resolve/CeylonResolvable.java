package org.intellij.plugins.ceylon.ide.ceylonCode.resolve;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.common.Backends;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.CeylonCompositeElementImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CeylonResolvable extends CeylonCompositeElementImpl implements PsiNamedElement {

    public CeylonResolvable(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        PsiElement parent = getParent();
        if (parent instanceof CeylonPsi.ImportPathPsi) {
            TextRange parentRange = TextRange.from(0, parent.getTextLength());
            return new CeylonReference<>(parent, parentRange, true);
        }
        else {
            TextRange range = TextRange.from(0, getTextLength());
            return new CeylonReference<>(this, range, true);
        }
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        PsiElement parent = getParent();
        if (parent instanceof CeylonPsi.MemberOrTypeExpressionPsi) {
            CeylonPsi.MemberOrTypeExpressionPsi refPsi =
                    (CeylonPsi.MemberOrTypeExpressionPsi) parent;
            Tree.MemberOrTypeExpression node = refPsi.getCeylonNode();
            Declaration model = node.getDeclaration();
            if (model!=null && model.isNative()) {
                ArrayList<PsiReference> list = new ArrayList<>();
                addBackend(list, Backends.HEADER);
                addBackend(list, Backends.JAVA);
                addBackend(list, Backends.JS);
                return list.toArray(new PsiReference[0]);
            }
        }
        return super.getReferences();
    }

    private void addBackend(ArrayList<PsiReference> list, Backends backend) {
        TextRange range = TextRange.from(0, getTextLength());
        list.add(new CeylonReference<>(this, range, true, backend));
    }

    @Override
    public String getName() {
        if (this instanceof CeylonPsi.IdentifierPsi) {
            return getText();
        }
        else {
            return null;
        }
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Not yet");
    }
}
