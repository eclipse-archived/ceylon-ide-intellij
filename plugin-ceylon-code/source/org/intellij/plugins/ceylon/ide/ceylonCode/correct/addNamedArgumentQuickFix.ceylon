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
    AddNamedArgumentQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

object ideaAddNamedArgumentQuickFix
        satisfies AddNamedArgumentQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        TextChange change, DefaultRegion region) {
        
        data.registerFix(desc, change, TextRange.from(region.start, region.length),
            ideaIcons.addCorrection);
    }
}
