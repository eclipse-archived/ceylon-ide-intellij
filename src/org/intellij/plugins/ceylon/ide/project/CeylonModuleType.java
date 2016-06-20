package org.intellij.plugins.ceylon.ide.project;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.util.ArrayUtil;
import org.intellij.plugins.ceylon.ide.CeylonBundle;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

/**
 * IntelliJ module containing Ceylon sources files.
 */
public class CeylonModuleType extends ModuleType<CeylonModuleBuilder> {

    @NonNls
    private static final String ID = "CEYLON_MODULE";
    private static CeylonModuleType INSTANCE = new CeylonModuleType();

    public CeylonModuleType() {
        super(ID);
    }

    public static CeylonModuleType getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public CeylonModuleBuilder createModuleBuilder() {
        return new CeylonModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Ceylon";
    }

    @NotNull
    @Override
    public String getDescription() {
        return CeylonBundle.message("project.wizard.module.description");
    }

    @Override
    public Icon getBigIcon() {
        return icons_.get_().getProject();
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return icons_.get_().getProject();
    }


    @Nullable
    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(settingsStep, moduleBuilder, new Condition<SdkTypeId>() {
            @Override
            public boolean value(SdkTypeId sdkType) {
                return moduleBuilder.isSuitableSdkType(sdkType);
            }
        });
    }

    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull CeylonModuleBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider) {
        ArrayList<ModuleWizardStep> steps = new ArrayList<>();

        steps.add(new PageOneWizardStep(moduleBuilder));
        steps.add(new PageTwoWizardStep(moduleBuilder));

        final ModuleWizardStep[] wizardSteps = steps.toArray(new ModuleWizardStep[steps.size()]);
        return ArrayUtil.mergeArrays(wizardSteps, super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider));
    }
}
