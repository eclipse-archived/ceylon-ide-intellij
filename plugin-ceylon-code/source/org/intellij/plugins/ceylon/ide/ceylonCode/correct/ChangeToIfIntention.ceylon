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
    ChangeToIfQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ChangeToIfIntention()
        extends GenericIntention()
        satisfies ChangeToIfQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> {
    
    familyName => "Change assert to if";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        addChangeToIfProposal(data, file, statement);
    }
}
