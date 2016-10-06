package org.intellij.plugins.ceylon.ide.runner;

import ceylon.interop.java.JavaIterable;
import ceylon.language.Iterable;
import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.RawCommandLineEditor;
import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
    private JComboBox<Backend> myBackend;
    private Project project;
    private final DefaultComboBoxModel<Backend> model;

    CeylonRunConfigurationEditor(final Project project) {
        this.project = project;

        model = new DefaultComboBoxModel<>(getBackends(null));
        myBackend.setModel(model);
        myBackend.setRenderer(new ListCellRendererWrapper<Backend>() {
            @Override
            public void customize(JList list, Backend value, int index, boolean selected, boolean hasFocus) {
                setText(value.name);
            }
        });

    }

    @NotNull
    private Backend[] getBackends(@Nullable Module module) {

        if (module == null || module.getNativeBackends().none()) {
            // TODO use this when we support Dart and other backends
//        ArrayList<Backend> backendsList = new ArrayList<>();
//        for (Backend backend : Backend.getRegisteredBackends()) {
//            backendsList.add(backend);
//        }
//        return backendsList.toArray(new Backend[backendsList.size()]);
            return new Backend[]{Backend.Java, Backend.JavaScript};
        } else {
            ArrayList<Backend> backends = new ArrayList<>();
            for (Backend backend : module.getNativeBackends()) {
                backends.add(backend);
            }
            return backends.toArray(new Backend[backends.size()]);
        }
    }

    @Override
    protected void resetEditorFrom(@NotNull CeylonRunConfiguration config) {
        myRunnableName.setText(config.getTopLevelNameFull());
        myCeylonModule.setText(config.getCeylonModule());
        myArguments.setText(config.getArguments());
        myVmOptions.setText(config.getVmOptions());
        myIdeModule.setModules(config.getValidModules());
        myIdeModule.setSelectedModule(config.getConfigurationModule().getModule());
        myBackend.getModel().setSelectedItem(config.getBackend());
    }

    @Override
    protected void applyEditorTo(@NotNull CeylonRunConfiguration config) throws ConfigurationException {
        config.setTopLevelNameFull(myRunnableName.getText());
        config.setCeylonModule(myCeylonModule.getText());
        config.setArguments(myArguments.getText());
        config.setVmOptions(myVmOptions.getText());
        config.getConfigurationModule().setModule(myIdeModule.getSelectedModule());
        config.setBackend(myBackend.getModel().getElementAt(myBackend.getSelectedIndex()));
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
        myCeylonModule.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                IdeaModule module = findModuleByName(myCeylonModule.getText());

                model.removeAllElements();
                for (Backend backend : getBackends(module)) {
                    model.addElement(backend);
                }
            }
        });

        myRunnableName = new TextFieldWithBrowseButton(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IdeaModule module = findModuleByName(myCeylonModule.getText());
                if (module != null) {
                    RunnableChooserDialog dialog = new RunnableChooserDialog(project, module);
                    dialog.show();
                    if (dialog.isOK()) {
                        Declaration decl = dialog.getSelectedDeclaration();
                        if (decl != null) {
                            myRunnableName.setText(decl.getQualifiedNameString());
                        }
                    }
                }
            }
        });
    }

    @Nullable
    private IdeaModule findModuleByName(@Nullable String name) {
        IdeaCeylonProjects projects = getCeylonProjects(project);
        if (name != null && !name.isEmpty() && projects != null) {
            for (CeylonProject p : new JavaIterable<>(klass(IdeaCeylonProject.class), projects.getCeylonProjects())) {
                Iterable modules = p.getModules().getFromProject();
                for (int i = 0; i < modules.getSize(); i++) {
                    IdeaModule mod = (IdeaModule) modules.getFromFirst(i);

                    if (mod.getNameAsString().equals(myCeylonModule.getText())) {
                        return mod;
                    }
                }
            }
        }

        return null;
    }
}
