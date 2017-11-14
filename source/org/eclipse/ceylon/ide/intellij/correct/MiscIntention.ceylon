/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import org.eclipse.ceylon.ide.common.correct {
    miscQuickFixes
}
import org.eclipse.ceylon.ide.common.util {
    nodes
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class AnonymousFunctionIntention() extends AbstractIntention() {
    familyName => "Convert anonymous function block/specifier";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => miscQuickFixes.addAnonymousFunctionProposals(data);
}

shared class DeclarationIntention() extends AbstractIntention() {
    familyName => "Convert declaration block/specifier";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);
        miscQuickFixes.addDeclarationProposals(data, decl, offset);
    }
}

shared class ConvertArgumentBlockIntention() extends AbstractIntention() {
    familyName => "Convert argument block/specifier";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value arg = nodes.findArgument(data.rootNode, data.node);
        miscQuickFixes.addArgumentBlockProposals(data, arg);
    }
}

shared class FillInArgumentNameIntention() extends AbstractIntention() {
    familyName => "Fill in argument name";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value arg = nodes.findArgument(data.rootNode, data.node);
        miscQuickFixes.addArgumentFillInProposals(data, arg);
    }
}