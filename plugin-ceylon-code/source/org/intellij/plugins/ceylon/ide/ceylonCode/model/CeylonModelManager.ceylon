import ceylon.interop.java {
    JavaRunnable,
    CeylonIterable
}

import com.intellij.notification {
    Notification,
    NotificationType {
        warning
    }
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
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.fileEditor {
    FileEditorManager {
        fileEditorManagerInstance=getInstance
    },
    FileEditorManagerEvent,
    FileEditorManagerListener {
        fileEditorManagerTopic=fileEditorManager
    },
    FileDocumentManager {
        fileDocumentManager=instance
    }
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.progress {
    ProgressManager {
        progressManager=instance
    },
    Task {
        Backgroundable
    },
    ProgressIndicator,
    PerformInBackgroundOption,
    ProcessCanceledException
}
import com.intellij.openapi.project {
    ProjectCoreUtil {
        isProjectOrWorkspaceFile
    }
}
import com.intellij.openapi.roots {
    ProjectRootManager {
        projectRootManager=getInstance
    }
}
import com.intellij.openapi.startup {
    StartupManager {
        startupManager=getInstance
    }
}
import com.intellij.openapi.vfs {
    VirtualFileListener,
    VirtualFile,
    VirtualFilePropertyEvent,
    VirtualFileEvent,
    VirtualFileMoveEvent,
    VirtualFileCopyEvent,
    VirtualFileManager
}
import com.intellij.psi {
    PsiFile,
    PsiManager {
        psiManager=getInstance
    }
}
import com.intellij.psi.impl {
    PsiDocumentTransactionListener {
        psiDocumentTransactionTopic=topic
    }
}
import com.intellij.testFramework {
    LightVirtualFile
}
import com.intellij.util {
    Alarm
}
import com.intellij.util.messages {
    MessageBusConnection
}
import com.redhat.ceylon.ide.common.model {
    ChangeAware,
    ModelAliases,
    ModelListenerAdapter
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}

import java.lang {
    Runnable,
    InterruptedException
}
import java.util {
    JHashSet=HashSet,
    Collections
}
import java.util.concurrent {
    TimeUnit,
    CountDownLatch,
    LinkedBlockingQueue,
    Future
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType {
        ceylonFileType=instance
    }
}
import org.intellij.plugins.ceylon.ide.ceylonCode.messages {
    getCeylonProblemsView,
    SourceMsg,
    ProjectMsg
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing {
    ProgressIndicatorMonitor
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class CeylonModelManager(model) 
        satisfies ProjectComponent
        & Disposable
        & VirtualFileListener
        & FileEditorManagerListener
        & PsiDocumentTransactionListener
        & ModelListenerAdapter<Module, VirtualFile, VirtualFile, VirtualFile>
        & ChangeAware<Module, VirtualFile, VirtualFile, VirtualFile>
        & ModelAliases<Module, VirtualFile, VirtualFile, VirtualFile> {
    shared IdeaCeylonProjects model;
    shared variable Integer delayBeforeUpdatingAfterChange = 4000;
    variable value automaticModelUpdateEnabled_ = true;
    variable value ideaProjectReady = false;
    variable Boolean cancelledBecauseOfSyntaxErrors = false;
    late variable Alarm buildTriggeringAlarm;
    
    value accumulatedChanges = LinkedBlockingQueue<NativeResourceChange>();
    variable Future<out Anything>? submitChangesFuture = null;

    object submitChangesTask satisfies Runnable { 
        shared actual void run() {
            variable NativeResourceChange? firstChange = null;
            try {
                firstChange = accumulatedChanges.take();
            } catch(InterruptedException ie) {
            }
            try {
                value changeSet = JHashSet<NativeResourceChange>();
                if (exists first=firstChange) {
                    changeSet.add(first);
                }
                accumulatedChanges.drainTo(changeSet);
                if (! changeSet.empty) {
                    platformUtils.log(Status._DEBUG, "Submitting ``changeSet.size()`` changes to the model");
                    model.fileTreeChanged(CeylonIterable(changeSet));
                    scheduleModelUpdate();
                }
            } catch(Throwable t) {
                Exception e;
                if (is Exception t) {
                    e = t;
                } else {
                    e = Exception(null, t);
                }
                if (! e is ProcessCanceledException) {
                    platformUtils.log(Status._WARNING, "An exception as thrown during the change submission task", e);
                }
            }
            
            if (ideaProjectReady) {
                scheduleSubmitChanges();
            }
        }
    }

    late MessageBusConnection busConnection;
    
    shared Boolean modelUpdateWasCannceledBecauseOfSyntaxErrors => cancelledBecauseOfSyntaxErrors;
    
    void scheduleSubmitChanges() {
        submitChangesFuture = application.executeOnPooledThread(submitChangesTask);
    }
    
    shared Boolean automaticModelUpdateEnabled => automaticModelUpdateEnabled_;
    assign automaticModelUpdateEnabled {
        variable value needsRestart = false;
        if (automaticModelUpdateEnabled &&
            !automaticModelUpdateEnabled_) {
            needsRestart = true;
        }
        automaticModelUpdateEnabled_ = automaticModelUpdateEnabled;
        if (needsRestart) {
            scheduleModelUpdate(0);
        }
    }
    
    componentName => "CeylonModelManager";
    
    shared void scheduleModelUpdate(Integer delay = delayBeforeUpdatingAfterChange, Boolean force = false) {
        if (ideaProjectReady && 
            (automaticModelUpdateEnabled_ || force)) {
            buildTriggeringAlarm.cancelAllRequests();
            buildTriggeringAlarm.addRequest(JavaRunnable {
                void run () {
                    cancelledBecauseOfSyntaxErrors = false;
                    buildTriggeringAlarm.cancelAllRequests();
                    
                    if (! model.ceylonProjects.any((ceylonProject)
                        => ceylonProject.build.somethingToDo)) {
                        return;
                    }

                    function syntaxErrorsWhenOnlyOneCeylonChange(CeylonProjectAlias ceylonProject) {
                        value fileChangesToAnalyze = ceylonProject.build.fileChangesToAnalyze;
                        if (exists firstChange = fileChangesToAnalyze.first) {
                            if (fileChangesToAnalyze.rest.first exists) {
                                return null; // more than 1 changes
                            }
                            if (is FileVirtualFileContentChange firstChange,
                                is CeylonFile ceylonFile = concurrencyManager.needReadAccess(() => psiManager(model.ideaProject).findFile(firstChange.resource.nativeResource) else null),
                                exists cu = ceylonFile.compilationUnit,
                                ! cu.errors.empty) {
                                return cu.errors ;
                            } else {
                                return null; // other type of change (non-source, non-Ceylon, etc ...)
                            }
                        } else {
                            return Collections.emptyList();
                        }
                    }
                    
                    if (! force) {
                        variable value shouldCancel = false;
                        for (p in model.ceylonProjects) {
                            if (exists syntaxErrors = syntaxErrorsWhenOnlyOneCeylonChange(p)) {
                                if (! syntaxErrors.empty) {
                                    shouldCancel = true;
                                }
                            } else {
                                shouldCancel = false;
                                break;
                            }
                        }
                        
                        if (shouldCancel) {
                            cancelledBecauseOfSyntaxErrors = true;
                            return;
                        }
                    }
                    
                    value bakgroundBuildLatch = CountDownLatch(1);
                    application.invokeAndWait(JavaRunnable {
                        run() => progressManager.run(object extends Backgroundable(
                            model.ideaProject,
                            "ceylon model update",
                            true,
                            PerformInBackgroundOption.\iALWAYS_BACKGROUND) {
                            
                            shared actual void run(ProgressIndicator progressIndicator) {
                                value monitor = ProgressIndicatorMonitor.wrap(progressIndicator);
                                value ticks = model.ceylonProjectNumber * 1000;
                                try (progress = monitor.Progress(ticks, "Updating Ceylon Model")) {
                                    concurrencyManager.withUpToDateIndexes(() {
                                        for (ceylonProject in model
                                                .ceylonProjectsInTopologicalOrder.sequence()
                                                .reversed) {
                                            ceylonProject.build.performBuild(progress.newChild(1000));
                                        }
                                    });
                                    
                                    value ceylonEditedFiles = concurrencyManager.needReadAccess(() =>
                                            fileEditorManagerInstance(model.ideaProject)
                                                .openFiles.array.coalesced
                                                .filter((file) => file.fileType == ceylonFileType));
                                        
                                    ceylonEditedFiles.each(void (VirtualFile element) {
                                        if (is CeylonFile ceylonFile = concurrencyManager.needReadAccess(()=>
                                                psiManager(model.ideaProject).findFile(element)),
                                            exists localAnalyzer = ceylonFile.localAnalyzer) {
                                            localAnalyzer.scheduleForcedTypechecking();
                                        }
                                    });
                                    /*
                                    application.invokeLater(JavaRunnable {
                                        void run() {
                                            reparseFiles(*fileEditorManagerInstance(model.ideaProject)
                                            .openFiles.array.coalesced
                                            .filter((file) => file.fileType == ceylonFileType));
                                        }
                                    }, ModalityState.any());
                                     */
                                } catch(Throwable t) {
                                    if (is ProcessCanceledException t) {
                                        throw t;
                                    } else {
                                        automaticModelUpdateEnabled = false;

                                        Notification(
                                            "Ceylon Model Update",
                                            "Ceylon Model Update failed",
                                            "The Ceylon model update triggered an unexpected exception: `` t `` that will be reported in the Event View.
                                                   To avoid performance issues the automatic update of the Ceylon model has been disabled.
                                                   You can reenable it by using the following menu entry: Tools -> Ceylon -> Enable automatic update of model.",
                                            warning
                                        ).notify(model.ideaProject);
                                        throw t;
                                    }
                                }
                            }
                            shared actual void onSuccess() {
                                bakgroundBuildLatch.countDown();
                            }
                            shared actual void onCancel() {
                                bakgroundBuildLatch.countDown();
                            }
                        });
                    }, ModalityState.any());
                    if (! bakgroundBuildLatch.await(5, TimeUnit.minutes)) {
                        automaticModelUpdateEnabled_ = false;
                        Notification(
                            "Ceylon Model Update",
                            "Ceylon Model Update stalled",
                            "The Ceylon model update didn't respond in a decent time. To avoid performance issues the automatic update of the Ceylon model has been disabled.
                             You can reenable it by using the following menu entry: Tools -> Ceylon -> Enable automatic update of model.",
                            warning).notify(model.ideaProject);
                    }
                }
            }, delay);
        }
    }
    
    /***************************************************************************
      ModelListenerAdapter implementations
     ***************************************************************************/
    
    ceylonProjectAdded(CeylonProjectAlias ceylonProject) =>
            scheduleModelUpdate(0);

    shared actual void buildMessagesChanged(CeylonProjectAlias project, {SourceMsg*}? frontendMessages,
        {SourceMsg*}? backendMessages, {ProjectMsg*}? projectMessages) {

        assert(is IdeaCeylonProject project);

        getCeylonProblemsView(model.ideaProject).updateMessages(project,
            frontendMessages, backendMessages, projectMessages);
    }

    /***************************************************************************
      ProjectComponent implementations
     ***************************************************************************/

    shared actual void dispose() {}
    
    shared actual void disposeComponent() {
        dispose();
        busConnection.disconnect();
        VirtualFileManager.instance.removeVirtualFileListener(this);
        model.removeModelListener(this);
        submitChangesFuture?.cancel(true);
    }
    
    shared actual void initComponent() {
        buildTriggeringAlarm = Alarm(Alarm.ThreadToUse.pooledThread, this);
        busConnection = model.ideaProject.messageBus.connect();
        busConnection.subscribe(fileEditorManagerTopic, this);
        busConnection.subscribe(psiDocumentTransactionTopic, this);
        VirtualFileManager.instance.addVirtualFileListener(this);
        model.addModelListener(this);
    }
    
    shared actual void projectOpened() => 
            startupManager(model.ideaProject)
            .runWhenProjectIsInitialized(JavaRunnable(() {
        ideaProjectReady = true;
        scheduleModelUpdate(500);
        scheduleSubmitChanges();
    }));
    
    shared actual void projectClosed() {
        ideaProjectReady = false;
    }
    
    /***************************************************************************
      VirtualFileListener implementation that notifies the file changes
      to the Ceylon Model
     ***************************************************************************/
    
    beforeContentsChange(VirtualFileEvent evt) => noop();
    beforeFileDeletion(VirtualFileEvent evt) => noop();
    beforeFileMovement(VirtualFileMoveEvent evt) => noop();
    beforePropertyChange(VirtualFilePropertyEvent evt) => noop();
    
    void notifyChanges({NativeResourceChange*} changes) {
        for (change in changes) {
            if (! accumulatedChanges.offer(change)) {
                model.ceylonProjects*.build*.requestFullBuild();
                break;
            }
        }
    }
    
    void notifyFileContenChange(VirtualFile file) {
        if (! isProjectOrWorkspaceFile(file)) {
            notifyChanges { NativeFileContentChange(file) };
        }
    }

    shared actual void contentsChanged(VirtualFileEvent evt) {
        value virtualFile = evt.file;
        if (! virtualFile.directory &&
            virtualFile.inLocalFileSystem &&
            evt.fromRefresh &&
            projectRootManager(model.ideaProject).fileIndex.isInContent(virtualFile) &&
            ! projectRootManager(model.ideaProject).fileIndex.isExcluded(virtualFile) &&
            ! fileDocumentManager.getCachedDocument(virtualFile) exists) {
            notifyFileContenChange(virtualFile);
        }
    }
    
    fileCopied(VirtualFileCopyEvent evt) => fileCreated(evt);
    
    shared actual void fileCreated(VirtualFileEvent evt) {
        value file = evt.file;
        notifyChanges {
            if (file.directory)
            then NativeFolderAddition(file)
            else NativeFileAddition(file)
        };
    }
    
    shared actual void fileDeleted(VirtualFileEvent evt) {
        value file = evt.file;
        notifyChanges {
            if (file.directory)
            then NativeFolderRemoval(file, null)
            else NativeFileRemoval(file, null)
        };
    }
    
    shared actual void fileMoved(VirtualFileMoveEvent evt) {
        value file = evt.file;
        value oldParent = evt.oldParent;
        value oldFile = object extends LightVirtualFile(file.name) {
            parent => oldParent;
            directory => file.directory;
        };
        
        notifyChanges(
            if (file.directory)
            then { NativeFolderRemoval(file, oldFile), NativeFolderAddition(file) }
            else { NativeFileRemoval(file, oldFile), NativeFileAddition(file) }
        );
    }
    
    shared actual void propertyChanged(VirtualFilePropertyEvent evt) {
        // TODO: Also manage the file rename
        noop();
    }

    transactionStarted(Document doc, PsiFile file) => noop();

    shared actual void transactionCompleted(Document document, PsiFile file) {
        value virtualFile = file.virtualFile;
        if (! file.directory) {
            notifyFileContenChange(virtualFile);
        }
    }
    
    /***************************************************************************
      FileEditorManagerListener implementation that:
       - forces to save the document when leaving an editor
       - triggers the typechecking when switching editors
     ***************************************************************************/
    
    fileClosed(FileEditorManager manager, VirtualFile file) => scheduleModelUpdate(0);
    
    fileOpened(FileEditorManager manager, VirtualFile file) => noop();
    
    shared actual void selectionChanged(FileEditorManagerEvent evt) => scheduleModelUpdate(0);
    
    /***************************************************************************
      Utility functions
     ***************************************************************************/
}
