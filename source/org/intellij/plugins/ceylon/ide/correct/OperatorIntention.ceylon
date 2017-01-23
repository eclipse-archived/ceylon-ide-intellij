import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.correct {
    operatorQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class ReverseOperatorIntention() extends AbstractIntention() {
    
    familyName => "Reverse operator";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            operatorQuickFix.addReverseOperatorProposal(data, oe);
        }
    }
}

shared class InvertOperatorIntention() extends AbstractIntention() {
    
    familyName => "Invert operator";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            operatorQuickFix.addInvertOperatorProposal(data, oe);
        }
    }
}

shared class SwapBinaryOperandsIntention() extends AbstractIntention() {
    
    familyName => "Swap binary operands";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            operatorQuickFix.addSwapBinaryOperandsProposal(data, oe);
        }
    }
}

shared class ParenthesesIntention() extends AbstractIntention() {
    
    familyName => "Add/remove parentheses";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            operatorQuickFix.addParenthesesProposals(data, oe);
        }
    }
}
