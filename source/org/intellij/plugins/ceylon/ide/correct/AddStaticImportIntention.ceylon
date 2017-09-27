import org.eclipse.ceylon.ide.common.correct {
    addStaticImportQuickFix
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class AddStaticImportIntention() extends AbstractIntention() {

    familyName => "Add static import";

    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => addStaticImportQuickFix.addProposal(data);

}