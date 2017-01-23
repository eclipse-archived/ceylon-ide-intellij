import com.redhat.ceylon.ide.common.correct {
    verboseRefinementQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class VerboseRefinementIntention() extends AbstractIntention() {
    
    familyName => "Convert refinement to verbose form";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        verboseRefinementQuickFix.addVerboseRefinementProposal(data, statement);
    }
}

shared class ShortcutRefinementIntention() extends AbstractIntention() {
    
    familyName => "Convert refinement to shortcut form";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findDeclaration(data.rootNode, data.node);
        verboseRefinementQuickFix.addShortcutRefinementProposal(data, statement);
    }
}
