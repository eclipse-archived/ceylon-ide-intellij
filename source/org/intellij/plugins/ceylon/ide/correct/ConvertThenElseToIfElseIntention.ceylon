import com.redhat.ceylon.ide.common.correct {
    convertThenElseToIfElseQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class ConvertThenElseToIfElseIntention() extends AbstractIntention() {
    
    familyName => "Convert 'if' or 'then'/'else' expression to statement";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        convertThenElseToIfElseQuickFix.addConvertToIfElseProposal(data, statement);
    }
}
