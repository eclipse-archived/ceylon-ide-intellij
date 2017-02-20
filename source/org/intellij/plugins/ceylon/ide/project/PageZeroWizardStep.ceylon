import com.intellij.ide.util.projectWizard {
    ModuleWizardStep
}

"Page 0: settings not stored in CeylonConfig."
shared class PageZeroWizardStep(CeylonModuleBuilder moduleBuilder) extends ModuleWizardStep() {

    value step = PageZero();

    component => step.panel;

    updateDataModel()
            => moduleBuilder.setPageZero(step);
}
