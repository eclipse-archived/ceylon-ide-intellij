package org.intellij.plugins.ceylon.ide.project;

import ceylon.language.Boolean;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.Consumer;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.common.config.CeylonConfigFinder;
import com.redhat.ceylon.common.config.Repositories;
import com.redhat.ceylon.ide.common.configuration.CeylonRepositoryConfigurator;
import org.apache.commons.lang.StringUtils;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.settings.JLabelLinkListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static com.intellij.openapi.fileChooser.FileChooser.chooseFile;
import static com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor;
import static com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFolderDescriptor;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.intellij.plugins.ceylon.ide.CeylonBundle.message;

public class PageTwo extends CeylonRepositoryConfigurator implements CeylonConfigForm {
    private CollectionListModel<String> listModel = new CollectionListModel<>();
    private JPanel panel;
    private TextFieldWithBrowseButton systemRepository;
    private TextFieldWithBrowseButton outputDirectory;
    private JCheckBox flatClasspath;
    private JCheckBox autoExportMavenDeps;
    private JCheckBox fullyExportMavenDeps;
    private JButton addExternalRepo;
    private JButton addMavenRepo;
    private JButton removeRepo;
    private JButton upButton;
    private JButton downButton;
    private JButton addRepo;
    private JButton addRemoteRepo;
    private JList<String> repoList;
    private TextFieldWithBrowseButton moduleOverrides;
    private JButton createOverridesButton;
    private JLabel overridesLink;
    private Module module;

    public PageTwo() {
        Repositories repositories = Repositories.withConfig(CeylonConfigFinder.loadDefaultConfig(null));

        outputDirectory.setText(
                repositories
                        .getOutputRepository()
                        .getUrl()
        );

        // TODO if we knew the module's location at this time, we could get this from the CeylonConfig
        addGlobalLookupRepo(repositories.getRepository("USER").getUrl());
        addOtherRemoteRepo(repositories.getRepository("REMOTE").getUrl());

        listModel.add(repositories.getRepository("USER").getUrl());
        listModel.add(repositories.getRepository("REMOTE").getUrl());

        repoList.setModel(listModel);
        repoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component cmp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (isFixedRepoIndex(index)) {
                    setIcon(PlatformIcons.LIBRARY_ICON);

                    if (isFixedRepoIndex(index)) {
                        setForeground(JBColor.GRAY);
                    }
                }
                else {
                    setIcon(PlatformIcons.FOLDER_ICON);
                }

                return cmp;
            }
        });

        systemRepository.addBrowseFolderListener(message("ceylon.facet.settings.selectsystemrepo"),
                null, null, createSingleFolderDescriptor());
        outputDirectory.addBrowseFolderListener(new BrowseOutputDirectoryListener());
        repoList.addListSelectionListener(new RepoListSelectionListener());
        addRemoteRepo.addActionListener(new AddRemoteRepoListener());
        removeRepo.addActionListener(new RemoveRepoListener());
        upButton.addActionListener(new MoveUpListener());
        downButton.addActionListener(new MoveDownListener());
        addExternalRepo.addActionListener(new AddExternalRepoListener());
        addMavenRepo.addActionListener(new AddMavenRepoListener());
        moduleOverrides.addBrowseFolderListener(new BrowseOverridesListener());

        JTextField textField = systemRepository.getTextField();
        if (textField instanceof JBTextField) {
            ((JBTextField) textField).getEmptyText().setText("Use IDE system modules");
        }
        createOverridesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Project project = module == null ? null : module.getProject();

                Consumer<VirtualFile> consumer = new Consumer<VirtualFile>() {
                    @Override
                    public void consume(final VirtualFile folder) {
                        if (folder.findChild("overrides.xml") != null) {
                            showMessageDialog(
                                    panel,
                                    message("project.wizard.overrides.exists"),
                                    message("project.wizard.overrides.title"),
                                    JOptionPane.ERROR_MESSAGE
                            );
                        } else {
                            final FileTemplate tpl = FileTemplateManager.getDefaultInstance()
                                    .getInternalTemplate("overrides.xml");
                                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            VirtualFile file = folder.createChildData(this, "overrides.xml");
                                            file.setBinaryContent(tpl.getText().getBytes());
                                            VirtualFile moduleRoot = getModuleRoot();

                                            if (moduleRoot != null && VfsUtil.isAncestor(moduleRoot, file, true)) {
                                                //noinspection ConstantConditions
                                                moduleOverrides.setText(VfsUtil.getRelativePath(file, moduleRoot));
                                            } else {
                                                moduleOverrides.setText(file.getPath());
                                            }

                                            if (project != null) {
                                                FileEditorManager.getInstance(project).openFile(file, true);
                                            }
                                        } catch (IOException e1) {
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
        JLabelLinkListener linkListener = new JLabelLinkListener() {
            @Override
            public void onLinkClicked(String href) {
                File file = new File(moduleOverrides.getText());
                VirtualFile virtualFile;
                Project project = getProject();
                VirtualFile moduleRoot = getModuleRoot();

                if (file.isAbsolute()) {
                    virtualFile = VfsUtil.findFileByIoFile(file, false);
                } else if (moduleRoot != null) {
                    virtualFile = VfsUtil.findRelativeFile(moduleRoot, moduleOverrides.getText());
                } else {
                    virtualFile = null;
                }

                if (virtualFile != null && project != null) {
                    FileEditorManager.getInstance(project).openFile(virtualFile, true);
                }
            }
        };
        overridesLink.addMouseListener(linkListener);
        overridesLink.addMouseMotionListener(linkListener);
        moduleOverrides.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateOverridesLink();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateOverridesLink();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateOverridesLink();
            }

            private void updateOverridesLink() {
                if (moduleOverrides.getText().isEmpty()) {
                    overridesLink.setText("Module overrides file (customize module resolution):");
                } else {
                    overridesLink.setText("<html>Module <a href=\"\">" +
                            "overrides file</a> (customize module resolution):</html>");
                }
            }
        });
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    public String getOutputDirectory() {
        return outputDirectory.getText();
    }

    @Override
    public void apply(IdeaCeylonProject config) {
        config.getIdeConfiguration().setSystemRepository(ceylon.language.String.instance(systemRepository.getText()));
        config.getConfiguration().setOutputRepo(outputDirectory.getText());
        String overrides = moduleOverrides.getText().isEmpty() ? null : moduleOverrides.getText();
        config.getConfiguration().setProjectOverrides(ceylon.language.String.instance(overrides));
        config.getConfiguration().setProjectFlatClasspath(Boolean.instance(flatClasspath.isSelected()));
        config.getConfiguration().setProjectAutoExportMavenDependencies(Boolean.instance(autoExportMavenDeps.isSelected()));
        config.getConfiguration().setProjectFullyExportMavenDependencies(Boolean.instance(fullyExportMavenDeps.isSelected()));
        applyToConfiguration(config.getConfiguration());
    }

    @Override
    public boolean isModified(IdeaCeylonProject config) {
        ceylon.language.String systemRepo = config.getIdeConfiguration().getSystemRepository();
        ceylon.language.String overrides = config.getConfiguration().getProjectOverrides();
        return !StringUtils.equals(systemRepo == null ? null : systemRepo.toString(), systemRepository.getText())
                || !StringUtils.equals(config.getConfiguration().getOutputRepo(), outputDirectory.getText())
                || !StringUtils.equals(overrides == null ? null : overrides.toString(), moduleOverrides.getText())
                || !Boolean.equals(flatClasspath.isSelected(), config.getConfiguration().getProjectFlatClasspath())
                || !Boolean.equals(autoExportMavenDeps.isSelected(), config.getConfiguration().getProjectAutoExportMavenDependencies())
                || !Boolean.equals(fullyExportMavenDeps.isSelected(), config.getConfiguration().getProjectFullyExportMavenDependencies())
                || isRepoConfigurationModified(config.getConfiguration())
                ;
    }

    @Override
    public void load(IdeaCeylonProject config) {
        module = config.getIdeArtifact();
        ceylon.language.String systemRepository = config.getIdeConfiguration().getSystemRepository();
        this.systemRepository.setText(systemRepository == null ? null : systemRepository.toString());
        outputDirectory.setText(config.getConfiguration().getOutputRepo());
        ceylon.language.String overrides = config.getConfiguration().getProjectOverrides();
        moduleOverrides.setText(overrides == null ? null : overrides.toString());
        flatClasspath.setSelected(boolValue(config.getConfiguration().getProjectFlatClasspath()));
        autoExportMavenDeps.setSelected(boolValue(config.getConfiguration().getProjectAutoExportMavenDependencies()));
        fullyExportMavenDeps.setSelected(boolValue(config.getConfiguration().getProjectFullyExportMavenDependencies()));

        listModel.removeAll();
        loadFromConfiguration(config.getConfiguration());
    }

    private boolean boolValue(Boolean bool) {
        //noinspection SimplifiableConditionalExpression
        return bool == null ? false : bool.booleanValue();
    }

    @Override
    public int[] getSelection() {
        return repoList.getSelectedIndices();
    }

    @Override
    public Object enableRemoveButton(boolean enabled) {
        removeRepo.setEnabled(enabled);
        return null;
    }

    @Override
    public Object enableUpButton(boolean enabled) {
        upButton.setEnabled(enabled);
        return null;
    }

    @Override
    public Object enableDownButton(boolean enabled) {
        downButton.setEnabled(enabled);
        return null;
    }

    @Override
    public String removeRepositoryFromList(long index) {
        String repo = listModel.getElementAt((int) index);
        listModel.remove((int) index);
        return repo;
    }

    @Override
    public Object addRepositoryToList(long index, String repo) {
        listModel.add((int) index, repo);
        repoList.setSelectedIndex((int) index);
        return null;
    }

    @Override
    public Object addAllRepositoriesToList(String[] repos) {
        for (String repo : repos) {
            listModel.add(repo);
        }
        return null;
    }

    @Nullable
    private Project getProject() {
        return module == null ? null : module.getProject();
    }

    @Nullable
    private VirtualFile getModuleRoot() {
        if (module != null) {
            ContentEntry[] contentEntries = ModuleRootManager.getInstance(module).getContentEntries();
            if (contentEntries.length > 0) {
                return contentEntries[0].getFile();
            }
        }
        return null;
    }

    private class BrowseOutputDirectoryListener extends TextBrowseFolderListener {

        BrowseOutputDirectoryListener() {
            super(createSingleFolderDescriptor());
        }

        @Nullable
        @Override
        protected Project getProject() {
            return PageTwo.this.getProject();
        }

        @Nullable
        @Override
        protected VirtualFile getInitialFile() {
            VirtualFile moduleRoot = getModuleRoot();
            if (moduleRoot != null) {
                return moduleRoot;
            }
            return super.getInitialFile();
        }

        @NotNull
        @Override
        protected String chosenFileToResultingText(@NotNull VirtualFile file) {
            VirtualFile modRoot = getModuleRoot();

            if (modRoot != null && VfsUtil.isAncestor(modRoot, file, false)) {
                return "./" + VfsUtil.getRelativePath(file, modRoot);
            }

            return super.chosenFileToResultingText(file);
        }
    }

    private class BrowseOverridesListener extends TextBrowseFolderListener {

        BrowseOverridesListener() {
            super(createSingleFileDescriptor(XmlFileType.INSTANCE));
        }

        @Nullable
        @Override
        protected Project getProject() {
            return PageTwo.this.getProject();
        }

        @Nullable
        @Override
        protected VirtualFile getInitialFile() {
            VirtualFile moduleRoot = getModuleRoot();
            if (moduleRoot != null) {
                return moduleRoot;
            }
            return super.getInitialFile();
        }

        @NotNull
        @Override
        protected String chosenFileToResultingText(@NotNull VirtualFile file) {
            VirtualFile modRoot = getModuleRoot();

            if (modRoot != null && VfsUtil.isAncestor(modRoot, file, true)) {
                //noinspection ConstantConditions
                return VfsUtil.getRelativePath(file, modRoot);
            }

            return super.chosenFileToResultingText(file);
        }
    }

    private class AddExternalRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            VirtualFile file = chooseFile(createSingleFolderDescriptor(), null, null);
            if (file != null) {
                addExternalRepo(file.getPresentableUrl());
            }
        }
    }

    private class AddRemoteRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String uri = JOptionPane.showInputDialog(PageTwo.this.panel,
                    message("project.wizard.repo.uri.description"),
                    message("project.wizard.repo.uri.title"),
                    JOptionPane.QUESTION_MESSAGE);

            if (StringUtils.isNotBlank(uri)) {
                addRemoteRepo(uri);
            }
        }
    }

    private class AddMavenRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            final AetherRepositoryDialog dialog = new AetherRepositoryDialog(panel);
            dialog.pack();

            if (dialog.showAndGet()) {
                String repo = dialog.getRepository();

                if (repo != null) {
                    addAetherRepo(repo);
                }
            }
        }
    }

    private class RepoListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            updateButtonState();
        }
    }

    private class RemoveRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            removeSelectedRepo();
            repoList.clearSelection();
        }
    }

    private class MoveUpListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!repoList.isSelectionEmpty()) {
                moveSelectedReposUp();
            }
        }
    }

    private class MoveDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!repoList.isSelectionEmpty()) {
                moveSelectedReposDown();
            }
        }
    }
}
