package org.intellij.plugins.ceylon.ide.project;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.plugins.ceylon.ide.CeylonBundle;

import javax.swing.*;

public class AetherRepositoryForm {
    private JPanel mainPanel;
    private TextFieldWithBrowseButton repoField;

    public AetherRepositoryForm() {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(XmlFileType.INSTANCE)
                .withFileFilter(new Condition<VirtualFile>() {
                    @Override
                    public boolean value(VirtualFile virtualFile) {
                        return virtualFile.getName().equals("settings.xml");
                    }
                });
        repoField.addBrowseFolderListener(CeylonBundle.message("project.wizard.repo.maven.selectpath"), null, null, descriptor);
    }

    public TextFieldWithBrowseButton getRepoField() {
        return repoField;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

}
