package org.intellij.plugins.ceylon.ide.runner;

import ceylon.language.Iterable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.intellij.plugins.ceylon.ide.highlighting.highlighter_;
import org.intellij.plugins.ceylon.ide.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.util.icons_;
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

    ModuleChooserDialog(final Project project) {
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
                        setText("<html>" + highlighter_.get_().highlight(getText(), project) + "</html>");
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

    private void expandAllNodes(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); ++i) {
            tree.expandRow(i);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        contentPane.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        modulesTree = new JTree();
        modulesTree.setRootVisible(false);
        scrollPane1.setViewportView(modulesTree);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
