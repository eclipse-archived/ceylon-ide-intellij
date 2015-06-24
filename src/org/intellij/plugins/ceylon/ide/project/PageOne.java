package org.intellij.plugins.ceylon.ide.project;

import com.intellij.openapi.module.Module;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacetState;

import javax.swing.*;

public class PageOne implements CeylonConfigForm {
    private JCheckBox compileForJvm;
    private JCheckBox compileToJs;
    private JPanel panel;
    private JCheckBox workOffline;

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
