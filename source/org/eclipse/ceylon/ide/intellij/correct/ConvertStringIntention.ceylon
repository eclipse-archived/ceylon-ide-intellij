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
    convertStringQuickFix
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class ConvertStringToVerbatimIntention() extends AbstractIntention() {
    
    familyName => "Convert string to verbatim";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => convertStringQuickFix.addConvertToVerbatimProposal(data);
}

shared class ConvertVerbatimToStringIntention() extends AbstractIntention() {
    
    familyName => "Convert verbatim to string";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => convertStringQuickFix.addConvertFromVerbatimProposal(data);
}

shared class ConvertToInterpolationIntention() extends AbstractIntention() {
    
    familyName => "Convert concatenation to interpolation";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => convertStringQuickFix.addConvertToInterpolationProposal(data);
}

shared class ConvertToConcatenationIntention() extends AbstractIntention() {
    
    familyName => "Convert interpolation to concatenation";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => convertStringQuickFix.addConvertToConcatenationProposal(data);
}
