package org.intellij.plugins.ceylon.ide.ceylonCode.messages;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.redhat.ceylon.ide.common.model.CeylonProjectBuild;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class SummaryNode extends BaseNode {

    private final Object model;

    SummaryNode(Project project, Object model) {
        super(project);
        this.model = model;
    }

    @Override
    protected void update(PresentationData presentation) {
        ProblemsModel problemsModel = (ProblemsModel) model;
        long warnings = problemsModel.countWarnings();
        long errors = problemsModel.countErrors();

        presentation.setPresentableText(String.format(
                "%d errors and %d warnings found in project",
                errors, warnings
        ));
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        ProblemsModel problemsModel = (ProblemsModel) model;
        Iterable msgs = problemsModel.getAllMessages();

        List<ProblemNode> nodes = new ArrayList<>();

        for (Object obj : msgs) {
            if (obj instanceof CeylonProjectBuild.BuildMessage) {
                nodes.add(new ProblemNode(myProject, (CeylonProjectBuild.BuildMessage) obj));
            }
        }

        return nodes;
    }
}
