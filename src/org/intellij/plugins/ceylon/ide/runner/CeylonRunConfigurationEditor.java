package org.intellij.plugins.ceylon.ide.runner;

import ceylon.interop.java.JavaIterable;
import ceylon.language.Iterable;
import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModule;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor.klass;
import static org.intellij.plugins.ceylon.ide.ceylonCode.model.getCeylonProjects_.getCeylonProjects;

/**
 * Graphical editor for Ceylon run configurations.
 */
public class CeylonRunConfigurationEditor extends SettingsEditor<CeylonRunConfiguration> {
    private TextFieldWithBrowseButton myRunnableName;
    private TextFieldWithBrowseButton myCeylonModule;
    private JPanel myPanel;
    private RawCommandLineEditor myArguments;
    private RawCommandLineEditor myVmOptions;
    private ModulesComboBox myIdeModule;
    private Project project;

    CeylonRunConfigurationEditor(final Project project) {
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull CeylonRunConfiguration config) {
        myRunnableName.setText(config.getTopLevelNameFull());
        myCeylonModule.setText(config.getCeylonModule());
        myArguments.setText(config.getArguments());
        myVmOptions.setText(config.getVmOptions());
        myIdeModule.setModules(config.getValidModules());
        myIdeModule.setSelectedModule(config.getConfigurationModule().getModule());
    }

    @Override
    protected void applyEditorTo(@NotNull CeylonRunConfiguration config) throws ConfigurationException {
        config.setTopLevelNameFull(myRunnableName.getText());
        config.setCeylonModule(myCeylonModule.getText());
        config.setArguments(myArguments.getText());
        config.setVmOptions(myVmOptions.getText());
        config.getConfigurationModule().setModule(myIdeModule.getSelectedModule());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return myPanel;
    }

    private void createUIComponents() {
        myCeylonModule = new TextFieldWithBrowseButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ModuleChooserDialog dialog = new ModuleChooserDialog(project);
                dialog.show();
                if (dialog.isOK()) {
                    Module module = dialog.getSelectedModule();
                    if (module != null) {
                        myCeylonModule.setText(module.getNameAsString());
                        Object ideArtifact = ((IdeaModule) module).getCeylonProject().getIdeArtifact();
                        myIdeModule.setSelectedModule((com.intellij.openapi.module.Module) ideArtifact);
                    }
                }
            }
        });

        myRunnableName = new TextFieldWithBrowseButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (myCeylonModule.getText() != null) {
                    IdeaCeylonProjects projects = getCeylonProjects(project);
                    if (projects != null) {
                        for (CeylonProject p : new JavaIterable<>(klass(IdeaCeylonProject.class), projects.getCeylonProjects())) {
                            Iterable modules = p.getModules().getFromProject();
                            for (int i = 0; i < modules.getSize(); i++) {
                                IdeaModule mod = (IdeaModule) modules.getFromFirst(i);

                                if (mod.getNameAsString().equals(myCeylonModule.getText())) {
                                    RunnableChooserDialog dialog = new RunnableChooserDialog(project, mod);
                                    dialog.show();
                                    if (dialog.isOK()) {
                                        Declaration decl = dialog.getSelectedDeclaration();
                                        if (decl != null) {
                                            myRunnableName.setText(decl.getQualifiedNameString());
                                        }
                                    }

                                    return;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
