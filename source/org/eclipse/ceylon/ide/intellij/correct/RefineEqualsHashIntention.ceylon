import org.eclipse.ceylon.ide.common.correct {
    refineEqualsHashQuickFix
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class RefineEqualsHashIntention() extends AbstractIntention() {
    
    familyName => "Refine 'equals()' and/or 'hash'";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => refineEqualsHashQuickFix.addRefineEqualsHashProposal(data, offset);
}
