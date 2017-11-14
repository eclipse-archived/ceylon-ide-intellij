/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.codeInsight.lookup {
    LookupElement
}
import org.eclipse.ceylon.ide.common.completion {
    FunctionCompletionProposal
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.ceylon.ide.intellij.util {
    icons
}

class IdeaFunctionCompletionProposal
        (Integer offset, String prefix, String desc, String text, Declaration decl, IdeaCompletionContext ctx)
        extends FunctionCompletionProposal(offset, prefix, desc, text, decl, ctx.lastCompilationUnit)
        satisfies IdeaCompletionProposal {

    shared actual variable Boolean toggleOverwrite = false;
    
    shared LookupElement lookupElement
            => newLookup {
                description = desc;
                text = text;
                icon = icons.surround;
            };
}
