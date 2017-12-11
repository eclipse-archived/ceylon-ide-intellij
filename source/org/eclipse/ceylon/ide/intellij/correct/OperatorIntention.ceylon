/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import org.eclipse.ceylon.compiler.typechecker.tree {
    Tree
}
import org.eclipse.ceylon.ide.common.correct {
    operatorQuickFix
}
import org.eclipse.ceylon.ide.common.util {
    nodes
}

import org.eclipse.ceylon.ide.intellij.psi {
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
