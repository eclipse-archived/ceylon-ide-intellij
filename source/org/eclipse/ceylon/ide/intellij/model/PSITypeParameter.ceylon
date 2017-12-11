/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.psi {
    PsiTypeParameter,
    PsiNamedElement
}
import org.eclipse.ceylon.model.loader.mirror {
    TypeParameterMirror,
    TypeMirror
}

import java.util {
    ArrayList
}

// We don't use `SmartPsiElementPointer`s because `psi` is accessed eagerly
class PSITypeParameter(PsiTypeParameter psi)
        satisfies TypeParameterMirror {
    
    bounds = ArrayList<TypeMirror>();

    for (bound in psi.extendsList.referencedTypes) {
        bounds.add(PSIType(bound));
    }

    name = (psi of PsiNamedElement).name else "<unknown>";
    
    string => "PSITypeParameter[``name``]";    
}