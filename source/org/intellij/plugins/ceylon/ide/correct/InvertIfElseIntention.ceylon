import com.redhat.ceylon.ide.common.correct {
    invertIfElseQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class InvertIfElseIntention() extends AbstractIntention() {
    
    familyName => "Invert 'if'/'else'";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        invertIfElseQuickFix.addInvertIfElseProposal(data, statement);
    }
}
