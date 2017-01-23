import com.redhat.ceylon.ide.common.correct {
    addThrowsAnnotationQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class AddThrowsAnnotationIntention() extends AbstractIntention() {
    
    familyName => "Add throws annotation";

    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        addThrowsAnnotationQuickFix.addThrowsAnnotationProposal(data, statement);
    }
}
