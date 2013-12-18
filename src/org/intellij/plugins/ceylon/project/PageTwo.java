package org.intellij.plugins.ceylon.project;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.redhat.ceylon.common.config.CeylonConfig;

import javax.swing.*;

import static com.redhat.ceylon.ide.project.config.AbstractProjectConfig.*;

public class PageTwo {
    private JPanel panel;
    private TextFieldWithBrowseButton systemRepository;
    private TextFieldWithBrowseButton outputDirectory;

    public JPanel getPanel() {
        return panel;
    }

    public void updateCeylonConfig(CeylonConfig config) {
        config.setOption(PROJECT_SYSTEM_REPO, systemRepository.getText());
        config.setOption(PROJECT_OUTPUT_DIRECTORY, outputDirectory.getText());
    }
}
