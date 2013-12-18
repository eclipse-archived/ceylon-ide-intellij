package org.intellij.plugins.ceylon.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

public class PageTwoWizardStep extends ModuleWizardStep {
    private final CeylonModuleBuilder moduleBuilder;
    private PageTwo step;

    public PageTwoWizardStep(CeylonModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
        step = new PageTwo();
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