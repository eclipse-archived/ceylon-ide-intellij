package org.intellij.plugins.ceylon.ide.refactoring;

import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenameHandler;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.DeclarationPsiNameIdOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonVariableRenameHandler extends VariableInplaceRenameHandler {

    @Nullable
    @Override
    protected VariableInplaceRenamer createRenamer(@NotNull final PsiElement elementToRename,
                                                   final Editor editor) {
        final PsiFile file = elementToRename.getContainingFile();

        return new VariableInplaceRenamer((PsiNamedElement) elementToRename, editor) {
            @Override
            public void finish(boolean success) {
                super.finish(success);

                if (success && file instanceof CeylonFile) {
                    new WriteCommandAction(myProject, file) {
                        @Override
                        protected void run(@NotNull Result result) throws Throwable {
                            ((CeylonFile) file).forceReparse();
                        }
                    }.execute();
                }
            }
        };
    }

    @Override
    protected boolean isAvailable(@Nullable PsiElement element, Editor editor, PsiFile file) {
        PsiElement context = file.findElementAt(editor.getCaretModel().getOffset());

        if (context != null && element != null &&
                context.getContainingFile() != element.getContainingFile()) {
            return false;
        }

        return element instanceof DeclarationPsiNameIdOwner;
    }
}
