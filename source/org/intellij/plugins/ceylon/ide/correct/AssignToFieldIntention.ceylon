import com.redhat.ceylon.ide.common.correct {
    assignToFieldQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class AssignToFieldIntention() extends AbstractIntention() {
    
    familyName => "Assign to field";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        value decl = nodes.findDeclaration(data.rootNode, data.node);

        assignToFieldQuickFix.addAssignToFieldProposal(data, statement, decl);
    }
}