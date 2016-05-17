import com.redhat.ceylon.ide.common.correct {
    addAnnotationQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class AddAnnotationIntention() extends AbstractIntention() {

    familyName => "Add annotation";

    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);

        // TODO this method can potentially generate more than one proposal, which
        // means we should have more than one intention :/
        addAnnotationQuickFix.addContextualAnnotationProposals(data, decl, offset);
    }
}
