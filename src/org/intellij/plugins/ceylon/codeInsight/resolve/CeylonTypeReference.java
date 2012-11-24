package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import org.intellij.plugins.ceylon.psi.CeylonNamedDeclaration;
import org.intellij.plugins.ceylon.psi.stub.ClassIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CeylonTypeReference<T extends PsiElement> extends PsiReferenceBase<T> {

    public CeylonTypeReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        // TODO fqn index
        Collection<CeylonNamedDeclaration> decls = ClassIndex.getInstance().get(myElement.getText(), myElement.getProject(),
                GlobalSearchScope.projectScope(myElement.getProject()));

        if (decls.isEmpty()) {
            return null;
        }

        return decls.iterator().next().getTypeNameDeclaration().getTypeName();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }
}
