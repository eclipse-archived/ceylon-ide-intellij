import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.correct {
    OperatorQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ReverseOperatorIntention()
        extends GenericIntention()
        satisfies OperatorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> {
    
    familyName => "Reverse operator";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            addReverseOperatorProposal(data, file, oe);
        }
    }
}

shared class InvertOperatorIntention()
        extends GenericIntention()
        satisfies OperatorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> {
    
    familyName => "Invert operator";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            addInvertOperatorProposal(data, file, oe);
        }
    }
}

shared class SwapBinaryOperandsIntention()
        extends GenericIntention()
        satisfies OperatorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> {
    
    familyName => "Swap binary operands";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            addSwapBinaryOperandsProposal(data, file, oe);
        }
    }
}

shared class ParenthesesIntention()
        extends GenericIntention()
        satisfies OperatorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> {
    
    familyName => "Add/remove parentheses";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            addParenthesesProposals(data, file, oe);
        }
    }
}
