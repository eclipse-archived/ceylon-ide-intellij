package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.intellij.plugins.ceylon.psi.CeylonNamedDeclaration;
import org.jetbrains.annotations.NotNull;

public class CeylonTypeReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        return new PsiReference[]{
                new CeylonTypeReference<CeylonNamedDeclaration>((CeylonNamedDeclaration) element, TextRange.from(0, element.getTextLength()), true)
        };
    }
}
