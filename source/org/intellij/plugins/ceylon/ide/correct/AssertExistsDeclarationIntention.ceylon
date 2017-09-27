import org.eclipse.ceylon.ide.common.correct {
    assertExistsDeclarationQuickFix
}
import org.eclipse.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class AssertExistsDeclarationIntention() extends AbstractIntention() {
    
    familyName => "Assert value exists or is non-empty";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);

        assertExistsDeclarationQuickFix.addAssertExistsDeclarationProposals(data, decl);
    }
}
