import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementBuilder
}
import com.intellij.openapi.editor {
    Document
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.ide.common.util {
    Indents
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}

shared alias IdeaImportProposals => ImportProposals<CeylonFile, LookupElement, Document, InsertEdit, TextEdit, TextChange>;

shared object ideaImportProposals
        satisfies ImportProposals<CeylonFile, LookupElement, Document, InsertEdit, TextEdit, TextChange>
                & IdeaDocumentChanges {

    shared actual Indents<Document> indents => ideaIndents;

    shared actual TextChange createImportChange(CeylonFile file)
            => TextChange(file.viewProvider.document);

    shared actual LookupElement newImportProposal(String description, TextChange correctionChange)
            => LookupElementBuilder.create(correctionChange, description);
}
