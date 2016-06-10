package org.intellij.plugins.ceylon.ide.ceylonCode.messages;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.ceylon.ide.common.model.CeylonProjectBuild;
import com.redhat.ceylon.ide.common.model.Severity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class SummaryNode extends AbstractTreeNode<Severity> {

    private final Object model;
    private final Severity severity;

    SummaryNode(Project project, Object model, Severity severity) {
        super(project, severity);
        this.model = model;
        this.severity = severity;
    }

    @Override
    protected void update(PresentationData presentation) {
        ProblemsModel problemsModel = (ProblemsModel) model;

        String desc = "problems";
        if (severity == Severity.getSeverity$error()) {
            desc = "errors";
        } else if (severity == Severity.getSeverity$warning()) {
            desc = "warnings";
        } else if (severity == Severity.getSeverity$info()) {
            desc = "messages";
        }
        presentation.setPresentableText(String.format(
                "%d %s in project",
                problemsModel.count(severity), desc));

        if (severity == Severity.getSeverity$error()) {
            presentation.setIcon(AllIcons.General.Error);
        } else if (severity == Severity.getSeverity$warning()) {
            presentation.setIcon(AllIcons.General.Warning);
        } else if (severity == Severity.getSeverity$info()) {
            presentation.setIcon(AllIcons.General.Information);
        }
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        ProblemsModel problemsModel = (ProblemsModel) model;
        Iterable msgs = problemsModel.getAllMessages();
        List<AbstractTreeNode> nodes = new ArrayList<>();
        Set<VirtualFile> files = new HashSet<VirtualFile>();
        for (Object obj : msgs) {
            if (obj instanceof CeylonProjectBuild.BuildMessage) {
                CeylonProjectBuild.BuildMessage message =
                        (CeylonProjectBuild.BuildMessage) obj;
                if (message.getSeverity()==severity) {
                    if (message instanceof CeylonProjectBuild.SourceFileMessage) {
                        CeylonProjectBuild.SourceFileMessage sourceFileMessage =
                                (CeylonProjectBuild.SourceFileMessage) message;
                        files.add((VirtualFile) sourceFileMessage.getFile());
                    } else {
                        nodes.add(new ProblemNode(myProject, message));
                    }
                }
            }
        }
        for (VirtualFile file: files) {
            nodes.add(new FileNode(myProject, file, severity, msgs));
        }

        return nodes;
    }
}
