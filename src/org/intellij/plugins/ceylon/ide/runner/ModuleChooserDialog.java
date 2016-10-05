package org.intellij.plugins.ceylon.ide.runner;

import ceylon.language.Iterable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

public class ModuleChooserDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTree modulesTree;

    ModuleChooserDialog(Project project) {
        super(project, false);
        init();
        setTitle("Select a Ceylon Module");

        modulesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        modulesTree.setModel(createModel(project));
        expandAllNodes(modulesTree);

        modulesTree.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                          boolean selected, boolean expanded,
                                                          boolean leaf, int row, boolean hasFocus) {
                Component cmp = super.getTreeCellRendererComponent(tree, value, selected,
                        expanded, leaf, row, hasFocus);

                if (value instanceof DefaultMutableTreeNode) {
                    Object object = ((DefaultMutableTreeNode) value).getUserObject();
                    if (object instanceof Module) {
                        setIcon(ModuleType.get((Module) object).getIcon());
                    } else if (object instanceof com.redhat.ceylon.model.typechecker.model.Module) {
                        setIcon(icons_.get_().getModuleDescriptors());
                    }
                }
                return cmp;
            }
        });

        setOKActionEnabled(false);
        modulesTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                setOKActionEnabled(getSelectedModule(e.getPath()) != null);
            }
        });
        modulesTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selRow = modulesTree.getRowForLocation(e.getX(), e.getY());
                    if (selRow != -1) {
                        TreePath selPath = modulesTree.getPathForLocation(e.getX(), e.getY());
                        if (getSelectedModule(selPath) != null) {
                            doOKAction();
                        }
                    }
                }
            }
        });
    }

    @Nullable
    private com.redhat.ceylon.model.typechecker.model.Module getSelectedModule(TreePath path) {
        if (path != null) {
            Object component = path.getLastPathComponent();
            if (component instanceof DefaultMutableTreeNode) {
                Object obj = ((DefaultMutableTreeNode) component).getUserObject();
                if (obj instanceof com.redhat.ceylon.model.typechecker.model.Module) {
                    return (com.redhat.ceylon.model.typechecker.model.Module) obj;
                }
            }
        }
        return null;
    }

    @Nullable
    com.redhat.ceylon.model.typechecker.model.Module getSelectedModule() {
        return getSelectedModule(modulesTree.getSelectionPath());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    private TreeModel createModel(Project project) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(root);
        IdeaCeylonProjects projects = project.getComponent(IdeaCeylonProjects.class);

        for (Module module : ModuleManager.getInstance(project).getModules()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(module, true);
            CeylonFacet facet = CeylonFacet.forModule(module);
            if (facet != null) {
                IdeaCeylonProject ceylonProject = (IdeaCeylonProject) projects.getProject(module);
                Iterable modules = ceylonProject.getModules().getFromProject();

                for (int i = 0; i < modules.getSize(); i++) {
                    node.add(new DefaultMutableTreeNode(modules.getFromFirst(i)));
                }
            }
            root.add(node);
        }
        return model;
    }

    private void expandAllNodes(JTree tree){
        for(int i=0;i<tree.getRowCount();++i){
            tree.expandRow(i);
        }
    }
}
