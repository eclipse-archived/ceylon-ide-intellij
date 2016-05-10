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
    ExpandTypeQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ExpandTypeIntention()
        extends GenericIntention()
        satisfies ExpandTypeQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> {
    
    familyName => "Expand type abbreviation";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        if (exists ed = data.editor) {
            value start = ed.selectionModel.selectionStart;
            value end = ed.selectionModel.selectionEnd;
            
            addExpandTypeProposal(data, file, statement, start, end);
        }
    }
}
