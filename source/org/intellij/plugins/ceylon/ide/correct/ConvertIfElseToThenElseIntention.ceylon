import com.redhat.ceylon.ide.common.correct {
    convertIfElseToThenElseQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class ConvertIfElseToThenElseIntention() extends AbstractIntention() {
    
    familyName => "Convert 'if' statement to expression";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        convertIfElseToThenElseQuickFix.addConvertToThenElseProposal(data, statement);
    }
}
