import com.redhat.ceylon.ide.common.correct {
    convertThenElseToIfElse
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ConvertThenElseToIfElseIntention() extends AbstractIntention() {
    
    familyName => "Convert if/then/else to statement";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        convertThenElseToIfElse.addConvertToIfElseProposal(data, data.document, statement);
    }
}
