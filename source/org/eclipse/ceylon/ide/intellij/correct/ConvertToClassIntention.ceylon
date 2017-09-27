import org.eclipse.ceylon.ide.common.correct {
    convertToClassQuickFix
}
import org.eclipse.ceylon.ide.common.util {
    nodes
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class ConvertToClassIntention() extends AbstractIntention() {
    
    familyName => "Convert object to class";
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);

        convertToClassQuickFix.addConvertToClassProposal(data, decl);
    }
}
