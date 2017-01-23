import com.redhat.ceylon.ide.common.correct {
    splitDeclarationQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class SplitDeclarationIntention() extends AbstractIntention() {

    familyName => "Split declaration";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        value decl = nodes.findDeclaration(data.rootNode, data.node);

        splitDeclarationQuickFix.addSplitDeclarationProposals(data, decl, statement);
    }
}
