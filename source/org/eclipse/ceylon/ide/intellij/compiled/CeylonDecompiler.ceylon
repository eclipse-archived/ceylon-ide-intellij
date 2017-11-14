/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.psi.compiled {
    ClassFileDecompilers
}

import org.eclipse.ceylon.ide.intellij.compiled {
    classFileDecompilerUtil
}

"This decompiler tries to determine if a class was compiled by the Ceylon compiler.
 It will also ignore “internal“ classes ($impl, anonymous classes etc)."
shared class CeylonDecompiler() extends ClassFileDecompilers.Full() {
    stubBuilder = CeylonClsStubBuilder();
    createFileViewProvider = CeylonClassFileFileViewProvider;
    accepts = classFileDecompilerUtil.isCeylonCompiledFile;
}
