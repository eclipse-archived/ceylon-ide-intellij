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
    PsiElement
}
import com.intellij.psi.impl.compiled {
    ClsCustomNavigationPolicyEx,
    ClsClassImpl,
    ClsMethodImpl,
    ClsFieldImpl
}

import org.eclipse.ceylon.ide.intellij.resolve {
    ceylonSourceNavigator
}
import java.lang {
    overloaded
}

shared class CeylonClsNavigationPolicy() extends ClsCustomNavigationPolicyEx() {

    function getElement(PsiElement clsClass)
            => ceylonSourceNavigator.getOriginalElements(clsClass)[0];

    overloaded
    shared actual PsiElement? getNavigationElement(ClsMethodImpl clsMethod)
            => getElement(clsMethod);

    overloaded
    shared actual PsiElement? getNavigationElement(ClsFieldImpl clsField)
            => getElement(clsField);

    overloaded
    shared actual PsiElement? getNavigationElement(ClsClassImpl clsClass)
            => getElement(clsClass);

}
