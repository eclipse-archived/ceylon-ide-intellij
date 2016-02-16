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
    ChangeTypeQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

object ideaChangeTypeQuickFix
        satisfies ChangeTypeQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & AbstractIntention {
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        TextChange change, Integer offset, Integer length, Unit unit) {
        
        data.registerFix(desc, change, TextRange.from(offset, length));
    }
}
