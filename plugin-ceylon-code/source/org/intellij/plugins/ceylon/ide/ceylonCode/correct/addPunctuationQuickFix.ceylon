import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.correct {
    AddPunctuationQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

object ideaAddPunctuationQuickFix 
        satisfies AddPunctuationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
        & IdeaDocumentChanges
        & AbstractIntention {
    
    shared actual void newProposal(IdeaQuickFixData data, Integer offset, 
        Integer length, String desc, TextChange change) {
        
        data.registerFix(desc, change, TextRange.from(offset, length));
    }
}