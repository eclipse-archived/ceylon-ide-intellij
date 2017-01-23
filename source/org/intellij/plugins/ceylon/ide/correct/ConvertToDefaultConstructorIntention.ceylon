import com.redhat.ceylon.ide.common.correct {
    convertToDefaultConstructorQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class ConvertToDefaultConstructorIntention() extends AbstractIntention() {
    
    familyName => "Convert to class with default constructor";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        convertToDefaultConstructorQuickFix.addConvertToDefaultConstructorProposal(data, statement);
    }
}
