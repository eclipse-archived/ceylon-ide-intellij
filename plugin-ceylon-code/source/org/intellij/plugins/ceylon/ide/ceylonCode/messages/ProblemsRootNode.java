package org.intellij.plugins.ceylon.ide.ceylonCode.messages;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

class ProblemsRootNode extends BaseNode {

    private final SummaryNode summaryNode;

    protected ProblemsRootNode(Project project, Object model) {
        super(project);

        summaryNode = new SummaryNode(project, model);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        return Collections.singletonList(summaryNode);
    }

    @Override
    protected void update(PresentationData presentation) {

    }
}
