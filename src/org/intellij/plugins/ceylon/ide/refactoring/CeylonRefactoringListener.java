package org.intellij.plugins.ceylon.ide.refactoring;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.DeclarationPsiNameIdOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonRefactoringListener implements RefactoringElementListenerProvider {

    @Nullable
    @Override
    public RefactoringElementListener getListener(PsiElement element) {
        if (element instanceof DeclarationPsiNameIdOwner) {
            return new RefactoringElementListener() {
                @Override
                public void elementMoved(@NotNull PsiElement newElement) {
                }

                @Override
                public void elementRenamed(@NotNull PsiElement newElement) {
                    final CeylonFile file = (CeylonFile) newElement.getContainingFile();
                    new WriteCommandAction(file.getProject()) {
                        @Override
                        protected void run(@NotNull Result result) throws Throwable {
                            file.forceReparse(file.getViewProvider().getDocument());
                        }
                    }.execute();
                }
            };
        }
        return null;
    }
}
