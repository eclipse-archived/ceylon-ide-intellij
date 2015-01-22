package org.intellij.plugins.ceylon.ide.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;

/**
 * IntelliJ module containing Ceylon sources files.
 */
public class CeylonModuleType extends ModuleType<CeylonModuleBuilder> {

    private static CeylonModuleType INSTANCE = new CeylonModuleType();

    @NonNls private static final String ID = "CEYLON_MODULE";

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
        return "Creates a new module containing Ceylon files";
    }

    @Override
    public Icon getBigIcon() {
        return IconLoader.getIcon("/icons/ceylon.png");
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return IconLoader.getIcon("/icons/ceylon.png");
    }

    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull CeylonModuleBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider) {
        ArrayList<ModuleWizardStep> steps = new ArrayList<>();

        if (!wizardContext.isCreatingNewProject()) {
            steps.add(new PageOneWizardStep(moduleBuilder));
            steps.add(new PageTwoWizardStep(moduleBuilder));
        }

        final ModuleWizardStep[] wizardSteps = steps.toArray(new ModuleWizardStep[steps.size()]);
        return ArrayUtil.mergeArrays(wizardSteps, super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider));
    }
}
