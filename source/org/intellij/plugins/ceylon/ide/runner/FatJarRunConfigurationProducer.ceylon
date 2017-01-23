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

import java.io {
    File
}

import org.intellij.plugins.ceylon.ide.model {
    getCeylonProject
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    CeylonPsi
}

shared abstract class CeylonTaskRunConfigurationProducer()
        extends RunConfigurationProducer<JarApplicationConfiguration>(JarApplicationConfigurationType.instance) {

    shared formal Boolean isEnabledForModule(CeylonPsi.ModuleDescriptorPsi descriptor);
    shared formal String getGeneratedJarName(String modName, String version);
    shared formal String getRunConfigName(String modName, String version);
    shared formal CeylonBeforeRunTask getBeforeTask(String modName, String version);

    shared actual Boolean isConfigurationFromContext(JarApplicationConfiguration configuration,
        ConfigurationContext context) {

        if (exists element = context.psiLocation,
            exists file = element.containingFile,
            exists ceylonProject = getCeylonProject(file),
            exists mod = PsiTreeUtil.getParentOfType(element, `CeylonPsi.ModuleDescriptorPsi`),
            isEnabledForModule(mod)) {

            value modName = mod.ceylonNode.importPath.model.nameAsString;
            value modVersion = mod.ceylonNode.version.text.trim('"'.equals);
            value jarName = getGeneratedJarName(modName, modVersion);
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
            exists mod = PsiTreeUtil.getParentOfType(el, `CeylonPsi.ModuleDescriptorPsi`),
            isEnabledForModule(mod),
            is RunManagerEx runManager = context.runManager,
            exists modName = mod.ceylonNode.importPath?.model?.nameAsString,
            exists modVersion = mod.ceylonNode.version?.text?.trim('"'.equals)) {

            value dir = ceylonProject.rootDirectory.absolutePath;

            configuration.setName(getRunConfigName(modName, modVersion));
            configuration.jarPath = dir + File.separator + getGeneratedJarName(modName, modVersion);
            configuration.workingDirectory = dir;
            configuration.\imodule = context.\imodule;

            value beforeTask = getBeforeTask(modName, modVersion);
            beforeTask.enabled = true;

            value runTasks = runManager.getBeforeRunTasks(configuration);
            runTasks.add(beforeTask);
            runManager.setBeforeRunTasks(configuration, runTasks, false);

            return true;
        }

        return false;
    }
}


"Generates run configurations for fat-jar apps, with a 'ceylon fat-jar' before-run task."
shared class FatJarRunConfigurationProducer()
        extends CeylonTaskRunConfigurationProducer() {

    isEnabledForModule(CeylonPsi.ModuleDescriptorPsi descriptor) => true;

    getGeneratedJarName(String modName, String version) => "``modName``-``version``.jar";

    getBeforeTask(String modName, String version) => CeylonBeforeRunTask {
        command = "fat-jar";
        modName + "/" + version
    };

    getRunConfigName(String modName, String version) => modName + " with fat-jar";
}
