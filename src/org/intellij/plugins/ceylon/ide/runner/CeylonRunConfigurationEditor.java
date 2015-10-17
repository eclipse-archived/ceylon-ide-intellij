package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Graphical editor for Ceylon run configurations.
 */
public class CeylonRunConfigurationEditor extends SettingsEditor<CeylonRunConfiguration> {
    private JTextField myRunnableName;
    private JTextField myCeylonModule;
    private JPanel myPanel;

    public CeylonRunConfigurationEditor(Project project) {
//        myFileName.addBrowseFolderListener("Choose .ceylon file", "Please choose the path to the Ceylon file to run",
//                project, FileChooserDescriptorFactory.createSingleLocalFileDescriptor());
//        myPackage = new PackageNameReferenceEditorCombo(null, project, null, RefactoringBundle.message("choose.destination.package"));
    }

    @Override
    protected void resetEditorFrom(CeylonRunConfiguration config) {
        myRunnableName.setText(config.getTopLevelNameFull());
        myCeylonModule.setText(config.getCeylonModule());
    }

    @Override
    protected void applyEditorTo(CeylonRunConfiguration s) throws ConfigurationException {
        s.setTopLevelNameFull(myRunnableName.getText());
        s.setCeylonModule(myCeylonModule.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }

    private void createUIComponents() {
    }

}
