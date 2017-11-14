/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import java.lang {
    Types {
        nativeString
    }
}

import com.intellij.codeInsight.completion {
    InsertionContext,
    InsertHandler
}
import com.intellij.codeInsight.lookup {
    LookupElementBuilder,
    LookupElement
}
import org.eclipse.ceylon.cmr.api {
    ModuleVersionDetails
}
import org.eclipse.ceylon.ide.common.completion {
    CommonCompletionProposal
}
import org.eclipse.ceylon.ide.common.platform {
    CommonDocument
}
import org.eclipse.ceylon.model.typechecker.model {
    Referenceable,
    Declaration
}

import javax.swing {
    Icon
}

import org.eclipse.ceylon.ide.intellij.platform {
    IdeaDocument
}
import org.eclipse.ceylon.ide.intellij.util {
    icons
}


shared interface IdeaCompletionProposal satisfies CommonCompletionProposal {
    
    shared actual void replaceInDoc(CommonDocument doc, Integer start, Integer length, String newText) {
        assert(is IdeaDocument doc);
        doc.nativeDocument.replaceString(start, start + length, nativeString(newText));
    }
    
    shared void adjustSelection(IdeaCompletionContext data) {
        value selection = getSelectionInternal(data.commonDocument);
        setSelection(data, selection.start, selection.end);
    }
    
    completionMode => "insert";
}

LookupElementBuilder newLookup(String description, String text, Icon? icon = null)
        => LookupElementBuilder.create(text, text)
            .withPresentableText(description)
            .withIcon(icon);

LookupElementBuilder newModuleLookup(String description, String text,
        ModuleVersionDetails version, Icon? icon = null)
        => LookupElementBuilder.create(version, text)
            .withPresentableText(description)
            .withIcon(icon);

LookupElementBuilder newDeclarationLookup(String description, String text,
        Referenceable declaration, Icon? icon = icons.forDeclaration(declaration),
        String? aliasedName = null, Declaration? qualifyingDeclaration = null) {

    value strikeout
            = if (is Declaration declaration)
            then declaration.deprecated
            else false;

    value lookupString
            = aliasedName
            else declaration.nameAsString;

    value qualifier
            = if (exists qualifyingDeclaration)
            then qualifyingDeclaration.name + "."
            else "";

    value fullLookupString
            = qualifier + lookupString + ":" + text.size.string;

    return LookupElementBuilder.create(declaration, fullLookupString)
            .withPresentableText(description)
            .withIcon(icon)
            .withStrikeoutness(strikeout);

}

void setSelection(IdeaCompletionContext data, Integer start, Integer end = start) {
    value editor = data.editor;
    editor.selectionModel.setSelection(start, end);
    editor.caretModel.moveToOffset(end);

}

shared class CompletionHandler(void handle(InsertionContext context))
        satisfies InsertHandler<LookupElement> {
    handleInsert(InsertionContext insertionContext, LookupElement? t)
            => handle(insertionContext);
}
