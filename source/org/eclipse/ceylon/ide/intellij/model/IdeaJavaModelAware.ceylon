/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.psi {
    PsiClass,
    PsiMethod
}
import org.eclipse.ceylon.ide.common.model {
    IJavaModelAware
}
import org.eclipse.ceylon.ide.common.util {
    BaseProgressMonitor
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.ceylon.ide.intellij.resolve {
    declarationToPsi
}

shared interface IdeaJavaModelAware
    satisfies IJavaModelAware<Module, PsiClass, PsiClass|PsiMethod> {

        javaClassRootToNativeProject(PsiClass javaClassRoot)
            => nothing; // TODO : this has to be implemented

        toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor)
            => if (is PsiClass|PsiMethod psi = declarationToPsi(ceylonDeclaration))
                then psi
                else null;
}
