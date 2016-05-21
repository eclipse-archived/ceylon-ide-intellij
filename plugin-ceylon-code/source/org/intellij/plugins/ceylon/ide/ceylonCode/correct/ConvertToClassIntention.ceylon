import com.intellij.openapi.project {
    Project
}
import com.redhat.ceylon.ide.common.correct {
    convertToClassQuickFix,
    AbstractConvertToClassProposal
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ConvertToClassIntention() extends AbstractIntention() {
    
    familyName => "Convert object to class";
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);

        convertToClassQuickFix.addConvertToClassProposal(data, decl);
    }
}

class ConvertToClassProposal(Project project)
        satisfies AbstractConvertToClassProposal {

}
