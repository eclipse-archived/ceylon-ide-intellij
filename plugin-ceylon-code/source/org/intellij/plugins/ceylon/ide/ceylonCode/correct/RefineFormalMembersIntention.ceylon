import com.redhat.ceylon.ide.common.correct {
    refineFormalMembersQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class RefineFormalMembersIntention() extends AbstractIntention() {
    
    familyName => "Refine formal members";
    
    shared actual Anything checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        return refineFormalMembersQuickFix.addRefineFormalMembersProposal(data, false);
    }
}
