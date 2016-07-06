package org.intellij.plugins.ceylon.ide.refactoring;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.ceylonLanguage_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;

public class IdentifierElementManipulator extends AbstractElementManipulator<CeylonPsi.IdentifierPsi> {
    @Override
    public CeylonPsi.IdentifierPsi handleContentChange(@NotNull CeylonPsi.IdentifierPsi element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        PsiFile file = PsiFileFactory.getInstance(element.getProject()).createFileFromText(ceylonLanguage_.get_(), newContent);
        PsiElement identifier = file.findElementAt(0);
        return (CeylonPsi.IdentifierPsi) element.replace(identifier.getParent());
    }
}
