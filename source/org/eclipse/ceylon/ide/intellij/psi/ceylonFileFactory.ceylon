/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.ide.fileTemplates {
    FileTemplateUtil {
        createFromTemplate
    },
    FileTemplateManager {
        fileTemplateManager=getInstance
    }
}
import com.intellij.psi {
    PsiDirectory,
    PsiElement
}
import org.eclipse.ceylon.common {
    Backend
}
import org.eclipse.ceylon.ide.common.util {
    escaping
}

import java.lang {
    Types {
        nativeString
    },
    JString=String
}
import java.util {
    HashMap,
    Properties,
    Arrays
}

shared object ceylonFileFactory {
    shared PsiElement createModuleDescriptor(PsiDirectory dir, String name, String version,
        Backend? backend = null, {String*} imports = []) {

        value tplManager = fileTemplateManager(dir.project);
        value props = HashMap<JString,Object>();

        if (exists backend) {
            props[nativeString("NATIVE")] = nativeString("native(\"``backend.nativeAnnotation``\")");
        }
        props[nativeString("MODULE_NAME")] = nativeString(name);
        props[nativeString("MODULE_VERSION")] = nativeString(version);

        if (! imports.empty) {
            props[nativeString("IMPORTS")] = Arrays.asList(*imports.map(nativeString));
        }

        return createFromTemplate(
            tplManager.getInternalTemplate("module.ceylon"),
            "module.ceylon", props, dir, null
        );
    }

    shared PsiElement createPackageDescriptor(PsiDirectory dir, String name, Boolean shared = true) {
        value tplManager = fileTemplateManager(dir.project);
        value props = HashMap<JString,Object>();

        props[nativeString("MODULE_NAME")] = nativeString(name);
        props[nativeString("IS_SHARED")] = nativeString(shared.string);

        return createFromTemplate(
            tplManager.getInternalTemplate("package.ceylon"),
            "package.ceylon", props, dir, null
        );
    }

    shared PsiElement createRun(PsiDirectory dir, String moduleName, String fileName, String unitName) {
        value tplManager = fileTemplateManager(dir.project);
        value props = HashMap<JString,Object>();

        props[nativeString("MODULE_NAME")] = nativeString(moduleName);
        props[nativeString("FUN_NAME")] = nativeString(unitName);

        return createFromTemplate(
            tplManager.getInternalTemplate("run.ceylon"),
            fileName, props, dir, null
        );
    }

    shared PsiElement createUnit(PsiDirectory dir, String unitName, String fileName, String templateName) {
        value tplManager = fileTemplateManager(dir.project);
        value props = HashMap<JString,Object>();

        value escaped = switch (templateName)
            case ("function"|"object")
            escaping.escapeInitialLowercase(unitName)
            case ("class"|"interface")
            escaping.escapeInitialUppercase(unitName)
            else unitName;
        props[nativeString("UNIT_NAME")] = nativeString(escaped);

        return createFromTemplate(
            tplManager.getInternalTemplate(templateName + ".ceylon"),
            fileName, props, dir, null
        );
    }

    shared PsiElement createFileFromTemplate(PsiDirectory dir, String templateName,
        String fileName = templateName, Properties? properties = null) {
        value tplManager = fileTemplateManager(dir.project);

        return createFromTemplate(
            tplManager.getInternalTemplate(templateName),
            fileName, properties, dir
        );
    }
}
