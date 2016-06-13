import ceylon.interop.java {
    javaString
}

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
import com.redhat.ceylon.common {
    Backend
}
import com.redhat.ceylon.ide.common.util {
    toJavaStringList
}

import java.lang {
    JString=String
}
import java.util {
    HashMap
}

shared object ceylonFileFactory {
    shared PsiElement createModuleDescriptor(PsiDirectory dir, String name, String version,
        Backend? backend = null, {String*} imports = empty) {

        value tplManager = fileTemplateManager(dir.project);
        value props = HashMap<JString,Object>();

        if (exists backend) {
            props.put(javaString("NATIVE"), javaString("native(\"" + backend.nativeAnnotation + "\")"));
        }
        props.put(javaString("MODULE_NAME"), javaString(name));
        props.put(javaString("MODULE_VERSION"), javaString(version));

        if (! imports.empty) {
            props.put(javaString("IMPORTS"), toJavaStringList(imports));
        }

        return createFromTemplate(
            tplManager.getInternalTemplate("module.ceylon"),
            "module.ceylon", props, dir, null
        );
    }

    shared PsiElement createPackageDescriptor(PsiDirectory dir, String name, Boolean shared = true) {
        value tplManager = fileTemplateManager(dir.project);
        value props = HashMap<JString,Object>();

        props.put(javaString("MODULE_NAME"), javaString(name));
        props.put(javaString("IS_SHARED"), javaString(shared.string));

        return createFromTemplate(
            tplManager.getInternalTemplate("package.ceylon"),
            "package.ceylon", props, dir, null
        );
    }

    shared PsiElement createRun(PsiDirectory dir, String moduleName, String fileName, String unitName) {
        value tplManager = fileTemplateManager(dir.project);
        value props = HashMap<JString,Object>();

        props.put(javaString("MODULE_NAME"), javaString(moduleName));
        props.put(javaString("FUN_NAME"), javaString(unitName));

        return createFromTemplate(
            tplManager.getInternalTemplate("run.ceylon"),
            fileName, props, dir, null
        );
    }

    shared PsiElement createUnit(PsiDirectory dir, String unitName, String fileName, String templateName) {
        value tplManager = fileTemplateManager(dir.project);
        value props = HashMap<JString,Object>();

        props.put(javaString("UNIT_NAME"), javaString(unitName));

        return createFromTemplate(
            tplManager.getInternalTemplate(templateName + ".ceylon"),
            fileName, props, dir, null
        );
    }

    shared PsiElement createFileFromTemplate(PsiDirectory dir, String templateName, String fileName = templateName) {
        value tplManager = fileTemplateManager(dir.project);

        return createFromTemplate(
            tplManager.getInternalTemplate(templateName),
            fileName, null, dir
        );
    }
}
