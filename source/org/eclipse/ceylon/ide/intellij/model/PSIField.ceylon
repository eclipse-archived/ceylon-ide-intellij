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
    PsiField,
    PsiModifier,
    SmartPsiElementPointer
}
import org.eclipse.ceylon.model.loader.mirror {
    FieldMirror
}
import org.eclipse.ceylon.ide.intellij.model {
    concurrencyManager {
        needReadAccess
    }
}

class PSIField(SmartPsiElementPointer<PsiField> psiPointer)
        extends PSIAnnotatedMirror(psiPointer)
        satisfies FieldMirror {

    shared PsiField psi => get(psiPointer);
    
    shared actual late PSIType type
            = needReadAccess(() => PSIType(psi.type));

    final => psi.hasModifierProperty(PsiModifier.final);

    static => psi.hasModifierProperty(PsiModifier.static);
    
}
