import ceylon.interop.java {
    JavaRunnable
}

import com.intellij.openapi {
    Disposable
}
import com.intellij.openapi.application {
    ApplicationManager {
        application
    },
    ModalityState
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
    FileEditorManagerEvent,
    FileDocumentManager {
        fileDocumentManager = instance
    }
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.startup {
    StartupManager {
        startupManager = getInstance
    }
}
import com.intellij.openapi.util {
    Computable,
    Key
}
import com.intellij.openapi.vfs {
    VirtualFileListener,
    VirtualFile,
    VirtualFileEvent,
    VirtualFileMoveEvent,
    VirtualFilePropertyEvent,
    VirtualFileCopyEvent,
    VirtualFileManager {
        virtualFileManager = instance
    }
}
import com.intellij.psi {
    PsiManager {
        psiManager=getInstance
    },
    FileViewProvider,
    SingleRootFileViewProvider,
    PsiDocumentManager {
        psiDocumentManager = getInstance
    }
}
import com.intellij.util.messages {
    MessageBusConnection
}
import com.redhat.ceylon.ide.common.model {
    ModelListenerAdapter,
    CeylonProject
}
import com.redhat.ceylon.ide.common.typechecker {
    ProjectPhasedUnit,
    ExternalPhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    ImmutableMapWrapper
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType {
        ceylonFileType = instance
    },
    CeylonLanguage {
        ceylonLanguage = instance
    }
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    concurrencyManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    CeylonLogger
}
import com.intellij.openapi.progress {
    ProgressManager
}
import java.lang.ref {
    WeakReference
}

CeylonLogger<CeylonLocalAnalyzerManager> ceylonLocalAnalyzerManagerLogger = CeylonLogger<CeylonLocalAnalyzerManager>();

shared Key<WeakReference<ExternalPhasedUnit>> uneditedExternalPhasedUnit = Key<WeakReference<ExternalPhasedUnit>>("CeylonPlugin.nativeFile_uneditedExternalPhasedUnit");

shared class CeylonLocalAnalyzerManager(model) 
        satisfies Correspondence<VirtualFile, CeylonLocalAnalyzer>
        & ProjectComponent
        & Disposable
        & VirtualFileListener
        & FileEditorManagerListener
        & ModelListenerAdapter<Module, VirtualFile, VirtualFile, VirtualFile> {
    shared IdeaCeylonProjects model;    
    value editedFilesAnalyzersMap = ImmutableMapWrapper<VirtualFile, CeylonLocalAnalyzer>(emptyMap, map);
    
    late MessageBusConnection busConnection;

    value logger => ceylonLocalAnalyzerManagerLogger;
            
    shared Map<VirtualFile, CeylonLocalAnalyzer> editedFilesAnalyzers => editedFilesAnalyzersMap.immutable;

    defines(VirtualFile key) => editedFilesAnalyzers.defines(key);
    get(VirtualFile key) => editedFilesAnalyzers[key];

    shared actual String componentName => "CeylonLocalAnalyzerManager";
    
    shared actual void dispose() {
        editedFilesAnalyzersMap.clear().items.each((localAnalyzer) {
           localAnalyzer.dispose(); 
        });
    }
    
    shared actual void disposeComponent() {
        dispose();
        busConnection.disconnect();
        virtualFileManager.removeVirtualFileListener(this);
        model.removeModelListener(this);
    }
    
    shared actual void projectOpened() {
        startupManager(model.ideaProject)
                .runWhenProjectIsInitialized(JavaRunnable(() {
            value openedCeylonFiles = fileEditorManagerInstance(model.ideaProject).openFiles.array.coalesced
                    .filter((vf) => vf.fileType == ceylonFileType);
            editedFilesAnalyzersMap.putAllKeys(openedCeylonFiles, newCeylonLocalAnalyzer, true);
        }));
    }

    shared actual void initComponent() {
        busConnection = model.ideaProject.messageBus.connect();
        busConnection.subscribe(fileEditorManagerTopic, this);
        virtualFileManager.addVirtualFileListener(this);
        model.addModelListener(this);
    }
    
    shared actual void projectClosed() {
        editedFilesAnalyzersMap.clear().items.each((analyzer) => 
            analyzer.dispose());
    }
    
    shared actual void fileClosed(FileEditorManager fileEditorManager, VirtualFile virtualFile) {
        if (!fileEditorManager.isFileOpen(virtualFile)) {
            value analyzer = editedFilesAnalyzersMap.remove(virtualFile);
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
        if (virtualFile.fileType == ceylonFileType) {
            editedFilesAnalyzersMap.putIfAbsent(virtualFile, () => newCeylonLocalAnalyzer(virtualFile));
        }
    }
    
    shared actual void fileDeleted(VirtualFileEvent virtualFileEvent) {
        editedFilesAnalyzersMap.remove(virtualFileEvent.file)?.dispose();
    }

    shared actual void contentsChanged(VirtualFileEvent virtualFileEvent) {
        value virtualFile = virtualFileEvent.file;
        if (exists analyzer = editedFilesAnalyzers[virtualFile],
            isInSourceArchive(virtualFile)) {
            editedFilesAnalyzersMap.put(virtualFile, newCeylonLocalAnalyzer(virtualFile));
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
    
    shared actual void ceylonModelParsed(CeylonProject<Module, VirtualFile, VirtualFile, VirtualFile> project) {
        value ceylonEditedFiles = concurrencyManager.needReadAccess(() =>
            fileEditorManagerInstance(model.ideaProject)
                .openFiles.array.coalesced).sequence();
        for (externalFile in ceylonEditedFiles.filter(isInSourceArchive)) {
            if (exists analyzer = editedFilesAnalyzersMap.get(externalFile)) {
                analyzer.scheduleExternalSourcePreparation(this);
            }
        }
    }

    shared actual void externalPhasedUnitsTypechecked({ExternalPhasedUnit*} typecheckedUnits, Boolean fullyTypechecked) {
        for (unit in typecheckedUnits) {
            if (exists virtualFile = CeylonTreeUtil.getDeclaringFile(unit.unit, model.ideaProject)?.virtualFile) {
                virtualFile.putUserData(uneditedExternalPhasedUnit, WeakReference<ExternalPhasedUnit>(unit));
                scheduleReparse(virtualFile);
            }
        }
    }

    shared void scheduleReparse(VirtualFile virtualFile) {
        ProgressManager.instance.executeNonCancelableSection(JavaRunnable(() {
            if (is SingleRootFileViewProvider fileViewProvider =
                application.runReadAction(object satisfies Computable<FileViewProvider> { compute()
                => psiManager(model.ideaProject).findViewProvider(virtualFile);})) {
                
                application.invokator.invokeLater(JavaRunnable(() {
                    application.runWriteAction(JavaRunnable(() {
                        value psiDocMgr = psiDocumentManager(model.ideaProject);
                        if (exists cachedDocument = fileDocumentManager.getCachedDocument(virtualFile),
                            psiDocMgr.isUncommited(cachedDocument)) {
                            psiDocMgr.commitDocument(cachedDocument);
                        }
                        fileViewProvider.onContentReload();
                    }));
                }), 
                ModalityState.any())
                .doWhenDone(JavaRunnable(() {
                    application.runReadAction(JavaRunnable(() {
                        if (exists cachedPsi = fileViewProvider.getCachedPsi(ceylonLanguage)) {
                            suppressWarnings("unusedDeclaration")
                            value unused = cachedPsi.node.lastChildNode;
                        }
                    }));
                }))
                .doWhenRejected(JavaRunnable(void () {
                    logger.error(() => "Reparse of file `` virtualFile.name`` was rejected", 20);
                }));
            }
        }));
    }

    selectionChanged(FileEditorManagerEvent fileEditorManagerEvent) => noop();
    beforeContentsChange(VirtualFileEvent virtualFileEvent) => noop();
    beforeFileDeletion(VirtualFileEvent virtualFileEvent) => noop();
    beforeFileMovement(VirtualFileMoveEvent virtualFileMoveEvent) => noop();
    beforePropertyChange(VirtualFilePropertyEvent virtualFilePropertyEvent) => noop();
    fileCopied(VirtualFileCopyEvent virtualFileCopyEvent) => noop();
    fileCreated(VirtualFileEvent virtualFileEvent) => noop();
    fileMoved(VirtualFileMoveEvent virtualFileMoveEvent) => noop();
    propertyChanged(VirtualFilePropertyEvent virtualFilePropertyEvent) => noop();
}