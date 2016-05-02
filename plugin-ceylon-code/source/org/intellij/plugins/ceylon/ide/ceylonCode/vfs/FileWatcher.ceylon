import com.intellij.openapi.fileEditor {
    FileEditorManagerListener,
    FileEditorManager,
    FileEditorManagerEvent,
    FileDocumentManager
}
import com.intellij.openapi.vfs {
    VirtualFileListener,
    VirtualFile,
    VirtualFilePropertyEvent,
    VirtualFileEvent,
    VirtualFileMoveEvent,
    VirtualFileCopyEvent
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject
}

shared class FileWatcher(IdeaCeylonProject project)
        satisfies VirtualFileListener & FileEditorManagerListener {

    shared actual void beforeContentsChange(VirtualFileEvent evt) {}

    shared actual void beforeFileDeletion(VirtualFileEvent evt) {}

    shared actual void beforeFileMovement(VirtualFileMoveEvent evt) {}

    shared actual void beforePropertyChange(VirtualFilePropertyEvent evt) {}

    shared actual void contentsChanged(VirtualFileEvent evt) {
        if (exists root = findSourceRoot(evt.file)) {
            value change = project.build.NativeFileContentChange(evt.file);
            project.projectFileTreeChanged({change});
        }
    }

    shared actual void fileClosed(FileEditorManager manager, VirtualFile file) {
        save(file);
    }

    fileCopied(VirtualFileCopyEvent evt) => fileCreated(evt);

    shared actual void fileCreated(VirtualFileEvent evt) {
        if (exists root = findSourceRoot(evt.file)) {
            value change = project.build.NativeFolderAddition(evt.file);
            project.projectFileTreeChanged({change});
        }
    }

    shared actual void fileDeleted(VirtualFileEvent evt) {
        if (exists root = findSourceRoot(evt.file)) {
            value change = project.build.NativeFileRemoval(evt.file, null);
            project.projectFileTreeChanged({change});
        }
    }

    shared actual void fileMoved(VirtualFileMoveEvent evt) {
        if (exists root = findSourceRoot(evt.file)) {
            // TODO we have the same "from" and "to" files, only their parent folder change
            value change = project.build.NativeFileRemoval(evt.file, evt.file);
            project.projectFileTreeChanged({change});
        }
    }

    shared actual void fileOpened(FileEditorManager manager, VirtualFile file) {}

    shared actual void propertyChanged(VirtualFilePropertyEvent evt) {}

    shared actual void selectionChanged(FileEditorManagerEvent evt) {
        if (exists oldFile = evt.oldFile) {
            save(oldFile);
        }
    }

    void save(VirtualFile file) {
        if (isCeylonSource(file),
            exists doc = FileDocumentManager.instance.getCachedDocument(file)) {

            // Will trigger contentsChanged(), which will call the typechecker
            FileDocumentManager.instance.saveDocument(doc);
        }
    }

    Boolean isCeylonSource(VirtualFile file) {
        return findSourceRoot(file) exists;
    }

    IdeaVirtualFolder? findSourceRoot(VirtualFile file) {
        if (exists ext = file.extension,
            ext == "ceylon",
            is IdeaVirtualFolder root = IdeaVirtualFolder(file.parent, project.ideArtifact).rootFolder) {

            return root;
        }

        return null;
    }
}
