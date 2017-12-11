/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
package org.eclipse.ceylon.ide.intellij.lang;

import com.intellij.lang.Language;

/**
 * Created by gavin on 7/6/16.
 */
public class CeylonLanguage extends Language {

    public static final CeylonLanguage INSTANCE = new CeylonLanguage();

    private CeylonLanguage() {
        super("Ceylon", "text/ceylon");
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
