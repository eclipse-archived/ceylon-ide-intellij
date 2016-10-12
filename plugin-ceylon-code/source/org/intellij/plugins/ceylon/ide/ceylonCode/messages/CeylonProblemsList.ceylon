import ceylon.interop.java {
    javaString
}

import com.intellij.ide.projectView {
    TreeStructureProvider
}
import com.intellij.ide.util.treeView {
    NodeRenderer,
    AbstractTreeBuilder,
    AbstractTreeStructureBase
}
import com.intellij.openapi.actionSystem {
    CommonDataKeys
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
import com.intellij.util {
    EditSourceOnDoubleClickHandler
}
import com.redhat.ceylon.ide.common.model {
    CeylonProjectBuild
}

import java.awt {
    BorderLayout
}
import java.util {
    Collections
}

import javax.swing {
    JScrollPane,
    JTree
}
import javax.swing.tree {
    DefaultTreeModel,
    DefaultMutableTreeNode
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject
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
        extends SimpleToolWindowPanel(false, true) {

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
    
    shared void setup() {
        this.layout = BorderLayout();
        add(JScrollPane(myTree), javaString(BorderLayout.center));
        myTree.rootVisible = false;

        value treeModel = DefaultTreeModel(DefaultMutableTreeNode());
        myTree.model = treeModel;
        myTree.cellRenderer = NodeRenderer();
        EditSourceOnDoubleClickHandler.install(myTree);

        builder = ProblemsTreeBuilder(myTree, treeModel, project);
        builder.initRootNode();

        model = ProblemsModel();
        builder.treeStructure = ProblemsTreeStructure(project, model);

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
        }
        return super.getData(dataId);
    }
}
