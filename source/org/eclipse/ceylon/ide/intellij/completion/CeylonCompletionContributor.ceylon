/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.codeInsight.completion {
    CompletionContributor,
    CompletionInitializationContext,
    CompletionType
}
import com.intellij.patterns {
    PlatformPatterns
}
import org.eclipse.ceylon.ide.common.settings {
    CompletionOptions
}

import org.eclipse.ceylon.ide.intellij.completion {
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
