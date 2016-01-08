package org.intellij.plugins.ceylon.ide.ceylonCode.resolve;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.CeylonCompositeElementImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CeylonResolvable extends CeylonCompositeElementImpl implements PsiNamedElement {

    public CeylonResolvable(ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        TextRange range = TextRange.from(0, getTextLength());

        if (getParent() instanceof CeylonPsi.ImportPathPsi) {
            TextRange parentRange = TextRange.from(0, getParent().getTextLength());
            return new CeylonReference<>(getParent(), parentRange, true);
        }
        return new CeylonReference<>(this, range, true);
    }

    @Override
    public String getName() {
        if (this instanceof CeylonPsi.IdentifierPsi) {
            return getText();
        }

        return super.getName();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        throw new UnsupportedOperationException("Not yet");
    }
}
