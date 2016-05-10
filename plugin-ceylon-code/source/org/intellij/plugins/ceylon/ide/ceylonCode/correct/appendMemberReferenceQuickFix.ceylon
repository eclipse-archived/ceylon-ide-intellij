import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.correct {
    AppendMemberReferenceQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

object ideaAppendMemberReferenceQuickFix
        satisfies AppendMemberReferenceQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        TextChange change, Integer offset, Integer length) {
        
        data.registerFix(desc, change, TextRange.from(offset, length));
    }
}
