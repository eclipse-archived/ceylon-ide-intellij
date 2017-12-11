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
    refineEqualsHashQuickFix
}

import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}

shared class RefineEqualsHashIntention() extends AbstractIntention() {
    
    familyName => "Refine 'equals()' and/or 'hash'";
    
    checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => refineEqualsHashQuickFix.addRefineEqualsHashProposal(data, offset);
}
