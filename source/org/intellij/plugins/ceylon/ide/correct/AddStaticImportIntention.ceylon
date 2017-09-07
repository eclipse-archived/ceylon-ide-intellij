import com.redhat.ceylon.ide.common.correct {
    addStaticImportQuickFix
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class AddStaticImportIntention() extends AbstractIntention() {

    familyName => "Add static import";

    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => addStaticImportQuickFix.addProposal(data);

}