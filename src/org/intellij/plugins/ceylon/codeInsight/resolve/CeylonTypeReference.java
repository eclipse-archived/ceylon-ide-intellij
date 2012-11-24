package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonQualifiedType;
import org.intellij.plugins.ceylon.psi.CeylonSupertypeQualifier;
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
        CeylonQualifiedType qualifiedType = PsiTreeUtil.getParentOfType(myElement, CeylonQualifiedType.class);
        String name = myElement.getText();

        if (qualifiedType != null) {
            // TODO surely incomplete
            CeylonSupertypeQualifier supertypeQualifier = qualifiedType.getSupertypeQualifier();
            if (supertypeQualifier != null && !supertypeQualifier.getTypeName().equals(myElement)) {
                name = supertypeQualifier.getTypeName().getText() + "." + name;
            }
        }

        if (name == null) {
            return null;
        }
        Collection<CeylonClass> decls = ClassIndex.getInstance().get(name, myElement.getProject(),
                GlobalSearchScope.projectScope(myElement.getProject()));

        if (!decls.isEmpty()) {
            return decls.iterator().next().getNameIdentifier();
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }
}
