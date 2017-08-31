import com.intellij.execution.configurations {
    RunConfiguration
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.ui {
    TextFieldWithBrowseButton
}
import com.intellij.ui {
    DocumentAdapter
}

import javax.swing.event {
    DocumentEvent
}

import org.intellij.plugins.ceylon.ide.model {
    IdeaModule,
    getCeylonProjects
}
import com.redhat.ceylon.common {
    Backend
}

class CeylonRunConfigurationEditor(Project project)
        extends AbstractCeylonRunConfigurationEditor(project) {

    shared actual void resetEditorFrom(RunConfiguration config) {
        assert (is CeylonRunConfiguration config);
        myRunnableName.setText(config.topLevelNameFull);
        myCeylonModule.setText(config.ceylonModule);
        myArguments.setText(config.arguments);
        myVmOptions.setText(config.vmOptions);
        myIdeModule.setModules(config.validModules);
        myIdeModule.selectedModule = config.configurationModule.\imodule;
        myBackend.model.selectedItem = config.backend;
    }

    shared actual void applyEditorTo(RunConfiguration config) {
        assert (is CeylonRunConfiguration config);
        config.topLevelNameFull = myRunnableName.text;
        config.ceylonModule = myCeylonModule.text;
        config.arguments = myArguments.text;
        config.vmOptions = myVmOptions.text;
        config.configurationModule.\imodule = myIdeModule.selectedModule;
        config.backend
            = myBackend.model.getElementAt(myBackend.selectedIndex)
            else Backend.java;
    }

    createEditor() => myPanel;

    shared actual void createUIComponents() {

        myCeylonModule = TextFieldWithBrowseButton((e) {
            value dialog = ModuleChooserDialog(project);
            dialog.show();
            if (dialog.ok,
                is IdeaModule mod = dialog.selectedModule) {
                myCeylonModule.setText(mod.nameAsString);
                myIdeModule.selectedModule = mod.ceylonProject?.ideArtifact;
            }
        });

        myCeylonModule.textField.document.addDocumentListener(object extends DocumentAdapter() {
            shared actual void textChanged(DocumentEvent e) {
                value mod = findModuleByName(myCeylonModule.text);
                model.removeAllElements();
                for (backend in getBackends(mod)) {
                    model.addElement(backend);
                }
            }
        });

        myRunnableName = TextFieldWithBrowseButton((e) {
            if (exists mod = findModuleByName(myCeylonModule.text)) {
                value dialog = RunnableChooserDialog(project, mod);
                dialog.show();
                if (dialog.ok,
                    exists decl = dialog.selectedDeclaration) {
                    myRunnableName.setText(decl.qualifiedNameString);
                }
            }
        });
    }

    IdeaModule? findModuleByName(String name) {
        if (!name.empty, exists projects = getCeylonProjects(project)) {
            for (p in projects.ceylonProjects) {
                assert (exists modules = p.modules?.fromProject);
                for (mod in modules) {
                    assert (is IdeaModule mod);
                    if (mod.nameAsString==myCeylonModule.text) {
                        return mod;
                    }
                }
            }
        }
        return null;
    }

}