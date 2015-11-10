package org.intellij.plugins.ceylon.ide.refactoring;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenameProcessor;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.jetbrains.annotations.NotNull;

public class CeylonRenamePsiProcessor extends RenamePsiElementProcessor {
    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return true;
    }

    @Override
    public RenameDialog createRenameDialog(Project project, final PsiElement element, final PsiElement nameSuggestionContext, final Editor editor) {
        final PsiFile containingFile = nameSuggestionContext.getContainingFile();
        return new RenameDialog(project, element, nameSuggestionContext, editor) {
            @Override
            protected RenameProcessor createRenameProcessor(String newName) {
                return new RenameProcessor(getProject(), element, newName, isSearchInComments(),
                        isSearchInNonJavaFiles()) {

                    @Override
                    protected void performPsiSpoilingRefactoring() {
                        super.performPsiSpoilingRefactoring();

                        if (containingFile != element.getContainingFile()) {
                            ((CeylonFile) containingFile).forceReparse();
                        }
                    }
                };
            }
        };
    }
}
