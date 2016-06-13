package org.intellij.plugins.ceylon.ide.ceylonCode.messages;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.SimpleTextAttributes;
import com.redhat.ceylon.ide.common.model.CeylonProjectBuild;
import com.redhat.ceylon.ide.common.model.Severity;
import org.jetbrains.annotations.NotNull;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class FileNode extends AbstractTreeNode<Object> {

    private final Severity severity;
    private VirtualFile file;
    private Iterable messages;

    FileNode(Project project, VirtualFile file, Severity severity, Iterable messages) {
        super(project, file.getPath());
        this.file = file;
        this.severity = severity;
        this.messages = messages;
    }

    @NotNull
    @Override
    public Collection<? extends AbstractTreeNode> getChildren() {
        List<AbstractTreeNode> nodes = new ArrayList<>();
        for (Object message: messages) {
            if (message instanceof CeylonProjectBuild.SourceFileMessage) {
                CeylonProjectBuild.SourceFileMessage sourceFileMessage =
                        (CeylonProjectBuild.SourceFileMessage) message;
                if (sourceFileMessage.getSeverity() == severity
                        && sourceFileMessage.getFile().equals(file)) {
                    nodes.add(new ProblemNode(myProject, sourceFileMessage));
                }
            }
        }
        return nodes;
    }

    @Override
    protected void update(PresentationData presentation) {
        presentation.setIcon(icons_.get_().getFile());
        presentation.addText(file.getPresentableName(),
                SimpleTextAttributes.REGULAR_ATTRIBUTES);
        int size = getChildren().size();
        if (size>1) {
            presentation.addText(String.format(" %d %s",
                    size,
                    severity == Severity.getSeverity$warning() ?
                            "warnings" : "errors"),
                    SimpleTextAttributes.GRAYED_ATTRIBUTES);
        }
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    @Override
    public void navigate(boolean requestFocus) {
        new OpenFileDescriptor(
                myProject,
                file,
                //TODO: go to first error!
                0, 0
        ).navigate(requestFocus);
    }
}
