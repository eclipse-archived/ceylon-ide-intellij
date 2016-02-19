import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.\imodule {
    Module
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
        extends AbstractIntention()
        satisfies OperatorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Reverse operator";
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, TextChange change)
            => makeAvailable(desc, change);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            addReverseOperatorProposal(data, file, oe);
        }
    }
}

shared class InvertOperatorIntention()
        extends AbstractIntention()
        satisfies OperatorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Invert operator";

    shared actual void newProposal(IdeaQuickFixData data, String desc, TextChange change)
            => makeAvailable(desc, change);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            addInvertOperatorProposal(data, file, oe);
        }
    }
}

shared class SwapBinaryOperandsIntention()
        extends AbstractIntention()
        satisfies OperatorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Swap binary operands";

    shared actual void newProposal(IdeaQuickFixData data, String desc, TextChange change)
            => makeAvailable(desc, change);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            addSwapBinaryOperandsProposal(data, file, oe);
        }
    }
}

shared class ParenthesesIntention()
        extends AbstractIntention()
        satisfies OperatorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Add/remove parentheses";

    shared actual void newProposal(IdeaQuickFixData data, String desc, TextChange change)
            => makeAvailable(desc, change);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        if (is Tree.BinaryOperatorExpression oe = nodes.findOperator(data.rootNode, data.node)) {
            addParenthesesProposals(data, file, oe);
        }
    }
}
