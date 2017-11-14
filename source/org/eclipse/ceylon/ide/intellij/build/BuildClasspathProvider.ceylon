/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.compiler.server {
    BuildProcessParametersProvider
}
import com.intellij.openapi.util.io {
    FileUtil
}

import java.lang {
    Str=String,
    Types {
        str=nativeString
    }
}
import java.util {
    ArrayList,
    List
}

import org.eclipse.ceylon.ide.intellij.startup {
    CeylonIdePlugin
}

shared class BuildClasspathProvider() extends BuildProcessParametersProvider() {

    function compute() {
        value repo = CeylonIdePlugin.embeddedCeylonRepository;
        value modulePaths = ArrayList<Str>();
        modulePaths.add(str(CeylonIdePlugin.classesDir.absolutePath));
        FileUtil.visitFiles(repo, (file) {
            if (file.file && file.name.endsWith(".jar")) {
                modulePaths.add(str(file.absolutePath));
            }
            return true;
        });
        return modulePaths;
    }

    shared actual late List<Str> classPath = compute();

}
