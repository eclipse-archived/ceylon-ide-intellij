import com.intellij.ide.util.projectWizard {
    ...
}
import com.intellij.openapi.\imodule {
    ModuleType
}
import com.intellij.openapi.roots.ui.configuration {
    ModulesProvider
}
import com.intellij.util {
    ArrayUtil
}

import java.lang {
    ObjectArray
}
import java.util {
    ArrayList
}

import org.intellij.plugins.ceylon.ide {
    CeylonBundle
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

"IntelliJ module containing Ceylon sources files."
shared class CeylonModuleType extends ModuleType<CeylonModuleBuilder> {

    static value id = "CEYLON_MODULE";
    static shared CeylonModuleType instance = CeylonModuleType();

    shared new () extends ModuleType<CeylonModuleBuilder>(id) {}

    createModuleBuilder() => CeylonModuleBuilder();

    name => "Ceylon";

    description => CeylonBundle.message("project.wizard.module.description");

    getNodeIcon(Boolean isOpened) => icons.project;

    shared actual ModuleWizardStep modifyProjectTypeStep(SettingsStep settingsStep, ModuleBuilder moduleBuilder)
            => ProjectWizardStepFactory.instance
                .createJavaSettingsStep(settingsStep, moduleBuilder, moduleBuilder.isSuitableSdkType);

    shared actual ObjectArray<ModuleWizardStep> createWizardSteps(WizardContext wizardContext,
            CeylonModuleBuilder moduleBuilder, ModulesProvider modulesProvider) {

        value steps = ArrayList<ModuleWizardStep>();
        steps.add(PageOneWizardStep(moduleBuilder));
        steps.add(PageTwoWizardStep(moduleBuilder));
        value wizardSteps = steps.toArray(ObjectArray<ModuleWizardStep>(steps.size()));
        return ArrayUtil.mergeArrays(wizardSteps,
            super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider));
    }
}
