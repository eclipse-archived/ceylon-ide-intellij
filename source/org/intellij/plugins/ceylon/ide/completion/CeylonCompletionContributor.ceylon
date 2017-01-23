import com.intellij.codeInsight.completion {
    CompletionContributor,
    CompletionInitializationContext,
    CompletionType
}
import com.intellij.patterns {
    PlatformPatterns
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}

import org.intellij.plugins.ceylon.ide.completion {
    IdeaCompletionProvider
}

shared variable CompletionOptions completionOptions = CompletionOptions();

shared class CeylonCompletionContributor extends CompletionContributor {

    shared new() extends CompletionContributor() {
        extend(
            CompletionType.basic,
            PlatformPatterns.psiElement(),
            IdeaCompletionProvider()
        );
    }

    shared actual void beforeCompletion(CompletionInitializationContext context) {
        super.beforeCompletion(context);
        context.dummyIdentifier = CompletionInitializationContext.dummyIdentifierTrimmed;
    }
}
