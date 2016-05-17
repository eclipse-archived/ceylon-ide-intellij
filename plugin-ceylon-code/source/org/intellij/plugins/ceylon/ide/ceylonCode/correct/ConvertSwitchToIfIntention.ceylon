import com.redhat.ceylon.ide.common.correct {
    convertSwitchToIfQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ConvertSwitchToIfIntention() extends AbstractIntention() {
    
    familyName => "Convert switch to if";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        convertSwitchToIfQuickFix.addConvertSwitchToIfProposal(data, statement);
    }
}

shared class ConvertIfToSwitchIntention() extends AbstractIntention() {
    
    familyName => "Convert if to switch";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        convertSwitchToIfQuickFix.addConvertIfToSwitchProposal(data, statement);
    }
}
