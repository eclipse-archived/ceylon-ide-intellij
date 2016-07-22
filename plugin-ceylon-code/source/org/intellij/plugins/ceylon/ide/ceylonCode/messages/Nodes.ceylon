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
    Severity,
    CeylonProjectBuild
}

import java.util {
    ArrayList,
    Collections,
    Arrays
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

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

alias BuildMessage => CeylonProjectBuild<out Anything,out Anything,out Anything,out Anything>.BuildMessage;
alias SourceFileMessage => CeylonProjectBuild<out Anything,out Anything,out Anything,out Anything>.SourceFileMessage;

class ProblemsRootNode(Project project, ProblemsModel model)
        extends AbstractTreeNode<Object>(project, true) {

    value errorNode = SummaryNode(project, model, Severity.error);
    value warnNode = SummaryNode(project, model, Severity.warning);
    value infoNode = SummaryNode(project, model, Severity.info);

    children => Arrays.asList(errorNode, warnNode, infoNode);

    shared actual void update(PresentationData presentation) {}
}

class FileNode(Project project, VirtualFile file, Severity severity,
            {BuildMessage*} messages)
        extends AbstractTreeNode<String>(project, file.path) {

    shared actual value children {
        value nodes = ArrayList<ProblemNode>();
        for (message in messages) {
            if (message.severity == severity,
                is SourceFileMessage message,
                exists f = message.file, f == file) {
                nodes.add(ProblemNode(myProject, message));
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

class ProblemNode(Project project, BuildMessage message)
        extends AbstractTreeNode<BuildMessage>(project, message) {

    children => Collections.emptyList();

    shared actual void update(PresentationData presentation) {
        setSeverityIcon(message.severity, presentation);

        if (is SourceFileMessage message) {
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

    canNavigate() => message is SourceFileMessage;

    canNavigateToSource() => canNavigate();

    shared actual void navigate(Boolean requestFocus) {
        if (is SourceFileMessage message,
            is VirtualFile file = message.file) {
            OpenFileDescriptor(myProject, file, message.startLine-1, message.startCol)
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

    shared actual value children {
        value msgs = model.allMessages;
        value nodes = ArrayList<FileNode|ProblemNode>();
        value files = HashSet<VirtualFile>();
        for (BuildMessage message in msgs) {
            if (message.severity == severity) {
                if (is SourceFileMessage message,
                    is VirtualFile file = message.file) {
                    files.add(file);
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
