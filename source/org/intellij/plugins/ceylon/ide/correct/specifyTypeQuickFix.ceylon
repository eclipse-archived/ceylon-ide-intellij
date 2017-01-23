import com.redhat.ceylon.ide.common.correct {
    specifyTypeQuickFix,
    specifyTypeArgumentsQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class IdeaSpecifyTypeIntention() extends AbstractIntention() {

    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value declaration = nodes.findDeclaration(data.rootNode, data.node);
        specifyTypeQuickFix.addTypingProposals(data, declaration);
    }
    
    familyName => "Specify type";
}

shared class IdeaSpecifyTypeArgumentsIntention() extends AbstractIntention() {

    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => specifyTypeArgumentsQuickFix.addTypingProposals(data);

    familyName => "Specify type arguments";
}
