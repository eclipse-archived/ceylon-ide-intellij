package org.intellij.plugins.ceylon.ide.codeInsight.resolve;

import com.intellij.codeInsight.TargetElementEvaluatorEx2;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonTargetElementEvaluator extends TargetElementEvaluatorEx2 {
    @Nullable
    @Override
    public PsiElement getElementByReference(@NotNull PsiReference ref, int flags) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNamedElement(@NotNull PsiElement element) {
        if (element.getParent() instanceof CeylonPsi.IdentifierPsi
                && element.getParent().getParent() instanceof CeylonPsi.DeclarationPsi) {
            return element.getParent().getParent();
        }

        return null;
    }
}
