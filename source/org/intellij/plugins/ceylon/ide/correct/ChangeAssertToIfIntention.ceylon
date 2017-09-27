import org.eclipse.ceylon.ide.common.correct {
    changeToIfQuickFix
}
import org.eclipse.ceylon.ide.common.util {
    nodes
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class ChangeAssertToIfIntention() extends AbstractIntention() {
    
    familyName => "Change 'assert' to 'if'";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        changeToIfQuickFix.addChangeToIfProposal(data, statement);
    }
}
