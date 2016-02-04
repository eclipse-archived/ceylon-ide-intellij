import com.intellij.openapi.vfs {
    VirtualFileListener,
    VirtualFile,
    VirtualFilePropertyEvent,
    VirtualFileEvent,
    VirtualFileMoveEvent,
    VirtualFileCopyEvent
}
import com.intellij.openapi.fileEditor {
    FileEditorManagerListener,
    FileEditorManager,
    FileEditorManagerEvent,
    FileDocumentManager
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
        if (isCeylonSource(evt.file)) {
            // TODO typecheck
            print("File changed: ``evt.file.name``");
        }
    }
    
    shared actual void fileClosed(FileEditorManager manager, VirtualFile file) {
        if (isCeylonSource(file),
            exists doc = FileDocumentManager.instance.getCachedDocument(file)) {
            
            // Will trigger contentsChanged(), which will call the typechecker
            FileDocumentManager.instance.saveDocument(doc);
        }
    }
    
    shared actual void fileCopied(VirtualFileCopyEvent evt) {}
    
    shared actual void fileCreated(VirtualFileEvent evt) {}
    
    shared actual void fileDeleted(VirtualFileEvent evt) {}
    
    shared actual void fileMoved(VirtualFileMoveEvent evt) {}
    
    shared actual void fileOpened(FileEditorManager manager, VirtualFile file) {}
    
    shared actual void propertyChanged(VirtualFilePropertyEvent evt) {}
    
    shared actual void selectionChanged(FileEditorManagerEvent evt) {
        if (isCeylonSource(evt.oldFile),
            exists doc = FileDocumentManager.instance.getCachedDocument(evt.oldFile)) {

            // Will trigger contentsChanged(), which will call the typechecker
            FileDocumentManager.instance.saveDocument(doc);
        }
    }
    
    Boolean isCeylonSource(VirtualFile file) {
        if (exists ext = file.extension,
            ext == "ceylon",
            exists root = IdeaVirtualFolder(file.parent, project.ideArtifact).rootFolder) {

            return true;
        }
        
        return false;
    }
}
