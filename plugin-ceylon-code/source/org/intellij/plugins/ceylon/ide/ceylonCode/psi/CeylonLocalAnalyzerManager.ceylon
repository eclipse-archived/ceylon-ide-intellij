import ceylon.interop.java {
    JavaRunnable
}

import com.intellij.openapi {
    Disposable
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.components {
    ProjectComponent
}
import com.intellij.openapi.fileEditor {
    FileEditorManagerListener {
        fileEditorManagerTopic=fileEditorManager
    },
    FileEditorManager {
        fileEditorManagerInstance=getInstance
    },
    FileEditorManagerEvent
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.startup {
    StartupManager
}
import com.intellij.openapi.util {
    Computable
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
import com.intellij.psi {
    PsiManager {
        psiManager=getInstance
    },
    FileViewProvider,
    SingleRootFileViewProvider
}
import com.intellij.util.messages {
    MessageBusConnection
}
import com.redhat.ceylon.ide.common.model {
    ModelListenerAdapter
}
import com.redhat.ceylon.ide.common.typechecker {
    ProjectPhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    ImmutableMapWrapper
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType,
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    concurrencyManager
}
shared class CeylonLocalAnalyzerManager(model) 
        satisfies Correspondence<VirtualFile, CeylonLocalAnalyzer>
        & ProjectComponent
        & Disposable
        & VirtualFileListener
        & FileEditorManagerListener
        & ModelListenerAdapter<Module, VirtualFile, VirtualFile, VirtualFile> {
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
        model.removeModelListener(this);
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
        model.addModelListener(this);
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
                scheduleReparse(virtualFile);
            }
        }
    }
    
    CeylonLocalAnalyzer newCeylonLocalAnalyzer(VirtualFile virtualFile) {
        value ceylonLocalAnalyzer = CeylonLocalAnalyzer(virtualFile, model.ideaProject);
        ceylonLocalAnalyzer.initialize(this);
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

    shared actual void modelPhasedUnitsTypechecked({ProjectPhasedUnit<Module, VirtualFile, VirtualFile, VirtualFile>*} typecheckedUnits) {
        value ceylonEditedFiles = concurrencyManager.needReadAccess(() =>
            fileEditorManagerInstance(model.ideaProject)
                .openFiles.array.coalesced).sequence();

        for (unit in typecheckedUnits) {
            value virtualFile = unit.unitFile.nativeResource;
            if (virtualFile in ceylonEditedFiles) {
                if (is CeylonFile ceylonFile = concurrencyManager.needReadAccess(()
                    => psiManager(model.ideaProject).findFile(virtualFile)),
                    exists localAnalyzer = ceylonFile.localAnalyzer) {
                    localAnalyzer.scheduleForcedTypechecking();
                }
            } else {
                scheduleReparse(virtualFile);
            }
        }
        
        
    }

    shared void scheduleReparse(VirtualFile virtualFile) {
        if (is SingleRootFileViewProvider fileViewProvider =
            ApplicationManager.application.runReadAction(object satisfies Computable<FileViewProvider> { compute()
            => PsiManager.getInstance(model.ideaProject).findViewProvider(virtualFile);})) {
            ApplicationManager.application.invokeLater(JavaRunnable(() {
                ApplicationManager.application.runWriteAction(JavaRunnable(() {
                    fileViewProvider.onContentReload();
                }));
                ApplicationManager.application.runReadAction(JavaRunnable(() {
                    if (exists cachedPsi = fileViewProvider.getCachedPsi(CeylonLanguage.instance)) {
                        suppressWarnings("unusedDeclaration")
                        value unused = cachedPsi.node.lastChildNode;
                    }
                }));
            })); 
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