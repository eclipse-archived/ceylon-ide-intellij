import com.intellij.ide.fileTemplates {
    FileTemplateManager
}
import com.intellij.ide.highlighter {
    XmlFileType
}
import com.intellij.ide.util.projectWizard {
    ModuleWizardStep
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.fileChooser {
    FileChooserDescriptorFactory {
        createSingleFolderDescriptor,
        createSingleFileDescriptor
    },
    FileChooser {
        chooseFile
    }
}
import com.intellij.openapi.fileEditor {
    FileEditorManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.roots {
    ModuleRootManager
}
import com.intellij.openapi.ui {
    TextBrowseFolderListener
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtil
}
import com.intellij.ui {
    CollectionListModel,
    JBColor
}
import com.intellij.ui.components {
    JBTextField
}
import com.intellij.util {
    PlatformIcons,
    Consumer
}
import com.redhat.ceylon.common.config {
    Repositories,
    CeylonConfigFinder
}
import com.redhat.ceylon.ide.common.configuration {
    CeylonRepositoryConfigurator
}

import java.awt {
    Component
}
import java.awt.event {
    ActionListener,
    ActionEvent
}
import java.io {
    File,
    IOException
}
import java.lang {
    ObjectArray,
    JString=String,
    Runnable
}

import javax.swing {
    JOptionPane {
        showMessageDialog
    },
    DefaultListCellRenderer,
    JList
}
import javax.swing.event {
    ListSelectionEvent,
    ListSelectionListener,
    DocumentListener,
    DocumentEvent
}

import org.apache.commons.lang {
    StringUtils
}
import org.intellij.plugins.ceylon.ide {
    CeylonBundle {
        message
    }
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.settings {
    JLabelLinkListener
}

shared class CeylonPageTwo()
        extends CeylonRepositoryConfigurator() satisfies CeylonConfigForm {

    value form = PageTwo();
    value listModel = CollectionListModel<String>();

    shared CeylonPageTwo init() {
        value repositories = Repositories.withConfig(CeylonConfigFinder.loadDefaultConfig(null));

        form.myOutputDirectory.text = repositories.outputRepository.url;

        // TODO if we knew the module's location at this time, we could get this from the CeylonConfig
        addGlobalLookupRepo(repositories.getRepository("USER").url);
        addOtherRemoteRepo(repositories.getRepository("REMOTE").url);

        listModel.add(repositories.getRepository("USER").url);
        listModel.add(repositories.getRepository("REMOTE").url);

        form.repoList.model = listModel;
        form.repoList.setCellRenderer(object extends DefaultListCellRenderer() {

            shared actual Component getListCellRendererComponent(JList<out Object> list, Object val, Integer index, Boolean isSelected, Boolean cellHasFocus) {
                value cmp = super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);

                if (isFixedRepoIndex(index)) {
                    this.icon = PlatformIcons.libraryIcon;

                    // TODO why that condition again?
                    if (isFixedRepoIndex(index)) {
                        setForeground(JBColor.gray);
                    }
                } else {
                    this.icon = PlatformIcons.folderIcon;
                }
                return cmp;
            }
        });

        form.systemRepository.addBrowseFolderListener(message("ceylon.facet.settings.selectsystemrepo"),
            null, null, createSingleFolderDescriptor());
        form.myOutputDirectory.addBrowseFolderListener(BrowseOutputDirectoryListener());
        form.repoList.addListSelectionListener(RepoListSelectionListener());
        form.addRemoteRepo.addActionListener(AddRemoteRepoListener());
        form.removeRepo.addActionListener(RemoveRepoListener());
        form.upButton.addActionListener(MoveUpListener());
        form.downButton.addActionListener(MoveDownListener());
        form.addExternalRepo.addActionListener(AddExternalRepoListener());
        form.addMavenRepo.addActionListener(AddMavenRepoListener());
        form.moduleOverrides.addBrowseFolderListener(BrowseOverridesListener());

        if (is JBTextField textField = form.systemRepository.textField) {
            textField.emptyText.setText("Use IDE system modules");
        }

        form.createOverridesButton.addActionListener(object satisfies ActionListener {

            shared actual void actionPerformed(ActionEvent e) {
                value project = form.\imodule.project;
                value consumer = object satisfies Consumer<VirtualFile> {

                    shared actual void consume(VirtualFile folder) {
                        if (folder.findChild("overrides.xml") exists) {
                            showMessageDialog(
                                form.myPanel,
                                message("project.wizard.overrides.exists"),
                                message("project.wizard.overrides.title"),
                                JOptionPane.errorMessage
                            );
                        } else {
                            value tpl = FileTemplateManager.defaultInstance.getInternalTemplate("overrides.xml");
                            ApplicationManager.application.runWriteAction(object satisfies Runnable {

                                shared actual void run() {
                                    try {
                                        value file = folder.createChildData(this, "overrides.xml");
                                        file.setBinaryContent(JString(tpl.text).bytes);
                                        if (exists mr = moduleRoot,
                                            VfsUtil.isAncestor(mr, file, true)) {
                                            value relativePath = VfsUtil.getRelativePath(file, mr);
                                            assert(exists relativePath);
                                            form.moduleOverrides.text = relativePath;
                                        } else {
                                            form.moduleOverrides.text = file.path;
                                        }

                                        FileEditorManager.getInstance(project).openFile(file, true);
                                    }
                                    catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                };
                chooseFile(
                    createSingleFolderDescriptor().withTitle("Select Destination Folder"),
                    project,
                    null,
                    consumer
                );
            }
        });

        object linkListener extends JLabelLinkListener() {
            shared actual void onLinkClicked(String href) {
                value file = File(form.moduleOverrides.text);
                if (exists virtualFile
                        = if (file.absolute)
                            then VfsUtil.findFileByIoFile(file, false)
                        else if (exists mr = moduleRoot)
                            then VfsUtil.findRelativeFile(mr, form.moduleOverrides.text)
                        else null) {
                    FileEditorManager.getInstance(form.\imodule.project)
                            .openFile(virtualFile, true);
                }
            }
        }
        form.overridesLink.addMouseListener(linkListener);
        form.overridesLink.addMouseMotionListener(linkListener);
        form.moduleOverrides.textField.document.addDocumentListener(object satisfies DocumentListener {

            insertUpdate(DocumentEvent e) => updateOverridesLink();
            removeUpdate(DocumentEvent e) => updateOverridesLink();
            changedUpdate(DocumentEvent e) => updateOverridesLink();

            void updateOverridesLink()
                    => form.overridesLink.text = form.moduleOverrides.text.empty
                    then "Module overrides file (customize module resolution):"
                    else  "<html>Module <a href=\"\">overrides file</a> (customize module resolution):</html>";
        });

        return this;
    }

    shared String outputDirectory => form.myOutputDirectory.text;

    panel => form.myPanel;

    shared actual void addAllRepositoriesToList(ObjectArray<JString> repos) {
        for (repo in repos) {
            listModel.add(repo.string);
        }
    }

    shared actual void addRepositoryToList(Integer index, String repo) {
        listModel.add(index, repo);
        form.repoList.selectedIndex = index;
    }

    enableDownButton(Boolean enable)
            => form.downButton.enabled = enable;

    enableRemoveButton(Boolean enable)
            => form.removeRepo.enabled = enable;

    enableUpButton(Boolean enable)
            => form.upButton.enabled = enable;

    shared actual String removeRepositoryFromList(Integer index) {
        value repo = listModel.getElementAt(index);
        listModel.remove(index);
        return repo;
    }

    selection => form.repoList.selectedIndices;


    shared actual void apply(IdeaCeylonProject config) {
        config.ideConfiguration.systemRepository = form.systemRepository.text;
        config.configuration.outputRepo = outputDirectory;
        value overrides = if (form.moduleOverrides.text.empty) then null else form.moduleOverrides.text;
        config.configuration.projectOverrides = overrides;
        config.configuration.projectFlatClasspath = form.flatClasspath.selected;
        config.configuration.projectAutoExportMavenDependencies = form.autoExportMavenDeps.selected;
        config.configuration.projectFullyExportMavenDependencies = form.fullyExportMavenDeps.selected;

        applyToConfiguration(config.configuration);
    }

    shared actual Boolean isModified(IdeaCeylonProject config) {
        value systemRepo = config.ideConfiguration.systemRepository;
        value overrides = config.configuration.projectOverrides;
        return !StringUtils.equals(systemRepo, form.systemRepository.text)
            || !StringUtils.equals(config.configuration.outputRepo, outputDirectory)
            || !StringUtils.equals(overrides, form.moduleOverrides.text)
            || form.flatClasspath.selected != (config.configuration.projectFlatClasspath else false)
            || form.autoExportMavenDeps.selected != (config.configuration.projectAutoExportMavenDependencies else false)
            || form.fullyExportMavenDeps.selected != (config.configuration.projectFullyExportMavenDependencies else false)
            || isRepoConfigurationModified(config.configuration);
    }

    shared actual void load(IdeaCeylonProject config) {
        form.\imodule = config.ideArtifact;
        value systemRepository = config.ideConfiguration.systemRepository;
        //TODO: Investigate. `text` setter has annotation `@Nullable`,
        // but getter has annotation `@NotNull`. Ceylon inferes that null is not assignable to
        // `text` property.
        //form.systemRepository.text = systemRepository?.string;
        form.systemRepository.setText(systemRepository?.string);
        form.myOutputDirectory.text = config.configuration.outputRepo;
        value overrides = config.configuration.projectOverrides;
        //TODO: Investigate. -||-
        //form.moduleOverrides.text = if (!exists overrides) then null else overrides.string;
        form.moduleOverrides.setText(overrides?.string);
        form.flatClasspath.selected = config.configuration.projectFlatClasspath else false;
        form.autoExportMavenDeps.selected = config.configuration.projectAutoExportMavenDependencies else false;
        form.fullyExportMavenDeps.selected = config.configuration.projectFullyExportMavenDependencies else false;
        listModel.removeAll();

        loadFromConfiguration(config.configuration);
    }

    VirtualFile? moduleRoot {
        if (exists m = form.\imodule) {
            value contentEntries = ModuleRootManager.getInstance(m).contentEntries;
            if (contentEntries.size>0) {
                return contentEntries.get(0).file;
            }
        }
        return null;
    }

    class BrowseOutputDirectoryListener()
            extends TextBrowseFolderListener(createSingleFolderDescriptor()) {

        shared actual Project? project => outer.form.\imodule?.project;
        assign project {}

        initialFile => moduleRoot else super.initialFile;

        shared actual String chosenFileToResultingText(VirtualFile file) {
            if (exists modRoot = moduleRoot,
                VfsUtil.isAncestor(modRoot, file, false)) {
                return "./" + (VfsUtil.getRelativePath(file, modRoot) else "");
            }
            return super.chosenFileToResultingText(file);
        }
    }

    class BrowseOverridesListener()
            extends TextBrowseFolderListener(createSingleFileDescriptor(XmlFileType.instance)) {


        shared actual Project? project => outer.form.\imodule?.project;
        assign project {}

        initialFile => moduleRoot else super.initialFile;

        shared actual String chosenFileToResultingText(VirtualFile file) {
            if (exists modRoot = moduleRoot,
                VfsUtil.isAncestor(modRoot, file, true)) {

                return VfsUtil.getRelativePath(file, modRoot) else "";
            }
            return super.chosenFileToResultingText(file);
        }
    }

    class AddExternalRepoListener() satisfies ActionListener {

        shared actual void actionPerformed(ActionEvent e) {
            value file = chooseFile(createSingleFolderDescriptor(), null, null);
            if (exists file) {
                addExternalRepo(file.presentableUrl);
            }
        }
    }

    class AddRemoteRepoListener() satisfies ActionListener {

        shared actual void actionPerformed(ActionEvent e) {
            value uri = JOptionPane.showInputDialog(
                form.myPanel,
                CeylonBundle.message("project.wizard.repo.uri.description"),
                CeylonBundle.message("project.wizard.repo.uri.title"),
                JOptionPane.questionMessage
            );

            if (StringUtils.isNotBlank(uri)) {
                addRemoteRepo(uri);
            }
        }
    }

    class AddMavenRepoListener() satisfies ActionListener {

        shared actual void actionPerformed(ActionEvent e) {
            value dialog = AetherRepositoryDialog(form.myPanel);
            dialog.pack();

            if (dialog.showAndGet()) {
                addAetherRepo(dialog.repository);
            }
        }
    }

    class RepoListSelectionListener() satisfies ListSelectionListener {

        shared actual void valueChanged(ListSelectionEvent e) {
            updateButtonState();
        }
    }

    class RemoveRepoListener() satisfies ActionListener {

        shared actual void actionPerformed(ActionEvent e) {
            removeSelectedRepo();
            form.repoList.clearSelection();
        }
    }

    class MoveUpListener() satisfies ActionListener {

        shared actual void actionPerformed(ActionEvent e) {
            if (!form.repoList.selectionEmpty) {
                moveSelectedReposUp();
            }
        }
    }

    class MoveDownListener() satisfies ActionListener {

        shared actual void actionPerformed(ActionEvent e) {
            if (!form.repoList.selectionEmpty) {
                moveSelectedReposDown();
            }
        }
    }
}

shared class PageTwoWizardStep(CeylonModuleBuilder moduleBuilder)
        extends ModuleWizardStep() {

    value step = CeylonPageTwo().init();

    component => step.panel;

    updateDataModel() => moduleBuilder.setPageTwo(step);
}
