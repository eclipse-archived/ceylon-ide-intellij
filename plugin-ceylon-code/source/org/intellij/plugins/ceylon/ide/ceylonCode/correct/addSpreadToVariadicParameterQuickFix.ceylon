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
    AddSpreadToVariadicParameterQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    TypedDeclaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

object ideaAddSpreadToVariadicParameterQuickFix
        satisfies AddSpreadToVariadicParameterQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        TypedDeclaration parameter, Integer offset, TextChange change) {
        
        data.registerFix(desc, change, TextRange.from(offset, 0));
    }
    
    
}