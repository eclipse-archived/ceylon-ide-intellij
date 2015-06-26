package org.intellij.plugins.ceylon.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.intellij.plugins.ceylon.ide.ceylonCode.completion.ideaCompletionManager_;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.jetbrains.annotations.NotNull;

public class CeylonCompletionContributor extends CompletionContributor {

    public CeylonCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new MyCompletionProvider());
    }

    private static class MyCompletionProvider extends CompletionProvider<CompletionParameters> {
        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
            PsiElement element = parameters.getOriginalPosition();

            if (element != null) {
                ideaCompletionManager_.get_().addCompletions(parameters, context, result, ((CeylonFile) element.getContainingFile()).getCompilationUnit());
            }
        }
    }

}