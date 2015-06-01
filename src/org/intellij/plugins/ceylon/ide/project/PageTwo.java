package org.intellij.plugins.ceylon.ide.project;

import ceylon.language.Boolean;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.JBColor;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.common.config.CeylonConfigFinder;
import com.redhat.ceylon.common.config.Repositories;
import com.redhat.ceylon.ide.common.CeylonProject;
import org.apache.commons.lang.StringUtils;
import org.intellij.plugins.ceylon.ide.CeylonBundle;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacetState;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageTwo implements CeylonConfigForm {
    private CollectionListModel<String> listModel = new CollectionListModel<>();
    private JPanel panel;
    private TextFieldWithBrowseButton systemRepository;
    private TextFieldWithBrowseButton outputDirectory;
    private JCheckBox flatClasspath;
    private JCheckBox exportMavenDeps;
    private JButton addExternalRepo;
    private JButton addMavenRepo;
    private JButton removeRepo;
    private JButton upButton;
    private JButton downButton;
    private JButton addRepo;
    private JButton addRemoteRepo;
    private JList<String> repoList;

    private List<String> projectLocalRepos = new ArrayList<>();
    private List<String> globalLookupRepos = new ArrayList<>();
    private List<String> projectRemoteRepos = new ArrayList<>();
    private List<String> otherRemoteRepos = new ArrayList<>();

    public PageTwo() {
        Repositories repositories = Repositories.withConfig(CeylonConfigFinder.loadDefaultConfig(null));

        outputDirectory.setText(
                repositories
                        .getOutputRepository()
                        .getUrl()
        );

        // TODO if we knew the module's location at this time, we could get this from the CeylonConfig
        globalLookupRepos.add(repositories.getRepository("USER").getUrl());
        otherRemoteRepos.add(repositories.getRepository("REMOTE").getUrl());

        listModel.add(globalLookupRepos);
        listModel.add(otherRemoteRepos);

        repoList.setModel(listModel);
        repoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component cmp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Repositories.Repository) {
                    setText(((Repositories.Repository) value).getUrl());
                    setIcon(PlatformIcons.LIBRARY_ICON);

                    if (isFixedRepoIndex(index)) {
                        setForeground(JBColor.GRAY);
                    }
                }

                return cmp;
            }
        });

        repoList.addListSelectionListener(new RepoListSelectionListener());
        addRemoteRepo.addActionListener(new AddRemoteRepoListener());
        removeRepo.addActionListener(new RemoveRepoListener());
        upButton.addActionListener(new MoveUpListener());
        downButton.addActionListener(new MoveDownListener());
        addExternalRepo.addActionListener(new AddExternalRepoListener());
        addMavenRepo.addActionListener(new AddMavenRepoListener());
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void apply(CeylonProject<Module> config, CeylonFacetState state) {
        state.setSystemRepository(systemRepository.getText());
        config.getConfiguration().setOutputRepo(outputDirectory.getText());
        config.getConfiguration().setProjectFlatClasspath(Boolean.instance(flatClasspath.isSelected()));
        config.getConfiguration().setProjectAutoExportMavenDependencies(Boolean.instance(exportMavenDeps.isSelected()));
    }

    @Override
    public boolean isModified(CeylonProject<Module> config, CeylonFacetState state) {
        return !StringUtils.equals(state.getSystemRepository(), systemRepository.getText())
                || !StringUtils.equals(config.getConfiguration().getOutputRepo(), outputDirectory.getText())
                || !Boolean.equals(flatClasspath.isSelected(), config.getConfiguration().getProjectFlatClasspath())
                || !Boolean.equals(exportMavenDeps.isSelected(), config.getConfiguration().getProjectAutoExportMavenDependencies())
                ;
    }

    @Override
    public void load(CeylonProject<Module> config, CeylonFacetState state) {
        systemRepository.setText(state.getSystemRepository());
        outputDirectory.setText(config.getConfiguration().getOutputRepo());
        flatClasspath.setSelected(boolValue(config.getConfiguration().getProjectFlatClasspath()));
        exportMavenDeps.setSelected(boolValue(config.getConfiguration().getProjectAutoExportMavenDependencies()));
    }

    private boolean boolValue(Boolean bool) {
        //noinspection SimplifiableConditionalExpression
        return bool == null ? false : bool.booleanValue();
    }

    private boolean isFixedRepoIndex(int index) {
        if (!globalLookupRepos.isEmpty()
                && index >= projectLocalRepos.size()
                && index < projectLocalRepos.size() +
                globalLookupRepos.size()) {
            return true;
        }
        if (!otherRemoteRepos.isEmpty()
                && index >= projectLocalRepos.size() +
                globalLookupRepos.size() +
                projectRemoteRepos.size()
                && index < projectLocalRepos.size() +
                globalLookupRepos.size() +
                projectRemoteRepos.size() +
                otherRemoteRepos.size()) {
            return true;
        }
        return false;
    }

    private void updateButtonState() {
        updateRemoveRepoButtonState();
        updateUpDownButtonState();
    }

    private void updateRemoveRepoButtonState() {
        int[] selectionIndices = repoList.getSelectedIndices();
        for (int index : selectionIndices) {
            if (!isFixedRepoIndex(index)) {
                removeRepo.setEnabled(true);
                return;
            }
        }
        removeRepo.setEnabled(false);
    }

    private void updateUpDownButtonState() {
        boolean isUpEnabled = false;
        boolean isDownEnabled = false;

        int[] selectionIndices = repoList.getSelectedIndices();
        if (selectionIndices.length == 1) {
            int index = selectionIndices[0];
            if (index > 0 &&
                    !isFixedRepoIndex(index)) {
                isUpEnabled = true;
            }
            int maxIndex =
                    projectLocalRepos.size() +
                            globalLookupRepos.size() +
                            projectRemoteRepos.size() - 1;
            if (index < maxIndex &&
                    !isFixedRepoIndex(index)) {
                isDownEnabled = true;
            }
        }

        upButton.setEnabled(isUpEnabled);
        downButton.setEnabled(isDownEnabled);
    }

    private void addProjectRepo(String repo, int index, boolean isLocalRepo) {
        if (isLocalRepo && projectLocalRepos.contains(repo)) {
            return;
        }
        if (!isLocalRepo && projectRemoteRepos.contains(repo)) {
            return;
        }

        if (isLocalRepo) {
            projectLocalRepos.add(index, repo);
        } else {
            int remoteIndex = index -
                    projectLocalRepos.size() -
                    globalLookupRepos.size();
            projectRemoteRepos.add(remoteIndex, repo);
        }

        listModel.add(index, repo);
        repoList.setSelectedIndex(index);

//        validate();
        updateButtonState();
    }

    private class AddExternalRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, null);
            if (file != null) {
                addProjectRepo(file.getPresentableUrl(), 0, true);
            }
        }
    }

    private class AddRemoteRepoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String uri = JOptionPane.showInputDialog(PageTwo.this.panel, CeylonBundle.message("project.wizard.repo.uri.description"), CeylonBundle.message("project.wizard.repo.uri.title"), JOptionPane.QUESTION_MESSAGE);

            if (StringUtils.isNotBlank(uri)) {
                int index = projectLocalRepos.size() +
                                globalLookupRepos.size() +
                                projectRemoteRepos.size();
                addProjectRepo(uri, index, false);
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
                    int index =
                            projectLocalRepos.size() +
                                    globalLookupRepos.size() +
                                    projectRemoteRepos.size();

                    if (repo.equals("")) {
                        addProjectRepo("aether", index, false);
                    } else {
                        addProjectRepo("aether:" + repo, index, false);
                    }
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
            int[] selection = repoList.getSelectedIndices();
            Arrays.sort(selection);
            for (int i = selection.length - 1; i >= 0; i--) {
                int index = selection[i];
                if (!isFixedRepoIndex(index)) {
                    String repo = listModel.getElementAt(index);
                    listModel.remove(index);
                    projectLocalRepos.remove(repo);
                    projectRemoteRepos.remove(repo);
                }
            }
            repoList.clearSelection();
            updateButtonState();
        }
    }

    private class MoveUpListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!repoList.isSelectionEmpty()) {
                int index = repoList.getSelectedIndex();
                String repo = listModel.getElementAt(index);
                listModel.remove(index);

                if (index > 0 && index <= projectLocalRepos.size()) {
                    projectLocalRepos.remove(index);
                    addProjectRepo(repo, index - 1, true);
                }
                if (index == projectLocalRepos.size() + globalLookupRepos.size()) {
                    projectRemoteRepos.remove(repo);
                    addProjectRepo(repo, projectLocalRepos.size(), true);
                }
                if (index > projectLocalRepos.size() + globalLookupRepos.size()) {
                    projectRemoteRepos.remove(repo);
                    addProjectRepo(repo, index - 1, false);
                }
            }
        }
    }

    private class MoveDownListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!repoList.isSelectionEmpty()) {
                int index = repoList.getSelectedIndex();
                String repo = listModel.getElementAt(index);
                listModel.remove(index);

                if (index < projectLocalRepos.size() - 1 && !projectLocalRepos.isEmpty()) {
                    projectLocalRepos.remove(repo);
                    addProjectRepo(repo, index + 1, true);
                }
                if( index == projectLocalRepos.size() - 1 && !projectLocalRepos.isEmpty()) {
                    projectLocalRepos.remove(repo);
                    addProjectRepo(repo, projectLocalRepos.size() + globalLookupRepos.size(), false);
                }
                if( index >= projectLocalRepos.size() + globalLookupRepos.size()
                        && index < projectLocalRepos.size() + globalLookupRepos.size() + projectRemoteRepos.size() - 1 ) {
                    projectRemoteRepos.remove(repo);
                    addProjectRepo(repo, index + 1, false);
                }
            }
        }
    }
}
