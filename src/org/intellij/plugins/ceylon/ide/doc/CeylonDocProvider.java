package org.intellij.plugins.ceylon.ide.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.plugins.ceylon.ide.ceylonCode.doc.getDocumentation_.getDocumentation;

/**
 * @author Matija Mazi <br/>
 */
public class CeylonDocProvider extends AbstractDocumentationProvider {

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        try {
            return getDocumentation(((CeylonFile) element.getContainingFile()).getCompilationUnit(), element.getTextOffset());
        } catch (ceylon.language.AssertionError | Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        return contextElement;
    }
}
