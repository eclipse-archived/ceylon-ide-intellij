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
import org.eclipse.ceylon.cmr.api {
    ModuleVersionDetails,
    ModuleSearchResult
}
import org.eclipse.ceylon.compiler.typechecker.tree {
    Node
}
import org.eclipse.ceylon.ide.common.completion {
    ModuleProposal,
    ProposalsHolder
}
import org.eclipse.ceylon.ide.common.platform {
    LinkedMode
}
import org.eclipse.ceylon.ide.common.refactoring {
    DefaultRegion
}

import org.eclipse.ceylon.ide.intellij.util {
    icons
}

class IdeaModuleCompletionProposal(Integer offset, String prefix,
        Integer len, String versioned, ModuleSearchResult.ModuleDetails mod,
        Boolean withBody, ModuleVersionDetails version,
        String name, Node node, IdeaCompletionContext ctx)
        extends ModuleProposal
        (offset, prefix, len, versioned, mod, withBody, version, name, node, ctx)
        satisfies IdeaCompletionProposal {

    shared LookupElement lookupElement
            => newModuleLookup {
                description = versioned;
                text = versioned.spanFrom(len);
                icon = icons.moduleArchives;
                version = version;
            }
            .withInsertHandler(CompletionHandler((context) {
               // Undo IntelliJ's completion
               value doc = ctx.commonDocument;

               replaceInDoc {
                   doc = doc;
                   start = offset;
                   length = text.size - prefix.size;
                   newText = "";
               };

               applyInternal(doc);
            }));

    "Used for nested proposals (autocompletion of module versions)."
    shared actual void newModuleProposal(ProposalsHolder proposals,
            ModuleVersionDetails details, DefaultRegion selection, LinkedMode linkedMode) {
        if (is IdeaProposalsHolder proposals) {
            value version = details.version;
            proposals.add(newLookup {
                description = version;
                text = version;
                icon = icons.moduleArchives;
            }
            .withInsertHandler((context, element) =>
                setSelection {
                    data = ctx;
                    start = selection.start+element.lookupString.size+2;
                    end = selection.start+element.lookupString.size+2;
                }
            ));
        }
    }

    toggleOverwrite => false;
}
