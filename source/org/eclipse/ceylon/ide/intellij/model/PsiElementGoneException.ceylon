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
    SmartPsiElementPointer
}

shared class PsiElementGoneException()
        extends Exception("the PSI element no longer exists") {}

throws (class PsiElementGoneException)
E get<E>(SmartPsiElementPointer<out E> pointer) {
    if (exists e = pointer.element) {
        return e;
    }
    else {
        throw PsiElementGoneException();
    }
}
