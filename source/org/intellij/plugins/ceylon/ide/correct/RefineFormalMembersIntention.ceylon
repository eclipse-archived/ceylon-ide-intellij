import com.redhat.ceylon.ide.common.correct {
    refineFormalMembersQuickFix
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class RefineFormalMembersIntention() extends AbstractIntention() {
    
    familyName => "Refine formal members";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) 
            => refineFormalMembersQuickFix.addRefineFormalMembersProposal(data, false);
}
