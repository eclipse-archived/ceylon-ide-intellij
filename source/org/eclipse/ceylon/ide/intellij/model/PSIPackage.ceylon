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
    PsiClass,
    PsiJavaFile,
    SmartPsiElementPointer
}
import org.eclipse.ceylon.ide.common.platform {
    platformUtils,
    Status
}
import org.eclipse.ceylon.model.loader.mirror {
    PackageMirror
}

import org.eclipse.ceylon.ide.intellij.model {
    concurrencyManager {
        needReadAccess
    }
}

class PSIPackage(SmartPsiElementPointer<PsiClass> psiPointer)
    satisfies PackageMirror {

    "This is needed when a PsiClass is removed from the index,
     and the model loader tries to unload the corresponding
     mirror. When that happens, we still need to access the
     qualified name although the PSI has been invalidated."
    variable String cachedQualifiedName = "";

    shared actual String qualifiedName { 
        try {
            cachedQualifiedName = needReadAccess(() {
                value file = get(psiPointer).containingFile;
                if (is PsiJavaFile file) {
                    return file.packageName;
                }
                platformUtils.log(Status._WARNING,
                    "No qualified name for file ``file else "<null>"``");
                return "";
            });
        } catch (PsiElementGoneException e) {}

        return cachedQualifiedName;
    }

    noop(qualifiedName); //initialize it on construction

}