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
import com.redhat.ceylon.model.typechecker.model {
    Module
}

import java.io {
    File
}
import java.util {
    ArrayList
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

    shared default Boolean isEnabledForModule(CeylonPsi.ModuleDescriptorPsi descriptor) => true;
    shared formal String getGeneratedJarName(String modName, String version);
    shared formal String getRunConfigName(String modName, String version);
    shared formal CeylonBeforeRunTask getBeforeTask(String modName, String version);

    shared actual Boolean isConfigurationFromContext(JarApplicationConfiguration configuration,
            ConfigurationContext context) {

        if (exists element = context.psiLocation,
            exists configModule = configuration.\imodule,
            exists contextModule = context.\imodule,
            exists file = element.containingFile,
            exists ceylonProject = getCeylonProject(file),
            exists mod = PsiTreeUtil.getParentOfType(element, `CeylonPsi.ModuleDescriptorPsi`),
            isEnabledForModule(mod),
            exists model = mod.ceylonNode?.importPath?.model) {
            assert (is Module model);

            value modName = model.nameAsString;
            value modVersion = model.version;
            value jarName = getGeneratedJarName(modName, modVersion);
            value dir = ceylonProject.rootDirectory.absolutePath;

            return configModule == contextModule
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
            exists model = mod.ceylonNode?.importPath?.model) {
            assert (is Module model);

            value dir = ceylonProject.rootDirectory.absolutePath;

            value modName = model.nameAsString;
            value modVersion = model.version;

            configuration.setName(getRunConfigName(modName, modVersion));
            configuration.jarPath = dir + File.separator + getGeneratedJarName(modName, modVersion);
            configuration.workingDirectory = dir;
            configuration.\imodule = context.\imodule;

            value beforeTask = getBeforeTask(modName, modVersion);
            beforeTask.enabled = true;

            value beforeTasks = ArrayList(runManager.getBeforeRunTasks(configuration));
            beforeTasks.add(beforeTask);
            runManager.setBeforeRunTasks(configuration, beforeTasks, false);

            return true;
        }

        return false;
    }
}
