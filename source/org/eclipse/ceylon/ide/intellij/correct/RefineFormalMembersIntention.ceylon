import org.eclipse.ceylon.ide.common.correct {
    refineFormalMembersQuickFix
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class RefineFormalMembersIntention() extends AbstractIntention() {
    
    familyName => "Refine formal members";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) 
            => refineFormalMembersQuickFix.addRefineFormalMembersProposal(data, false);
}
