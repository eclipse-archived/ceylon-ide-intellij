import ceylon.interop.java {
    javaString
}

import com.intellij.ide.util.treeView {
    NodeRenderer
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
import java.lang {
    Runnable
}

shared alias BuildMsg => CeylonProjectBuild<Module,VirtualFile,VirtualFile,VirtualFile>.BuildMessage;
shared alias SourceMsg => CeylonProjectBuild<Module,VirtualFile,VirtualFile,VirtualFile>.SourceFileMessage;
shared alias ProjectMsg => CeylonProjectBuild<Module,VirtualFile,VirtualFile,VirtualFile>.ProjectMessage;

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
        builder.queueUpdate().doWhenDone(object satisfies Runnable { run = expand; });
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
