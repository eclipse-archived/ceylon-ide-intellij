package org.intellij.plugins.ceylon.ide.ceylonCode.messages;

import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.SimpleTextAttributes;
import com.redhat.ceylon.ide.common.model.CeylonProjectBuild;
import com.redhat.ceylon.ide.common.model.Severity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

class ProblemNode extends AbstractTreeNode<CeylonProjectBuild.BuildMessage> {

    ProblemNode(Project project, CeylonProjectBuild.BuildMessage message) {
        super(project, message);
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    protected void update(PresentationData presentation) {
        CeylonProjectBuild.BuildMessage message = getValue();

        if (message.getSeverity() == Severity.getSeverity$error()) {
            presentation.setIcon(AllIcons.General.Error);
        } else if (message.getSeverity() == Severity.getSeverity$warning()) {
            presentation.setIcon(AllIcons.General.Warning);
        } else if (message.getSeverity() == Severity.getSeverity$info()) {
            presentation.setIcon(AllIcons.General.Information);
        }

        if (message instanceof CeylonProjectBuild.SourceFileMessage) {
            CeylonProjectBuild.SourceFileMessage sourceMsg = (CeylonProjectBuild.SourceFileMessage) message;

            presentation.addText(String.format(
                    "%s(%d,%d): ",
                    ((VirtualFile)sourceMsg.getFile()).getName(),
                    sourceMsg.getStartLine(),
                    sourceMsg.getStartCol()
            ), SimpleTextAttributes.GRAYED_ATTRIBUTES);

            org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.highlighter_.get_()
                    .highlightPresentationData(presentation, sourceMsg.getMessage(), getProject());
        }
        presentation.setPresentableText(message.getMessage());
    }

    @Override
    public boolean canNavigate() {
        return getValue() instanceof CeylonProjectBuild.SourceFileMessage;
    }

    @Override
    public boolean canNavigateToSource() {
        return canNavigate();
    }

    @Override
    public void navigate(boolean requestFocus) {
        CeylonProjectBuild.SourceFileMessage msg = (CeylonProjectBuild.SourceFileMessage) getValue();

        new OpenFileDescriptor(
                myProject,
                (VirtualFile) msg.getFile(),
                (int) msg.getStartLine() - 1,
                (int) msg.getStartCol()
        ).navigate(requestFocus);
    }
}
