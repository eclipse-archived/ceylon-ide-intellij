package org.intellij.plugins.ceylon.ide.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.redhat.ceylon.ide.common.settings.CompletionOptions;
import org.intellij.plugins.ceylon.ide.ceylonCode.completion.IdeaCompletionProvider;
import org.intellij.plugins.ceylon.ide.settings.CompletionSettings;
import org.jetbrains.annotations.NotNull;

public class CeylonCompletionContributor extends CompletionContributor {

    public CeylonCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(), new IdeaCompletionProvider() {
            @Override
            public CompletionOptions getOptions() {
                return CompletionSettings.getInstance().getOptions();
            }
        });
    }

    @Override
    public void beforeCompletion(@NotNull CompletionInitializationContext context) {
        super.beforeCompletion(context);
        context.setDummyIdentifier(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED);
    }
}