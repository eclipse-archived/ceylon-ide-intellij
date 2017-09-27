import org.eclipse.ceylon.ide.common.correct {
    convertThenElseToIfElseQuickFix
}
import org.eclipse.ceylon.ide.common.util {
    nodes
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class ConvertThenElseToIfElseIntention() extends AbstractIntention() {
    
    familyName => "Convert 'if' or 'then'/'else' expression to statement";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        convertThenElseToIfElseQuickFix.addConvertToIfElseProposal(data, statement);
    }
}
