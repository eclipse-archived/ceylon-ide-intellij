package org.intellij.plugins.ceylon.ide.project;

import com.intellij.openapi.module.Module;
import com.redhat.ceylon.ide.common.model.CeylonProject;
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
    public void apply(CeylonProject<Module> project) {
        project.getIdeConfiguration().setCompileToJvm(ceylon.language.Boolean.instance(compileForJvm.isSelected()));
        project.getIdeConfiguration().setCompileToJs(ceylon.language.Boolean.instance(compileToJs.isSelected()));
        project.getConfiguration().setProjectOffline(ceylon.language.Boolean.instance(workOffline.isSelected()));
    }

    @Override
    public boolean isModified(CeylonProject<Module> project) {
        return project.getIdeConfiguration().getCompileToJvm().booleanValue() != compileForJvm.isSelected()
                || project.getIdeConfiguration().getCompileToJs().booleanValue() != compileToJs.isSelected()
                || !ceylon.language.Boolean.equals(workOffline.isSelected(), project.getConfiguration().getProjectOffline());
    }

    @Override
    public void load(CeylonProject<Module> project) {
        compileForJvm.setSelected(project.getIdeConfiguration().getCompileToJvm().booleanValue());
        compileToJs.setSelected(project.getIdeConfiguration().getCompileToJs().booleanValue());

        if (project.getConfiguration().getProjectOffline() != null) {
            workOffline.setSelected(project.getConfiguration().getProjectOffline().booleanValue());
        } else {
            workOffline.setSelected(false);
        }
    }

}
