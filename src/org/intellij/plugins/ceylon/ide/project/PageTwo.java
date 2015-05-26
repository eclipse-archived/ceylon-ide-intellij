package org.intellij.plugins.ceylon.ide.project;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.redhat.ceylon.common.config.CeylonConfig;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;

import static com.redhat.ceylon.ide.project.config.AbstractProjectConfig.PROJECT_OUTPUT_DIRECTORY;
import static com.redhat.ceylon.ide.project.config.AbstractProjectConfig.PROJECT_SYSTEM_REPO;

public class PageTwo implements CeylonConfigForm {
    private JPanel panel;
    private TextFieldWithBrowseButton systemRepository;
    private TextFieldWithBrowseButton outputDirectory;

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void updateCeylonConfig(CeylonConfig config) {
        config.setOption(PROJECT_SYSTEM_REPO, systemRepository.getText());
        config.setOption(PROJECT_OUTPUT_DIRECTORY, outputDirectory.getText());
    }

    @Override
    public boolean isModified(CeylonConfig config) {
        return !StringUtils.equals(config.getOption(PROJECT_SYSTEM_REPO), systemRepository.getText())
                || !StringUtils.equals(config.getOption(PROJECT_OUTPUT_DIRECTORY), outputDirectory.getText());
    }

    @Override
    public void loadCeylonConfig(CeylonConfig config) {
        systemRepository.setText(config.getOption(PROJECT_SYSTEM_REPO));
        outputDirectory.setText(config.getOption(PROJECT_OUTPUT_DIRECTORY));
    }
}
