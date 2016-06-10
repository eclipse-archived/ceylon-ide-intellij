package org.intellij.plugins.ceylon.ide.ceylonCode.messages;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.redhat.ceylon.ide.common.model.Severity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

class ProblemsRootNode extends AbstractTreeNode<Object> {

    private final SummaryNode errorNode;
    private final SummaryNode warnNode;
    private final SummaryNode infoNode;

    protected ProblemsRootNode(Project project, Object model) {
        super(project, true);

        errorNode = new SummaryNode(project, model, Severity.getSeverity$error());
        warnNode = new SummaryNode(project, model, Severity.getSeverity$warning());
        infoNode = new SummaryNode(project, model, Severity.getSeverity$info());
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        return Arrays.asList(errorNode, warnNode, infoNode);
    }

    @Override
    protected void update(PresentationData presentation) {

    }
}
