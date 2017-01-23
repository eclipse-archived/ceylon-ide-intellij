import com.redhat.ceylon.ide.common.correct {
    expandTypeQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class ExpandTypeIntention() extends AbstractIntention() {
    
    familyName => "Expand type abbreviation";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        if (exists ed = data.editor) {
            value start = ed.selectionModel.selectionStart;
            value end = ed.selectionModel.selectionEnd;

            expandTypeQuickFix.addExpandTypeProposal(data, statement, start, end);
        }
    }
}
