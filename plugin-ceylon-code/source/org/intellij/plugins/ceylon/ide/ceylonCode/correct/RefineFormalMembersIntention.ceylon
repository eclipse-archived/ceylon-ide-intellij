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
    RefineFormalMembersQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class RefineFormalMembersIntention()
        extends GenericIntention()
        satisfies RefineFormalMembersQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> {    
    
    familyName => "Refine formal members";
    
    shared actual Anything checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        return addRefineFormalMembersProposal(data, false);
    }
    
    getDocChar(Document doc, Integer offset)
            => doc.charsSequence.charAt(offset);
    
    shared actual void newRefineFormalMembersProposal(IdeaQuickFixData data, String desc) {
        if (exists editor = data.editor,
            exists change = refineFormalMembers(data, data.doc, editor.caretModel.offset)) {
            makeAvailable(desc, change, null);
        }
    }
}
