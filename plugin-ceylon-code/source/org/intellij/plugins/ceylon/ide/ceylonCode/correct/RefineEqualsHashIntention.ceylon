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
    RefineEqualsHashQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class RefineEqualsHashIntention() 
        extends GenericIntention()
        satisfies RefineEqualsHashQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> {    
    
    familyName => "Refine equals() and/or hash";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => addRefineEqualsHashProposal(data, file, offset);
}
