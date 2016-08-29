import ceylon.interop.java {
    JavaRunnable
}

import com.intellij.concurrency {
    AsyncFutureResult
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
        fileDocumentManager=instance
    }
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.progress {
    ProgressManager
}
import com.intellij.openapi.startup {
    StartupManager {
        startupManager=getInstance
    }
}
import com.intellij.openapi.util {
    Computable,
    Key,
    Ref,
    Conditions
}
import com.intellij.openapi.vfs {
    VirtualFileListener,
    VirtualFile,
    VirtualFileEvent,
    VirtualFileMoveEvent,
    VirtualFilePropertyEvent,
    VirtualFileCopyEvent,
    VirtualFileManager {
        virtualFileManager=instance
    }
}
import com.intellij.psi {
    PsiManager {
        psiManager=getInstance
    },
    FileViewProvider,
    SingleRootFileViewProvider,
    PsiDocumentManager {
        psiDocumentManager=getInstance
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

import java.lang.ref {
    WeakReference
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    ceylonFileType,
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    concurrencyManager,
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    CeylonLogger
}

CeylonLogger<CeylonLocalAnalyzerManager> ceylonLocalAnalyzerManagerLogger = CeylonLogger<CeylonLocalAnalyzerManager>();

shared Key<AsyncFutureResult<ExternalPhasedUnit>> uneditedExternalPhasedUnitFuture = Key<AsyncFutureResult<ExternalPhasedUnit>>("CeylonPlugin.nativeFile_uneditedExternalPhasedUnitFuture");
shared Key<WeakReference<ExternalPhasedUnit>> uneditedExternalPhasedUnitRef = Key<WeakReference<ExternalPhasedUnit>>("CeylonPlugin.nativeFile_uneditedExternalPhasedUnitRef");
shared Key<ExternalPhasedUnit> uneditedExternalPhasedUnitToParse = Key<ExternalPhasedUnit>("CeylonPlugin.nativeFile_uneditedExternalPhasedUnitToParse");

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
                triggerReparse(virtualFile);
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
            virtualFile.putUserData(uneditedExternalPhasedUnitRef, null);
            editedFilesAnalyzersMap.put(virtualFile, newCeylonLocalAnalyzer(virtualFile))?.dispose();
        }
    }

    shared actual void modelPhasedUnitsTypechecked({ProjectPhasedUnit<Module, VirtualFile, VirtualFile, VirtualFile>*} typecheckedUnits) {
        logger.trace(()=>"Enter modelPhasedUnitsTypechecked(``typecheckedUnits``)", 10);
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
                triggerReparse(virtualFile);
            }
        }
        logger.trace(()=>"Exit modelPhasedUnitsTypechecked(``typecheckedUnits``)");
    }

    shared actual void ceylonModelParsed(CeylonProject<Module, VirtualFile, VirtualFile, VirtualFile> project) {
        value ceylonEditedFiles = concurrencyManager.needReadAccess(() =>
            fileEditorManagerInstance(model.ideaProject)
                .openFiles.array.coalesced).sequence();
        for (externalFile in ceylonEditedFiles.filter(isInSourceArchive)) {
            if (exists analyzer = editedFilesAnalyzersMap.get(externalFile)) {
                scheduleExternalSourcePreparation(externalFile);
            }
        }
    }

    shared actual void externalPhasedUnitsTypechecked({ExternalPhasedUnit*} typecheckedUnits, Boolean fullyTypechecked) {
        logger.trace(()=>"Enter externalPhasedUnitsTypechecked(``typecheckedUnits``, ``fullyTypechecked``)", 10);
        for (unit in typecheckedUnits) {
            if (exists virtualFile = CeylonTreeUtil.getDeclaringFile(unit.unit, model.ideaProject)?.virtualFile) {
                triggerReparse(virtualFile, unit);
            }
        }
        logger.trace(()=>"Exit externalPhasedUnitsTypechecked(``typecheckedUnits``, ``fullyTypechecked``)");
    }

    shared class PhasedUnitSynchronizer(VirtualFile virtualFile) {
        shared void typecheckExternalPhasedUnitIfNecessary(void nextStep(ExternalPhasedUnit pu)) {
            value phasedUnit = getCeylonProjects(model.ideaProject)?.findExternalPhasedUnit(virtualFile);
            if (exists phasedUnit) {
                nextStep(phasedUnit);
            } else {
                logger.debug(()=>"External phased unit not found in prepareExternalSource() for file ``virtualFile.name``");
            }
        }
    }

    shared void scheduleExternalSourcePreparation(VirtualFile virtualFile) {
        logger.trace(()=>"Enter prepareExternalSource() for file ``virtualFile.name``", 20);
        ApplicationManager.application.executeOnPooledThread(JavaRunnable(() {
            value phasedUnit = getCeylonProjects(model.ideaProject)?.findExternalPhasedUnit(virtualFile);
            if (exists phasedUnit) {
                triggerReparse(virtualFile, phasedUnit);
            } else {
                logger.debug(()=>"External phased unit not found in prepareExternalSource() for file ``virtualFile.name``");
            }
        }));
        logger.trace(()=>"Exit prepareExternalSource() for file ``virtualFile.name``");
    }

    shared void retrieveTypecheckedAndBridgedExternalSource(VirtualFile virtualFile) {
        logger.trace(()=>"Enter retrieveTypecheckedAndBridgedExternalSource() for file ``virtualFile.name``", 20);
        "This synchronous method can only becalled from the UI thread.
         From pooled threads that don't hold the read lock, 
         call the asynchronous `scheduleExternalSourcePreparation` method."
        assert(application.dispatchThread);
        value phasedUnit = getCeylonProjects(model.ideaProject)?.findExternalPhasedUnit(virtualFile);
        if (exists phasedUnit) {
            triggerReparse(virtualFile, phasedUnit, true);
        } else {
            logger.debug(()=>"External phased unit not found in retrieveTypecheckedAndBridgedExternalSource() for file ``virtualFile.name``");
        }
        logger.trace(()=>"Exit retrieveTypecheckedAndBridgedExternalSource() for file ``virtualFile.name``");
    }

    shared void triggerReparse(VirtualFile virtualFile, ExternalPhasedUnit? externalPhasedUnit = null, Boolean synchronously = false) {
        logger.trace(()=>"Enter scheduleReparse(``virtualFile``)", 10);
            Ref<FileViewProvider> providerRef = Ref<FileViewProvider>();
            ProgressManager.instance.executeNonCancelableSection(JavaRunnable(() {
                providerRef.set(
                    application.runReadAction(object satisfies Computable<FileViewProvider> { compute()
                    => psiManager(model.ideaProject).findViewProvider(virtualFile);}));
            }));

            if (is SingleRootFileViewProvider fileViewProvider = providerRef.get()) {
                if (exists externalPhasedUnit) {
                    virtualFile.putUserData(uneditedExternalPhasedUnitToParse, externalPhasedUnit);
                }
                
                void cleanOnFailure(Throwable? t = null) {
                    virtualFile.putUserData(uneditedExternalPhasedUnitToParse, null);
                    if (exists future = virtualFile.getUserData(uneditedExternalPhasedUnitFuture)) {
                        if (! future.done) {
                            try {
                                if (exists t) {
                                    future.setException(t);
                                } else {
                                    future.cancel(true);
                                }
                            } catch(Throwable tt) {}
                        }
                    }
                }

                void commitAndReloadContent() {
                    application.runWriteAction(JavaRunnable(() {
                        try {
                            value psiDocMgr = psiDocumentManager(model.ideaProject);
                            if (exists cachedDocument = fileDocumentManager.getCachedDocument(virtualFile),
                                psiDocMgr.isUncommited(cachedDocument)) {
                                psiDocMgr.commitDocument(cachedDocument);
                            }
                            fileViewProvider.onContentReload();
                        } catch(Throwable t) {
                            cleanOnFailure(t);
                            throw;
                        }
                    }));
                }

                void triggerReparse() {
                    application.runReadAction(JavaRunnable(() {
                        try {
                            if (exists cachedPsi = fileViewProvider.getCachedPsi(ceylonLanguage)) {
                                suppressWarnings("unusedDeclaration")
                                value unused = cachedPsi.node.lastChildNode;
                            }
                        } catch(Throwable t) {
                            cleanOnFailure(t);
                            throw;
                        }
                    }));
                }
                
                if (synchronously) {
                    assert(application.dispatchThread);
                    commitAndReloadContent();
                    triggerReparse();
                } else {
                    application.invokator.invokeLater(
                        JavaRunnable(commitAndReloadContent), ModalityState.any(), 
                        model.ideaProject.disposed)
                            .doWhenDone(JavaRunnable(triggerReparse))
                            .doWhenRejected(JavaRunnable(void () {
                            logger.error(() => "Reparse of file `` virtualFile.name`` was rejected", 20);
                            cleanOnFailure();
                        }));
                }
            }
        logger.trace(()=>"Exit scheduleReparse(``virtualFile``)");
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