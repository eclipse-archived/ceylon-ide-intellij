import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document,
    IdeaTextChange = TextChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.intellij.psi {
    PsiFile
}

shared alias IdeaImportProposals => ImportProposals<CeylonFile, LookupElement, Document, InsertEdit, IdeaTextChange, TextChange>;

shared object ideaImportProposals
        satisfies ImportProposals<CeylonFile, LookupElement, Document, InsertEdit, IdeaTextChange, TextChange>
                & IdeaDocumentChanges {

    shared actual Indents<Document> indents => ideaIndents;
    
    shared actual TextChange createImportChange(CeylonFile file)
            => nothing;
    
    shared actual LookupElement newImportProposal(String description, TextChange correctionChange)
            => nothing;
}
