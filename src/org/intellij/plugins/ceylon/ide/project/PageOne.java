package org.intellij.plugins.ceylon.ide.project;

import com.intellij.openapi.module.Module;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacetState;
import org.intellij.plugins.ceylon.ide.settings.CeylonSettings;

import javax.swing.*;
import java.awt.*;

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
    public void apply(CeylonProject<Module> project, CeylonFacetState state) {
        state.setCompileForJvm(compileForJvm.isSelected());
        state.setCompileToJs(compileToJs.isSelected());
        project.getConfiguration().setProjectOffline(ceylon.language.Boolean.instance(workOffline.isSelected()));
    }

    @Override
    public boolean isModified(CeylonProject<Module> project, CeylonFacetState state) {
        return state.isCompileForJvm() != compileForJvm.isSelected()
                || state.isCompileToJs() != compileToJs.isSelected()
                || !ceylon.language.Boolean.equals(workOffline.isSelected(), project.getConfiguration().getProjectOffline());
    }

    @Override
    public void load(CeylonProject<Module> project, CeylonFacetState state) {
        compileForJvm.setSelected(state.isCompileForJvm());
        compileToJs.setSelected(state.isCompileToJs());

        if (project.getConfiguration().getProjectOffline() != null) {
            workOffline.setSelected(project.getConfiguration().getProjectOffline().booleanValue());
        } else {
            workOffline.setSelected(false);
        }
    }

}
