package org.intellij.plugins.ceylon.ide.ceylonCode.messages;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;

abstract class BaseNode extends AbstractTreeNode<Object> {

    BaseNode(Project project) {
        super(project, true);
    }

}
