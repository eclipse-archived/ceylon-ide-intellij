package org.intellij.plugins.ceylon.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

public class PageOneWizardStep extends ModuleWizardStep {
    private final CeylonModuleBuilder moduleBuilder;
    private PageOne step;

    public PageOneWizardStep(CeylonModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
        step = new PageOne();
    }

    @Override
    public JComponent getComponent() {
        return step.getPanel();
    }

    @Override
    public void updateDataModel() {
        step.updateCeylonConfig(moduleBuilder.getConfig());
    }
}
