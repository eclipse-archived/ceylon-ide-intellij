package org.intellij.plugins.ceylon.runner;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Graphical editor for Ceylon run configurations.
 */
public class CeylonRunConfigurationEditor extends SettingsEditor<CeylonRunConfiguration> {
    private TextFieldWithBrowseButton myFileName;
    private JTextField myRunnableName;
    private JPanel myPanel;

    public CeylonRunConfigurationEditor(Project project) {
        myFileName.addBrowseFolderListener("Choose .ceylon file", "Please choose the path to the Ceylon file to run",
                project, FileChooserDescriptorFactory.createSingleLocalFileDescriptor());
    }

    @Override
    protected void resetEditorFrom(CeylonRunConfiguration config) {
        myFileName.setText(config.getRawFilePath());
        myRunnableName.setText(config.getTopLevelName());
    }

    @Override
    protected void applyEditorTo(CeylonRunConfiguration s) throws ConfigurationException {
        s.setFilePath(myFileName.getText());
        s.setTopLevelName(myRunnableName.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }

    private void createUIComponents() {
    }
}
