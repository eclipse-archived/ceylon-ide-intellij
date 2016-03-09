import com.redhat.ceylon.ide.common.completion {
    TypeProposal
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    InsertEdit,
    TextEdit,
    IdeaDocumentChanges,
    TextChange,
    ideaImportProposals
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

class IdeaTypeProposal(Integer offset, Type? type, String text, String desc, Tree.CompilationUnit rootNode)
        extends TypeProposal<CeylonFile, LookupElement, Document, InsertEdit, TextEdit, TextChange, TextRange>
        (offset, type, text, desc, rootNode)
        satisfies IdeaDocumentChanges & IdeaCompletionProposal {
    
    shared actual ImportProposals<CeylonFile,LookupElement,Document,InsertEdit,TextEdit,TextChange> importProposals
            => ideaImportProposals;
    
    shared actual Boolean toggleOverwrite => false;
    
    shared LookupElement lookupElement => newLookup(desc, text)
        .withIcon(if (exists type) then ideaIcons.forDeclaration(type.declaration) else null);
    
}