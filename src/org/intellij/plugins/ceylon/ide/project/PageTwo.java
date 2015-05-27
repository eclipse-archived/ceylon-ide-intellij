package org.intellij.plugins.ceylon.ide.project;

import ceylon.language.Boolean;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.redhat.ceylon.ide.common.CeylonProject;
import org.apache.commons.lang.StringUtils;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacetState;

import javax.swing.*;

public class PageTwo implements CeylonConfigForm {
    private JPanel panel;
    private TextFieldWithBrowseButton systemRepository;
    private TextFieldWithBrowseButton outputDirectory;
    private JCheckBox flatClasspath;
    private JCheckBox exportMavenDeps;
    private JList list1;
    private JButton addExternalRepo;
    private JButton addMavenRepo;
    private JButton removeRepo;
    private JButton upButton;
    private JButton downButton;
    private JButton addRepo;
    private JButton addRemoteRepo;

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void apply(CeylonProject<Module> config, CeylonFacetState state) {
        state.setSystemRepository(systemRepository.getText());
        config.getConfiguration().setOutputRepo(outputDirectory.getText());
        config.getConfiguration().setProjectFlatClasspath(Boolean.instance(flatClasspath.isSelected()));
        config.getConfiguration().setProjectAutoExportMavenDependencies(Boolean.instance(exportMavenDeps.isSelected()));
    }

    @Override
    public boolean isModified(CeylonProject<Module> config, CeylonFacetState state) {
        return !StringUtils.equals(state.getSystemRepository(), systemRepository.getText())
                || !StringUtils.equals(config.getConfiguration().getOutputRepo(), outputDirectory.getText())
                || !Boolean.equals(flatClasspath.isSelected(), config.getConfiguration().getProjectFlatClasspath())
                || !Boolean.equals(exportMavenDeps.isSelected(), config.getConfiguration().getProjectAutoExportMavenDependencies())
                ;
    }

    @Override
    public void load(CeylonProject<Module> config, CeylonFacetState state) {
        systemRepository.setText(state.getSystemRepository());
        outputDirectory.setText(config.getConfiguration().getOutputRepo());
        flatClasspath.setSelected(boolValue(config.getConfiguration().getProjectFlatClasspath()));
        exportMavenDeps.setSelected(boolValue(config.getConfiguration().getProjectAutoExportMavenDependencies()));
    }

    private boolean boolValue(Boolean bool) {
        //noinspection SimplifiableConditionalExpression
        return bool == null ? false : bool.booleanValue();
    }
}
