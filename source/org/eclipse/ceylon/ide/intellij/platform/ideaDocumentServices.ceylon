/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import org.eclipse.ceylon.ide.common.platform {
    DocumentServices,
    CommonDocument
}
import com.intellij.psi.codeStyle {
    CodeStyleSettings
}
import org.eclipse.ceylon.compiler.typechecker.context {
    PhasedUnit
}

object ideaDocumentServices satisfies DocumentServices {
    createTextChange(String desc, CommonDocument|PhasedUnit input)
            => IdeaTextChange(input);
    
    createCompositeChange(String desc) => IdeaCompositeChange();
    
    // TODO take the settings from the current project
    indentSpaces => CodeStyleSettings().indentOptions?.indentSize else 4;
    
    indentWithSpaces => true;
}