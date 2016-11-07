import ceylon.interop.java {
    javaClass
}

import com.intellij.execution {
    RunManagerEx
}
import com.intellij.execution.actions {
    RunConfigurationProducer,
    ConfigurationContext
}
import com.intellij.execution.jar {
    JarApplicationConfiguration,
    JarApplicationConfigurationType
}
import com.intellij.openapi.util {
    Ref
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}

import java.util {
    List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    getCeylonProject
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsi
}
import java.io {
    File
}

Boolean importsJavaEE(List<Tree.ImportModule> importModules) {
    for (imp in importModules) {
        if (exists quoted = imp.quotedLiteral,
            quoted.text == "\"javax:javaee-api\"") {
            return true;
        }

        if (exists path = imp.importPath) {
            value importName = ".".join {
                for (id in path.identifiers)
                id.text
            };
            if (importName == "javax.javaeeapi") {
                return true;
            }
        }
    }
    return false;
}

"Generates run configurations for Swarm apps, with a 'ceylon swarm' before-run task."
shared class SwarmRunConfigurationProducer()
        extends RunConfigurationProducer<JarApplicationConfiguration>(JarApplicationConfigurationType.instance) {

    shared actual Boolean isConfigurationFromContext(JarApplicationConfiguration configuration,
        ConfigurationContext context) {

        if (exists element = context.psiLocation,
            exists file = element.containingFile,
            exists ceylonProject = getCeylonProject(file),
            exists mod = PsiTreeUtil.getParentOfType(element, javaClass<CeylonPsi.ModuleDescriptorPsi>()),
            importsJavaEE(mod.ceylonNode.importModuleList.importModules)) {

            value modName = mod.ceylonNode.importPath.model.get(0).nameAsString;
            value modVersion = mod.ceylonNode.version.text.trim('"'.equals);
            value jarName = modName + "-" + modVersion + "-swarm.jar";
            value dir = ceylonProject.rootDirectory.absolutePath;

            return configuration.\imodule == context.\imodule
                && configuration.jarPath == dir + File.separator + jarName;
        }
        return false;
    }

    shared actual Boolean setupConfigurationFromContext(JarApplicationConfiguration configuration,
        ConfigurationContext context, Ref<PsiElement> sourceElement) {

        value el = sourceElement.get();

        if (is CeylonFile file = el.containingFile,
            exists ceylonProject = getCeylonProject(file),
            exists mod = PsiTreeUtil.getParentOfType(el, javaClass<CeylonPsi.ModuleDescriptorPsi>()),
            importsJavaEE(mod.ceylonNode.importModuleList.importModules),
            is RunManagerEx runManager = context.runManager,
            exists modName = mod.ceylonNode.importPath?.model?.get(0)?.nameAsString,
            exists modVersion = mod.ceylonNode.version?.text?.trim('"'.equals)) {

            value dir = ceylonProject.rootDirectory.absolutePath;

            configuration.setName(modName + " in Swarm");
            configuration.jarPath = dir + File.separator + modName + "-" + modVersion + "-swarm.jar";
            configuration.workingDirectory = dir;
            configuration.\imodule = context.\imodule;

            value swarmTask = CeylonBeforeRunTask {
                command = "swarm";
                "--provided-module", "javax:javaee-api",
                modName + "/" + modVersion
            };
            swarmTask.enabled = true;

            value runTasks = runManager.getBeforeRunTasks(configuration);
            runTasks.add(swarmTask);
            runManager.setBeforeRunTasks(configuration, runTasks, false);

            return true;
        }

        return false;
    }
}
