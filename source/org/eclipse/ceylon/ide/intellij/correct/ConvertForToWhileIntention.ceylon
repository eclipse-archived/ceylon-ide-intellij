import org.eclipse.ceylon.ide.common.correct {
    convertForToWhileQuickFix
}
import org.eclipse.ceylon.ide.common.util {
    nodes
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class ConvertForToWhileIntention() extends AbstractIntention() {

    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        convertForToWhileQuickFix.addConvertForToWhileProposal(data, statement);
    }

    familyName => "Convert 'for' to 'while'";
}