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
    AddParameterListQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

object ideaAddParameterListQuickFix
        satisfies AddParameterListQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement>
        & IdeaDocumentChanges
        & IdeaQuickFix {
    
    shared actual void newProposal(IdeaQuickFixData data, Integer start, String desc, TextChange change) {
        data.registerFix(desc, change, TextRange.from(start, 0));
    }
}
