import com.redhat.ceylon.ide.common.correct {
    joinIfStatementsQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class JoinIfStatementsIntention() extends AbstractIntention() {
    
    familyName => "Join 'if' statements";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        joinIfStatementsQuickFix.addJoinIfStatementsProposal(data, statement);
    }
}
