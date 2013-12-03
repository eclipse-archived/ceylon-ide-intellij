package org.intellij.plugins.ceylon.psi;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.util.RefactoringDescriptionLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonElementDescriptionProvider implements ElementDescriptionProvider {
    @Nullable
    @Override
    public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
        if (!(location instanceof RefactoringDescriptionLocation)) return null;
        RefactoringDescriptionLocation rdLocation = (RefactoringDescriptionLocation) location;

        if (element instanceof CeylonPsi.AttributeDeclarationPsi) {
            return "attribute";
        }
        return null;
    }
}
