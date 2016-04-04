package org.intellij.plugins.ceylon.ide.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.changeSignature.ChangeSignatureHandler;
import org.intellij.plugins.ceylon.ide.ceylonCode.refactoring.CeylonChangeSignatureHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return false; // see org.intellij.plugins.ceylon.ide.refactoring.CeylonVariableRenameHandler
    }

    @Nullable
    @Override
    public ChangeSignatureHandler getChangeSignatureHandler() {
        return new CeylonChangeSignatureHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceVariableHandler() {
        return new IntroduceVariableHandler();
    }
}
