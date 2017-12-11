/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
package org.eclipse.ceylon.ide.intellij.psi;

import com.intellij.psi.tree.IElementType;
import org.eclipse.ceylon.ide.intellij.lang.CeylonLanguage;
import org.jetbrains.annotations.NonNls;

public class CeylonElementType extends IElementType {

    public CeylonElementType(@NonNls String debugName) {
        super(debugName, CeylonLanguage.INSTANCE);
    }
}