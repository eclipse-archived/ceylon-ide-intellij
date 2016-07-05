import ceylon.collection {
    HashSet
}

import com.intellij.icons {
    AllIcons
}
import com.intellij.ide.projectView {
    PresentationData
}
import com.intellij.ide.util.treeView {
    AbstractTreeNode
}
import com.intellij.openapi.fileEditor {
    OpenFileDescriptor
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.ui {
    SimpleTextAttributes
}
import com.redhat.ceylon.ide.common.model {
    CeylonProjectBuild,
    Severity
}

import java.util {
    ArrayList,
    Collection,
    Collections,
    Arrays
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

alias Build => CeylonProjectBuild<Module,VirtualFile,VirtualFile,VirtualFile>;

String severityString(Severity severity)
        => switch (severity)
        case (Severity.info) "messages"
        case (Severity.warning) "warnings"
        case (Severity.error) "errors";

void setSeverityIcon(Severity severity, PresentationData presentation) {
    switch (severity)
    case (Severity.error) {
        presentation.setIcon(AllIcons.General.error);
    } case (Severity.warning) {
        presentation.setIcon(AllIcons.General.warning);
    } case (Severity.info) {
        presentation.setIcon(AllIcons.General.information);
    }
}

class ProblemsRootNode(Project project, ProblemsModel model)
        extends AbstractTreeNode<Object>(project, true) {

    value errorNode = SummaryNode(project, model, Severity.error);
    value warnNode = SummaryNode(project, model, Severity.warning);
    value infoNode = SummaryNode(project, model, Severity.info);

    shared actual Collection<out AbstractTreeNode<out Object>> children
            => Arrays.asList(errorNode, warnNode, infoNode);

    shared actual void update(PresentationData presentation) {}
}

class FileNode(Project project, VirtualFile file, Severity severity,
            {BuildMsg*} messages)
        extends AbstractTreeNode<Object>(project, file.path) {

    shared actual Collection<out AbstractTreeNode<out Object>> children {
        value nodes = ArrayList<AbstractTreeNode<out Object>>();
        for (message in messages) {
            if (is Build.SourceFileMessage message) {
                if (message.severity == severity,
                    message.file == file) {
                    nodes.add(ProblemNode(myProject, message));
                }
            }
        }
        return nodes;
    }

    shared actual void update(PresentationData presentation) {
        presentation.setIcon(icons.file);
        presentation.addText(file.presentableName,
            SimpleTextAttributes.regularAttributes);
        value size = children.size();
        if (size>1) {
            presentation.addText(
                " ``size`` ``severityString(severity)``",
                SimpleTextAttributes.grayedAttributes);
        }
    }

    canNavigate() => true;

    canNavigateToSource() => true;

    navigate(Boolean requestFocus)
            => OpenFileDescriptor(myProject, file, 0, 0)
                .navigate(requestFocus);

}

class ProblemNode(Project project, Build.BuildMessage message)
        extends AbstractTreeNode<Build.BuildMessage>(project, message) {

    shared actual Collection<out AbstractTreeNode<out Object>> children
            => Collections.emptyList();

    shared actual void update(PresentationData presentation) {
        setSeverityIcon(message.severity, presentation);

        if (is Build.SourceFileMessage message) {
            presentation.addText(message.startLine.string + " ",
                    SimpleTextAttributes.grayedAttributes);
            highlighter.highlightPresentationData {
                data = presentation;
                description = message.message;
                project = project;
            };
        }
        presentation.presentableText = message.message;
    }

    canNavigate() => message is Build.SourceFileMessage;

    canNavigateToSource() => canNavigate();

    shared actual void navigate(Boolean requestFocus) {
        if (is Build.SourceFileMessage message) {
            OpenFileDescriptor(myProject, message.file, message.startLine-1, message.startCol)
                .navigate(requestFocus);
        }
    }
}

class SummaryNode(Project project, ProblemsModel model, Severity severity)
        extends AbstractTreeNode<Severity>(project, severity) {

    shared actual void update(PresentationData presentation) {
        presentation.presentableText
            = "``model.count(severity)`` ``severityString(severity)`` in project";
        setSeverityIcon(severity, presentation);
    }

    shared actual Collection<out AbstractTreeNode<out Object>> children {
        value msgs = model.allMessages;
        value nodes = ArrayList<AbstractTreeNode<out Object>>();
        value files = HashSet<VirtualFile>();
        for (message in msgs) {
            if (message.severity == severity) {
                if (is Build.SourceFileMessage message) {
                    files.add(message.file);
                } else {
                    nodes.add(ProblemNode(myProject, message));
                }
            }
        }
        for (file in files) {
            nodes.add(FileNode {
                project = myProject;
                file = file;
                severity = severity;
                messages = msgs;
            });
        }
        return nodes;
    }
}
