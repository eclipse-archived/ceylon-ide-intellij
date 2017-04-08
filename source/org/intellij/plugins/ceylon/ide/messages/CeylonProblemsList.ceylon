import java.lang {
    Types {
        nativeString
    }
}

import com.intellij.diff {
    DiffContentFactory {
        diffContentFactory=instance
    },
    DiffManager {
        diffManager=instance
    }
}
import com.intellij.diff.requests {
    SimpleDiffRequest
}
import com.intellij.icons {
    AllIcons
}
import com.intellij.ide.projectView {
    TreeStructureProvider
}
import com.intellij.ide.util.treeView {
    NodeRenderer,
    AbstractTreeBuilder,
    AbstractTreeStructureBase
}
import com.intellij.openapi {
    Disposable
}
import com.intellij.openapi.actionSystem {
    CommonDataKeys,
    ActionManager,
    IdeActions,
    DefaultActionGroup,
    ActionPlaces,
    PlatformDataKeys,
    AnAction,
    AnActionEvent,
    CommonShortcuts
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.ui {
    SimpleToolWindowPanel
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.ui {
    PopupHandler,
    TreeCopyProvider
}
import com.intellij.util {
    EditSourceOnDoubleClickHandler
}
import com.redhat.ceylon.ide.common.model {
    CeylonProjectBuild
}

import java.awt {
    BorderLayout,
    Component
}
import java.awt.event {
    FocusEvent,
    FocusAdapter
}
import java.util {
    Collections
}

import javax.swing {
    JScrollPane,
    JTree,
    JPanel
}
import javax.swing.tree {
    DefaultTreeModel,
    DefaultMutableTreeNode
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonFileType
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject,
    getCeylonProjects,
    getModelManager
}

shared alias BuildMsg => CeylonProjectBuild<Module,VirtualFile,VirtualFile,VirtualFile>.BuildMessage;
shared alias SourceMsg => CeylonProjectBuild<Module,VirtualFile,VirtualFile,VirtualFile>.SourceFileMessage;
shared alias ProjectMsg => CeylonProjectBuild<Module,VirtualFile,VirtualFile,VirtualFile>.ProjectMessage;

class ProblemsTreeBuilder(JTree tree, DefaultTreeModel model, Project project)
        extends AbstractTreeBuilder(tree, model, null, null, false) {}

class ProblemsTreeStructure(Project project, ProblemsModel model)
        extends AbstractTreeStructureBase(project) {

    value rootNode = ProblemsRootNode(project, model);

    commit() => noop();

    hasSomethingToCommit() => false;

    providers => Collections.emptyList<TreeStructureProvider>();

    rootElement => rootNode;
}

class CeylonProblemsList(Project project)
        extends SimpleToolWindowPanel(false, true)
        satisfies Disposable {

    value myTree = JTree();
    late ProblemsModel model;
    late ProblemsTreeBuilder builder;

    shared actual variable String name = "";
    
    shared void expand() {
        variable value i = 0;
        while (i<myTree.rowCount) {
            myTree.expandRow(i++);
        }
    }

    object diffAction extends AnAction() {
        function findSelectedMessage() {
            return if (is DefaultMutableTreeNode node = myTree.selectionPath.lastPathComponent,
                is ProblemNode problem = node.userObject,
                is SourceFileMessage message = problem.message)
            then message
            else null;
        }

        shared actual void update(AnActionEvent e) {
            e.presentation.visible = false;
            if (exists message = findSelectedMessage(),
                message.typecheckerMessage.code == 2100) {

                e.presentation.visible = true;
                e.presentation.text = "Compare parameter lists";
                e.presentation.setIcon(AllIcons.Actions.diff);
            }
        }

        shared actual void actionPerformed(AnActionEvent e) {
            if (exists message = findSelectedMessage(),
                exists firstColon = message.message.firstInclusion(": "),
                exists isNotAssignableTo = message.message.firstInclusion(" is not assignable to "),
                firstColon < isNotAssignableTo) {

                value expected = diffContentFactory.create(
                    message.message[firstColon + 2..isNotAssignableTo - 1].trim('\''.equals),
                    CeylonFileType.instance
                );
                value actual = diffContentFactory.create(
                    message.message[isNotAssignableTo + 22...].trim('\''.equals),
                    CeylonFileType.instance
                );
                diffManager.showDiff(
                    project,
                    SimpleDiffRequest(
                        "Compare parameter lists",
                        expected, actual,
                        "Expected", "Actual"
                    )
                );
            }
        }
    }

    object mouseListener extends PopupHandler() {
        shared actual void invokePopup(Component comp, Integer x, Integer y) {
            value group = DefaultActionGroup();

            group.add(ActionManager.instance.getAction(IdeActions.actionCopy));
            group.add(diffAction);

            ActionManager.instance.createActionPopupMenu(ActionPlaces.unknown, group)
                .component.show(comp, x, y);
        }
    }

    function createActionsToolbar() {
        value group = DefaultActionGroup();

        group.add(object extends AnAction("Refresh", "Refresh messages", AllIcons.Actions.refresh) {
            actionPerformed(AnActionEvent? e) => updateModel();
        });
        group.add(object extends AnAction("Reset", "Reset Ceylon model", AllIcons.Actions.restart) {
            shared actual void actionPerformed(AnActionEvent? e) {
                if (exists mm = getModelManager(project),
                    exists projects = getCeylonProjects(project)) {

                    for (p in projects.ceylonProjects) {
                        p.build.requestFullBuild();
                        p.build.classPathChanged();
                    }

                    mm.scheduleModelUpdate(0, true);
                }
            }
        });

        value actionToolbar = ActionManager.instance.createActionToolbar(ActionPlaces.usageViewToolbar, group, false);
        return actionToolbar.component;
    }

    shared void setup() {
        this.layout = BorderLayout();
        add(JScrollPane(myTree), nativeString(BorderLayout.center));
        myTree.rootVisible = false;

        value treeModel = DefaultTreeModel(DefaultMutableTreeNode());
        myTree.model = treeModel;
        myTree.cellRenderer = NodeRenderer();
        myTree.addMouseListener(mouseListener);
        EditSourceOnDoubleClickHandler.install(myTree);
        diffAction.registerCustomShortcutSet(CommonShortcuts.diff, myTree);

        builder = ProblemsTreeBuilder(myTree, treeModel, project);
        builder.initRootNode();

        model = ProblemsModel();
        builder.treeStructure = ProblemsTreeStructure(project, model);

        value toolbar = JPanel(BorderLayout());
        toolbar.add(createActionsToolbar(), nativeString(BorderLayout.west));
        setToolbar(toolbar);

        myTree.addFocusListener(object extends FocusAdapter() {
            focusGained(FocusEvent? e) => updateModel();
        });
    }

    shared void updateErrors(IdeaCeylonProject project, {SourceMsg*}? frontendMessages,
        {SourceMsg*}? backendMessages, {ProjectMsg*}? projectMessages) {

        model.updateProblems(project, frontendMessages, backendMessages, projectMessages);
        builder.queueUpdate().doWhenDone(expand);
    }

    shared actual Object? getData(String dataId) {
        if (CommonDataKeys.navigatable.\iis(dataId),
            exists path = myTree.selectionPath,
            is DefaultMutableTreeNode node = path.lastPathComponent,
            is ProblemNode userObject = node.userObject) {

            return userObject;
        } else if (PlatformDataKeys.copyProvider.\iis(dataId)) {
            return TreeCopyProvider(myTree);
        }
        return super.getData(dataId);
    }

    shared actual void dispose() {
        model.clear();
    }

    shared void updateModel() {
        if (exists mm = getModelManager(project)) {
            mm.scheduleModelUpdate(0, true);
        }
    }
}
