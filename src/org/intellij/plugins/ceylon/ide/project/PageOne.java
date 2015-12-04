package org.intellij.plugins.ceylon.ide.project;

import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.settings.CeylonSettings;

import javax.swing.*;

public class PageOne implements CeylonConfigForm {
    private JCheckBox compileForJvm;
    private JCheckBox compileToJs;
    private JPanel panel;
    private JCheckBox workOffline;

    public PageOne() {
        String defaultVm = CeylonSettings.getInstance().getDefaultTargetVm();
        compileForJvm.setSelected(!defaultVm.equals("js"));
        compileToJs.setSelected(!defaultVm.equals("jvm"));
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void apply(IdeaCeylonProject project) {
        project.getIdeConfiguration().setCompileToJvm(ceylon.language.Boolean.instance(compileForJvm.isSelected()));
        project.getIdeConfiguration().setCompileToJs(ceylon.language.Boolean.instance(compileToJs.isSelected()));
        project.getConfiguration().setProjectOffline(ceylon.language.Boolean.instance(workOffline.isSelected()));
    }

    @Override
    public boolean isModified(IdeaCeylonProject project) {
        return project.getIdeConfiguration().getCompileToJvm() == null
                || project.getIdeConfiguration().getCompileToJs() == null
                || project.getConfiguration().getProjectOffline() == null
                || project.getIdeConfiguration().getCompileToJvm().booleanValue() != compileForJvm.isSelected()
                || project.getIdeConfiguration().getCompileToJs().booleanValue() != compileToJs.isSelected()
                || project.getConfiguration().getProjectOffline().booleanValue() != workOffline.isSelected();
    }

    @Override
    public void load(IdeaCeylonProject project) {
        String defaultVm = CeylonSettings.getInstance().getDefaultTargetVm();
        compileForJvm.setSelected(safeNullBoolean(project.getIdeConfiguration().getCompileToJvm(), !defaultVm.equals("js")));
        compileToJs.setSelected(safeNullBoolean(project.getIdeConfiguration().getCompileToJs(), !defaultVm.equals("jvm")));
        workOffline.setSelected(safeNullBoolean(project.getConfiguration().getProjectOffline(), false));
    }

    private boolean safeNullBoolean(ceylon.language.Boolean bool, boolean def) {
        return bool == null ? def : bool.booleanValue();
    }
}
