import com.intellij.openapi.\imodule {
    ModuleManager,
    ModuleType,
    Mod=Module
}
import com.intellij.openapi.project {
    Project
}
import com.redhat.ceylon.model.typechecker.model {
    Module
}

import java.awt {
    Component
}
import java.awt.event {
    MouseAdapter,
    MouseEvent
}

import javax.swing {
    JTree
}
import javax.swing.tree {
    TreePath,
    DefaultMutableTreeNode,
    TreeModel,
    DefaultTreeModel,
    TreeSelectionModel,
    DefaultTreeCellRenderer
}

import org.intellij.plugins.ceylon.ide.facet {
    CeylonFacet
}
import org.intellij.plugins.ceylon.ide.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProjects,
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

class ModuleChooserDialog(Project project)
        extends AbstractModuleChooserDialog(project) {

    init();
    title = "Select a Ceylon Module";

    Module? getSelectedModule(TreePath? path) {
        if (exists path,
            is DefaultMutableTreeNode component = path.lastPathComponent,
            is Module obj = component.userObject) {
            return obj;
        }
        else {
            return null;
        }
    }

    shared Module? selectedModule => getSelectedModule(modulesTree.selectionPath);

    createCenterPanel() => contentPane;

    TreeModel createModel(Project project) {
        value root = DefaultMutableTreeNode();
        value model = DefaultTreeModel(root);
        value projects = project.getComponent(`IdeaCeylonProjects`);
        for (mod in ModuleManager.getInstance(project).modules) {
            value node = DefaultMutableTreeNode(mod, true);
            if (exists facet = CeylonFacet.forModule(mod)) {
                assert (is IdeaCeylonProject ceylonProject = projects.getProject(mod),
                        exists modules = ceylonProject.modules?.fromProject);
                for (i in 0:modules.size) {
                    node.add(DefaultMutableTreeNode(modules.getFromFirst(i)));
                }
            }
            root.add(node);
        }
        return model;
    }

    void expandAllNodes(JTree tree) {
        for (i in 0:tree.rowCount) {
            tree.expandRow(i);
        }
    }

    modulesTree.selectionModel.selectionMode = TreeSelectionModel.singleTreeSelection;
    modulesTree.model = createModel(project);
    expandAllNodes(modulesTree);

    modulesTree.setCellRenderer(object extends DefaultTreeCellRenderer() {
        shared actual Component getTreeCellRendererComponent(
                JTree tree, Object val, Boolean selected,
                Boolean expanded, Boolean leaf, Integer row, Boolean hasFocus) {
            value cmp = super.getTreeCellRendererComponent(tree, val, selected, expanded, leaf, row, hasFocus);
            if (is DefaultMutableTreeNode val) {
                switch (obj = val.userObject)
                case (is Mod) {
                    icon = ModuleType.get(obj).icon;
                }
                else case (is Module) {
                    icon = icons.moduleDescriptors;
                    text = "<html>``highlighter.highlight(text, project)``</html>";
                }
                else {}
            }
            return cmp;
        }
    });

    okActionEnabled = false;

    modulesTree.addTreeSelectionListener((e)
            => okActionEnabled = getSelectedModule(e.path) exists);

    modulesTree.addMouseListener(object extends MouseAdapter() {
        shared actual void mouseClicked(MouseEvent e) {
            if (e.clickCount == 2) {
                value selRow = modulesTree.getRowForLocation(e.x, e.y);
                if (selRow != -1) {
                    value selPath = modulesTree.getPathForLocation(e.x, e.y);
                    if (getSelectedModule(selPath) exists) {
                        doOKAction();
                    }
                }
            }
        }
    });
}
