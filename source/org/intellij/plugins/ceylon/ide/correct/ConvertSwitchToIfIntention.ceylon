import com.redhat.ceylon.ide.common.correct {
    convertSwitchToIfQuickFix,
    convertSwitchStatementToExpressionQuickFix,
    convertSwitchExpressionToStatementQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class ConvertSwitchToIfIntention() extends AbstractIntention() {
    
    familyName => "Convert 'switch' to 'if'";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        convertSwitchToIfQuickFix.addConvertSwitchToIfProposal(data, statement);
    }
}

shared class ConvertIfToSwitchIntention() extends AbstractIntention() {
    
    familyName => "Convert 'if' to 'switch'";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        convertSwitchToIfQuickFix.addConvertIfToSwitchProposal(data, statement);
    }
}

shared class ConvertSwitchStatementToExpressionIntention() extends AbstractIntention() {

    familyName => "Convert 'switch' statement to expression";

    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        convertSwitchStatementToExpressionQuickFix.addConvertSwitchStatementToExpressionProposal(data, statement);
    }
}

shared class ConvertSwitchExpressionToStatementIntention() extends AbstractIntention() {

    familyName => "Convert 'switch' expression to statement";

    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);

        convertSwitchExpressionToStatementQuickFix.addConvertSwitchExpressionToStatementProposal(data, statement);
    }
}
