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
    FixAliasQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

object ideaFixAliasQuickFix
        satisfies FixAliasQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & AbstractIntention {
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        Integer offset, TextChange change) {
        
        data.registerFix(desc, change, TextRange.from(offset, 0));
    }
}
