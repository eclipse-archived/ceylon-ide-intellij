import ceylon.interop.java {
    JavaRunnable
}

import com.intellij.openapi {
    Disposable
}
import com.intellij.openapi.components {
    ProjectComponent
}
import com.intellij.openapi.fileEditor {
    FileEditorManagerListener {
        fileEditorManagerTopic=fileEditorManager
    },
    FileEditorManager,
    FileEditorManagerEvent
}
import com.intellij.openapi.startup {
    StartupManager
}
import com.intellij.openapi.vfs {
    VirtualFileListener,
    VirtualFile,
    VirtualFileEvent,
    VirtualFileMoveEvent,
    VirtualFilePropertyEvent,
    VirtualFileCopyEvent,
    VirtualFileManager
}
import com.intellij.util.messages {
    MessageBusConnection
}
import com.redhat.ceylon.ide.common.util {
    ImmutableMapWrapper
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}
class CeylonLocalAnalyzerManager(model) 
        satisfies Correspondence<VirtualFile, CeylonLocalAnalyzer>
        & ProjectComponent
        & Disposable
        & VirtualFileListener
        & FileEditorManagerListener {
    shared IdeaCeylonProjects model;    
    value mutableMap = ImmutableMapWrapper<VirtualFile, CeylonLocalAnalyzer>(emptyMap, map);
    
    late MessageBusConnection busConnection;

    shared Map<VirtualFile, CeylonLocalAnalyzer> analyzers => mutableMap.immutable;

    defines(VirtualFile key) => analyzers.defines(key);
    get(VirtualFile key) => analyzers[key];

    shared actual String componentName => "CeylonLocalAnalyzerManager";
    
    shared actual void dispose() {
        mutableMap.clear().items.each((localAnalyzer) {
           localAnalyzer.dispose(); 
        });
    }
    
    shared actual void disposeComponent() {
        dispose();
        busConnection.disconnect();
        VirtualFileManager.instance.removeVirtualFileListener(this);
    }
    
    shared actual void projectOpened() {
        StartupManager.getInstance(model.ideaProject)
                .runWhenProjectIsInitialized(JavaRunnable(() {
            value openedCeylonFiles = FileEditorManager.getInstance(model.ideaProject).openFiles.array.coalesced
                    .filter((vf) => vf.fileType == CeylonFileType.instance);
            mutableMap.putAllKeys(openedCeylonFiles, newCeylonLocalAnalyzer, true);
        }));
    }

    shared actual void initComponent() {
        busConnection = model.ideaProject.messageBus.connect();
        busConnection.subscribe(fileEditorManagerTopic, this);
        VirtualFileManager.instance.addVirtualFileListener(this);
    }
    
    shared actual void projectClosed() {
        mutableMap.clear().items.each((analyzer) => 
            analyzer.dispose());
    }
    
    shared actual void fileClosed(FileEditorManager fileEditorManager, VirtualFile virtualFile) {
        if (!fileEditorManager.isFileOpen(virtualFile)) {
            value analyzer = mutableMap.remove(virtualFile);
            if (exists analyzer) {
                analyzer.dispose();
                analyzer.scheduleReparse(virtualFile);
            }
        }
    }
    
    CeylonLocalAnalyzer newCeylonLocalAnalyzer(VirtualFile virtualFile) {
        value ceylonLocalAnalyzer = CeylonLocalAnalyzer(virtualFile, model.ideaProject);
        ceylonLocalAnalyzer.initialize();
        return ceylonLocalAnalyzer;
    }
    
    shared actual void fileOpened(FileEditorManager fileEditorManager, VirtualFile virtualFile) {
        if (virtualFile.fileType == CeylonFileType.instance) {
            mutableMap.putIfAbsent(virtualFile, () => newCeylonLocalAnalyzer(virtualFile));
        }
    }
    
    shared actual void fileDeleted(VirtualFileEvent virtualFileEvent) {
        mutableMap.remove(virtualFileEvent.file)?.dispose();
    }

    shared actual void contentsChanged(VirtualFileEvent virtualFileEvent) {
        value virtualFile = virtualFileEvent.file;
        if (exists analyzer = analyzers[virtualFile],
            isInSourceArchive(virtualFile)) {
            mutableMap.put(virtualFile, newCeylonLocalAnalyzer(virtualFile));
        }
    }

    selectionChanged(FileEditorManagerEvent? fileEditorManagerEvent) => noop();
    beforeContentsChange(VirtualFileEvent? virtualFileEvent) => noop();
    beforeFileDeletion(VirtualFileEvent? virtualFileEvent) => noop();
    beforeFileMovement(VirtualFileMoveEvent? virtualFileMoveEvent) => noop();
    beforePropertyChange(VirtualFilePropertyEvent? virtualFilePropertyEvent) => noop();
    fileCopied(VirtualFileCopyEvent? virtualFileCopyEvent) => noop();
    fileCreated(VirtualFileEvent? virtualFileEvent) => noop();
    fileMoved(VirtualFileMoveEvent? virtualFileMoveEvent) => noop();
    propertyChanged(VirtualFilePropertyEvent? virtualFilePropertyEvent) => noop();
}