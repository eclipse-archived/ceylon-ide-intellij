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
    ModalityState,
    ApplicationInfo,
    TransactionGuardImpl,
    TransactionGuard
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
    Ref
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

import org.intellij.plugins.ceylon.ide.lang {
    ceylonFileType,
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProjects,
    concurrencyManager {
        needReadAccess
    },
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.util {
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

    componentName => "CeylonLocalAnalyzerManager";
    
    shared actual void dispose()
            => editedFilesAnalyzersMap.clear().items
                .each((analyzer) => analyzer.dispose());

    shared actual void projectClosed() => dispose();

    shared actual void disposeComponent() {
        dispose();
        busConnection.disconnect();
        virtualFileManager.removeVirtualFileListener(this);
        model.removeModelListener(this);
    }
    
    shared actual void projectOpened()
            => startupManager(model.ideaProject)
                .runWhenProjectIsInitialized(()
                    => editedFilesAnalyzersMap.putAllKeys {
                        toItem = newCeylonLocalAnalyzer;
                        reuseExistingItems = true;
                        for (file in fileEditorManagerInstance(model.ideaProject).openFiles)
                        if (file.fileType == ceylonFileType)
                            file
                    });

    shared actual void initComponent() {
        busConnection = model.ideaProject.messageBus.connect();
        busConnection.subscribe(fileEditorManagerTopic, this);
        virtualFileManager.addVirtualFileListener(this);
        model.addModelListener(this);
    }
    
    "Reinstantiates local analyzers for currently open files, when a Ceylon facet
     is added to a module. This is necessary because the `CeylonLocalAnalyzer.ceylonProject`
     attribute needs to be recomputed after the project is added to `IdeaCeylonProjects`."
    shared void ceylonFacetAdded(FileEditorManager fileEditorManager) {
        for (file in fileEditorManager.openFiles) {
            if (file.fileType == ceylonFileType) {
                editedFilesAnalyzersMap.remove(file)?.dispose();
                fileOpened(fileEditorManager, file);
            }
        }
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
        value ceylonEditedFiles
                = needReadAccess(()
                    => [*fileEditorManagerInstance(model.ideaProject).openFiles]);

        for (unit in typecheckedUnits) {
            value virtualFile = unit.unitFile.nativeResource;
            if (virtualFile in ceylonEditedFiles) {
                if (is CeylonFile ceylonFile
                        = needReadAccess(()
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
        value ceylonEditedFiles
                = needReadAccess(()
                    => [*fileEditorManagerInstance(model.ideaProject).openFiles]);
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
        ApplicationManager.application.executeOnPooledThread(() {
            value phasedUnit = getCeylonProjects(model.ideaProject)?.findExternalPhasedUnit(virtualFile);
            if (exists phasedUnit) {
                triggerReparse(virtualFile, phasedUnit);
            } else {
                logger.debug(()=>"External phased unit not found in prepareExternalSource() for file ``virtualFile.name``");
            }
        });
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
            cleanOnFailure(virtualFile);
            logger.debug(()=>"External phased unit not found in retrieveTypecheckedAndBridgedExternalSource() for file ``virtualFile.name``");
        }
        logger.trace(()=>"Exit retrieveTypecheckedAndBridgedExternalSource() for file ``virtualFile.name``");
    }

    void cleanOnFailure(VirtualFile virtualFile, Throwable? t = null) {
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
    

    shared void triggerReparse(VirtualFile virtualFile, ExternalPhasedUnit? externalPhasedUnit = null, Boolean synchronously = false) {
        logger.trace(()=>"Enter scheduleReparse(``virtualFile``)", 10);
            Ref<FileViewProvider> providerRef = Ref<FileViewProvider>();
            ProgressManager.instance.executeNonCancelableSection(()
                => providerRef.set(
                    application.runReadAction(object satisfies Computable<FileViewProvider> {
                        compute()
                            => psiManager(model.ideaProject)
                                .findViewProvider(virtualFile);
                    })));

            if (is SingleRootFileViewProvider fileViewProvider = providerRef.get()) {
                if (exists externalPhasedUnit) {
                    virtualFile.putUserData(uneditedExternalPhasedUnitToParse, externalPhasedUnit);
                }
                
                void commitAndReloadContent() {
                    application.runWriteAction(() {
                        try {
                            if (!fileViewProvider.virtualFile.valid) {
                                return;
                            }
                            value psiDocMgr = psiDocumentManager(model.ideaProject);
                            if (exists cachedDocument = fileDocumentManager.getCachedDocument(virtualFile),
                                psiDocMgr.isUncommited(cachedDocument)) {
                                psiDocMgr.commitDocument(cachedDocument);
                            }
                            fileViewProvider.onContentReload();
                        } catch(Throwable t) {
                            cleanOnFailure(virtualFile, t);
                            throw t;
                        }
                    });
                }

                void triggerReparse() {
                    application.runReadAction(() {
                        if (model.ideaProject.isDisposed() || !fileViewProvider.virtualFile.valid) {
                            return;
                        }
                        try {
                            if (exists cachedPsi = fileViewProvider.getCachedPsi(ceylonLanguage)) {
                                suppressWarnings("unusedDeclaration")
                                value unused = cachedPsi.node.lastChildNode;
                            } else {
                                logger.debug(() => "no cached PSI file on which we could call `cachedPsi.node.lastChildNode` to trigger a reparse of file `` virtualFile.path ``");
                            }
                            if (exists ceylonFile = fileViewProvider.getPsi(ceylonLanguage)) {
                                if (!ceylonFile.firstChild exists) {
                                    logger.warn(() => "`ceylonFile.firstChild` is `null` in the `CeylonFile` of a triggered reparse of file `` virtualFile.path ``");
                                }
                            } else {
                                logger.warn(() => "ceylonFile is `null` at the end of a triggered reparse of file `` virtualFile.path ``");
                            }
                        } catch(Throwable t) {
                            cleanOnFailure(virtualFile, t);
                            throw t;
                        }
                    });
                }

                if (synchronously) {
                    assert(application.dispatchThread);
                    commitAndReloadContent();
                    triggerReparse();
                } else {
                    value runnable =
                            if (ApplicationInfo.instance.build.baselineVersion>=163,
                                is TransactionGuardImpl guard = TransactionGuard.instance)
                            then guard.wrapLaterInvocation(commitAndReloadContent, ModalityState.defaultModalityState())
                            else JavaRunnable(commitAndReloadContent);

                    application.invokator
                        .invokeLater(
                            runnable,
                            ModalityState.defaultModalityState(),
                            model.ideaProject.disposed)
                        .doWhenDone(triggerReparse)
                        .doWhenRejected(() {
                            logger.error(() => "Reparse of file `` virtualFile.name`` was rejected", 20);
                            cleanOnFailure(virtualFile);
                        });
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