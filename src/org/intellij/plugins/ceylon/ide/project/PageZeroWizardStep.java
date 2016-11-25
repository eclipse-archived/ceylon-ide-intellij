package org.intellij.plugins.ceylon.ide.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;

import javax.swing.*;

/**
 * Page 0: settings not stored in CeylonConfig.
 */
public class PageZeroWizardStep extends ModuleWizardStep {

    private final CeylonModuleBuilder moduleBuilder;
    private PageZero step;

    PageZeroWizardStep(CeylonModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
        step = new PageZero();
    }

    @Override
    public JComponent getComponent() {
        return step.getPanel();
    }

    @Override
    public void updateDataModel() {
        moduleBuilder.setPageZero(step);
    }
}
