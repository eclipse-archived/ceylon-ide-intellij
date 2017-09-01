import ceylon.collection {
    HashSet
}

import com.intellij.compiler.server {
    BuildManagerListener {
        buildManagerTopic=topic
    }
}
import com.intellij.facet {
    FacetManager
}
import com.intellij.notification {
    Notification,
    NotificationType,
    Notifications
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
    FileEditorManager,
    FileEditorManagerEvent,
    FileEditorManagerListener {
        fileEditorManagerTopic=fileEditorManager
    },
    FileDocumentManager {
        fileDocumentManager=instance
    }
}
import com.intellij.openapi.\imodule {
    Module,
    ModuleComponent
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
    ProjectUtil {
        isProjectOrWorkspaceFile
    },
    DumbService,
    Project
}
import com.intellij.openapi.roots {
    ProjectRootManager {
        projectRootManager=getInstance
    },
    ModuleRootManager
}
import com.intellij.openapi.roots.ui.configuration {
    ModulesConfigurator
}
import com.intellij.openapi.startup {
    StartupManager {
        startupManager=getInstance
    }
}
import com.intellij.openapi.util {
    Ref,
    Computable
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
    PsiFile
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
    ModelListenerAdapter,
    deltaBuilderFactory
}

import java.lang {
    InterruptedException,
    Error,
    Thread,
    ThreadLocal,
    Types {
        classForInstance
    }
}
import java.util {
    JHashSet=HashSet,
    UUID
}
import java.util.concurrent {
    TimeUnit,
    CountDownLatch,
    LinkedBlockingQueue,
    Future
}
import java.util.concurrent.atomic {
    AtomicInteger
}

import org.intellij.plugins.ceylon.ide.facet {
    CeylonFacet,
    CeylonFacetConfiguration
}
import org.intellij.plugins.ceylon.ide.messages {
    getCeylonProblemsView,
    SourceMsg,
    ProjectMsg
}
import org.intellij.plugins.ceylon.ide.model.parsing {
    ProgressIndicatorMonitor
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonLocalAnalyzerManager
}
import org.intellij.plugins.ceylon.ide.settings {
    ceylonSettings
}
import org.intellij.plugins.ceylon.ide.util {
    CeylonLogger
}

final class ChangesInProject of noChange | changesThatAlwaysRequireModelUpdate {
    shared new noChange {}
    shared new changesThatAlwaysRequireModelUpdate {}
}

CeylonLogger<CeylonModelManager> ceylonModelManagerLogger = CeylonLogger<CeylonModelManager>();

variable Boolean cachedLowerModelUpdatePriority = false;

ThreadLocal<Integer> modelUpdateOriginalPriority = ThreadLocal<Integer>();
Anything() withOriginalModelUpdatePriority() {
    if (cachedLowerModelUpdatePriority) {
        if (exists modelUpdatePriority = modelUpdateOriginalPriority.get()) {
            value currentThread = Thread.currentThread();
            value currentPriority = currentThread.priority;
            currentThread.priority = modelUpdatePriority;
            return () {
                Thread.currentThread().priority = currentPriority;
            };
        } else {
            return noop;
        }
    } else {
        return noop;
    }
}

shared class CeylonModelManager(IdeaCeylonProjects model_)
        satisfies ProjectComponent
                & Disposable
                & VirtualFileListener
                & FileEditorManagerListener
                & PsiDocumentTransactionListener
                & BuildManagerListener
                & ModelListenerAdapter<Module, VirtualFile, VirtualFile, VirtualFile>
                & ChangeAware<Module, VirtualFile, VirtualFile, VirtualFile>
                & ModelAliases<Module, VirtualFile, VirtualFile, VirtualFile> {

    value pauseRequests = AtomicInteger();
    variable Integer? lastRequestedDelay = null;
    pauseRequests.set(0);
    variable MessageBusConnection? busConnection = null;
    
    shared Integer delayBeforeUpdatingAfterChange => ceylonSettings.autoUpdateInterval;
    shared Integer modelUpdateTimeoutMinutes => ceylonSettings.modelUpdateTimeoutMinutes;
    variable value automaticModelUpdateEnabled_ = true;
    variable value ideaProjectReady = false;
    variable Boolean cancelledBecauseOfSyntaxErrors = false;
    variable Boolean cancelledBecauseOfAPause = false;
    variable Alarm? buildTriggeringAlarm = null;
    variable value busy_ = false;
    variable Anything()? stopWaitingForModelUpdate = null;

    shared IdeaCeylonProjects model => model_;
    shared Boolean busy => busy_;
    value lowerPriority {
        value lowerModelUpdatePriority = ceylonSettings.lowerModelUpdatePriority;
        cachedLowerModelUpdatePriority = lowerModelUpdatePriority;
        return lowerModelUpdatePriority;
    }

    value accumulatedChanges = LinkedBlockingQueue<NativeResourceChange>();
    variable Future<out Anything>? submitChangesFuture = null;

    value logger => ceylonModelManagerLogger;

    shared void pauseAutomaticModelUpdate()
            => pauseRequests.incrementAndGet();

    shared void resumeAutomaticModelUpdate(Integer delay = delayBeforeUpdatingAfterChange) {
        if (pauseRequests.decrementAndGet() == 0) {
            value force = delay == 0;
            if (cancelledBecauseOfAPause || force) {
                scheduleModelUpdate(delay, force);
            }
        }
    }
    
    Object submitChangesTask() {
        variable NativeResourceChange? firstChange = null;
        try {
            firstChange = accumulatedChanges.take();
        } catch(InterruptedException ie) {}

        value changeSet = JHashSet<NativeResourceChange>();
        if (exists first=firstChange) {
            changeSet.add(first);
        }
        accumulatedChanges.drainTo(changeSet);

        try {
            if (! changeSet.empty) {
                logger.debug(() => "Submitting ``changeSet.size()`` changes to the model");
                model.fileTreeChanged {*changeSet};
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
                logger.warnThrowable(e, () => "An exception as thrown during the change submission task");
            }
            accumulatedChanges.addAll(changeSet);
            Thread.sleep(1000);
        }

        if (ideaProjectReady) {
            scheduleSubmitChanges();
        }
        return "";
    }

    shared Boolean modelUpdateWasCannceledBecauseOfSyntaxErrors => cancelledBecauseOfSyntaxErrors;
    
    void scheduleSubmitChanges()
            => submitChangesFuture = application.executeOnPooledThread(submitChangesTask);
    
    shared Boolean automaticModelUpdateEnabled => automaticModelUpdateEnabled_;
    assign automaticModelUpdateEnabled {
        variable value needsRestart = false;
        if (automaticModelUpdateEnabled &&
            !automaticModelUpdateEnabled_) {
            needsRestart = true;
        }
        automaticModelUpdateEnabled_ = automaticModelUpdateEnabled;
        if (needsRestart) {
            scheduleModelUpdate(delayBeforeUpdatingAfterChange);
        }
    }
    
    componentName => "CeylonModelManager";

    function delayToUse(Integer requestedDelay) => min({requestedDelay, if (exists theLastDelay = lastRequestedDelay) theLastDelay});

    class ModelUpdateBackgroundTask(Ref<ProgressIndicator> buildProgressIndicator, CountDownLatch bakgroundBuildLatch) extends Backgroundable(
        model.ideaProject,
        "Ceylon model update",
        true,
        PerformInBackgroundOption.alwaysBackground) {
        
        shared actual void run(ProgressIndicator progressIndicator) {
            value currentThread = Thread.currentThread();
            value currentPriority = currentThread.priority;
            modelUpdateOriginalPriority.set(currentPriority);
            busy_ = true;
            buildProgressIndicator.set(progressIndicator);
            value monitor = ProgressIndicatorMonitor.wrap(progressIndicator);
            value ticks = model.ceylonProjectNumber * 1000;
            try (progress = monitor.Progress(ticks, "Updating Ceylon model")) {
                if (lowerPriority) {
                    currentThread.priority = currentPriority - (currentPriority  - Thread.minPriority) / 2;
                }
                
                concurrencyManager.withUpToDateIndexes(() {
                    for (ceylonProject in model
                        .ceylonProjectsInTopologicalOrder.sequence()
                            .reversed) {
                        setTitle("Ceylon model update for module `` ceylonProject.name ``");
                        ceylonProject.build.performBuild(progress.newChild(1000));
                    }
                });
            }
            catch (PsiElementGoneException e) {
                // A PSI element has been invalidated, we should rebuild the project
                // to make sure we keep a consistent state.
                for (project in model.ceylonProjects) {
                    project.build.requestFullBuild();
                }
                scheduleModelUpdate(0);
            }
            /*catch (ProcessCanceledException t) {
                throw t;
            }
            catch (Throwable t) {
                automaticModelUpdateEnabled = false;

                Notification(
                    "Ceylon Model Update",
                    "Ceylon model update failed",
                    "The Ceylon model update triggered an unexpected exception: `` t `` that will be reported in the Event View.
                           To avoid performance issues the automatic update of the Ceylon model has been disabled.
                           You can reenable it by using the following menu entry: Tools -> Ceylon -> Enable automatic update of model.",
                    warning
                ).notify(model.ideaProject);
                throw t;
            }*/
            finally {
                currentThread.priority = currentPriority;
                modelUpdateOriginalPriority.set(null);
                busy_ = false;
            }
        }
        onSuccess() => bakgroundBuildLatch.countDown();
        onCancel() => bakgroundBuildLatch.countDown();
    }

    void runModelUpdate() {
        value bakgroundBuildLatch = CountDownLatch(1);
        value buildProgressIndicator = Ref<ProgressIndicator>();
        stopWaitingForModelUpdate = () {
            bakgroundBuildLatch.countDown();
        };

        try {
            application.invokeAndWait(
                () => progressManager.run(ModelUpdateBackgroundTask(buildProgressIndicator, bakgroundBuildLatch)),
                ModalityState.any());
            
            if (! bakgroundBuildLatch.await(modelUpdateTimeoutMinutes, TimeUnit.minutes)) {
//                automaticModelUpdateEnabled_ = false;
                try {
                    buildProgressIndicator.get()?.cancel();
                } catch(e) {
                    logger.warnThrowable(e, () => "Error when trying to cancel a timed-out model update");
                }
                /*Notification(
                    "Ceylon Model Update",
                    "Ceylon model update stalled",
                    "The Ceylon model update didn't respond in a decent time. To avoid performance issues the automatic update of the Ceylon model has been disabled.
                     You can reenable it by using the following menu entry: Tools -> Ceylon -> Enable automatic update of model.",
                    warning).notify(model.ideaProject);*/
            }
        } finally {
            stopWaitingForModelUpdate = null;
        }
    }
    
    shared void scheduleModelUpdate(Integer delay = delayBeforeUpdatingAfterChange, Boolean force = false) {
        if (ideaProjectReady &&
            (automaticModelUpdateEnabled_ || force)) {
            value plannedDelay = delayToUse(delay);
            lastRequestedDelay = plannedDelay;
            buildTriggeringAlarm?.cancelAllRequests();
            buildTriggeringAlarm?.addRequest(() {
                    cancelledBecauseOfSyntaxErrors = false;
                    cancelledBecauseOfAPause = false;
                    buildTriggeringAlarm?.cancelAllRequests();
                    
                    void resetLastRequestedDelay() {
                        if (exists theLastDelay = lastRequestedDelay,
                            plannedDelay == theLastDelay) {
                            lastRequestedDelay = null;
                        }
                    }

                    for (project in model.ceylonProjects) {
                        if (project.build.fullBuildRequested,
                            project.ideConfiguration.compileToJvm else false,
                            ModuleRootManager.getInstance(project.ideArtifact).sdk is Null) {

                            Notifications.Bus.notify(Notification("Ceylon", "Missing JDK",
                                "The module '``project.ideArtifact.name``' should be compiled for the JVM but has no JDK configured.",
                                NotificationType.error));
                        }
                    }

                    if (! model.ceylonProjects.any((ceylonProject)
                        => ceylonProject.build.somethingToDo)) {
                        if (!force) {
                            resetLastRequestedDelay();                            
                        }
                        return;
                    }

                    if (! force) {
                        if (pauseRequests.get() > 0) {
                            cancelledBecauseOfAPause = true;
                            return;
                        }
                        
                        value mgr = FileEditorManager.getInstance(model.ideaProject);
                        function isThereOnlyOneCeylonFileContentChange(CeylonProjectAlias ceylonProject) {
                            value fileChangesToAnalyze = ceylonProject.build.fileChangesToAnalyze;
                            if (exists firstChange = fileChangesToAnalyze.first) {
                                if (fileChangesToAnalyze.rest.first exists) {
                                    return ChangesInProject.changesThatAlwaysRequireModelUpdate; // more than 1 changes
                                }
                                if (is FileVirtualFileContentChange firstChange) {
                                    value virtualFile = firstChange.resource.nativeResource;
                                    if (virtualFile in mgr.selectedFiles.array) {
                                        return virtualFile;
                                    } else {
                                        return ChangesInProject.changesThatAlwaysRequireModelUpdate; // change in a file that is not currently edited
                                    }
                                } else {
                                    return ChangesInProject.changesThatAlwaysRequireModelUpdate; // other type of change (non-source, non-Ceylon, etc ...)
                                }
                            } else {
                                return ChangesInProject.noChange;
                            }
                        }
                        
                        value changedCurrentlyEditedFiles = HashSet<VirtualFile>();
                        for (p in model.ceylonProjects) {
                            switch(answer = isThereOnlyOneCeylonFileContentChange(p))
                            case(ChangesInProject.changesThatAlwaysRequireModelUpdate) {
                                changedCurrentlyEditedFiles.clear();
                                break;
                            }
                            case(ChangesInProject.noChange) {
                            }
                            case(is VirtualFile) {
                                changedCurrentlyEditedFiles.add(answer);
                            }
                        }
                        
                        if (exists changedFile = changedCurrentlyEditedFiles.first,
                            changedCurrentlyEditedFiles.rest.empty) {
                            value analyzerManager = model.ideaProject.getComponent(`CeylonLocalAnalyzerManager`);
                            if (exists localAnalysisResult = analyzerManager[changedFile]?.result) {
                                variable value shouldCancel = false;
                                if (! localAnalysisResult.parsedRootNode.errors.empty) {
                                    shouldCancel = true;
                                } else {
                                    if (exists lastPhasedUnit = localAnalysisResult.lastPhasedUnit,
                                        is IdeaCeylonProject ceylonProject = localAnalysisResult.ceylonProject,
                                        exists projectFile = ceylonProject.projectFileFromNative(changedFile),
                                        exists modelPhasedUnit = ceylonProject.getParsedUnit(projectFile)) {
                                        
                                        try {
                                            value deltas = concurrencyManager.withAlternateResolution(() =>
                                                deltaBuilderFactory.buildDeltas {
                                                    referencePhasedUnit = modelPhasedUnit;
                                                    changedPhasedUnit = lastPhasedUnit;
                                                }
                                            );
                                            if (deltas.changes.empty && 
                                                deltas.childrenDeltas.empty) {
                                                shouldCancel = true;
                                            }
                                        } catch(e) {
                                        } catch(AssertionError e) {
                                            e.printStackTrace();
                                        } catch(Error e) {
                                            if (classForInstance(e).name ==
                                                "com.redhat.ceylon.compiler.java.runtime.metamodel.ModelError") {
                                                e.printStackTrace();
                                            } else {
                                                throw e;
                                            }
                                        }
                                    }
                                }
                                if (shouldCancel) {
                                    cancelledBecauseOfSyntaxErrors = true;
                                    resetLastRequestedDelay();
                                    return;
                                }
                            }
                        }
                    }
                    
                    resetLastRequestedDelay();                            
                    DumbService.getInstance(model.ideaProject).waitForSmartMode();
                   
                    runModelUpdate();
                    
                }, plannedDelay);
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
        busConnection?.disconnect();
        busConnection = null;
        VirtualFileManager.instance.removeVirtualFileListener(this);
        model.removeModelListener(this);
        submitChangesFuture?.cancel(true);
        submitChangesFuture = null; 
        buildTriggeringAlarm = null;
        if (exists reallyStop = stopWaitingForModelUpdate) {
            reallyStop();
        }
    }
    
    shared actual void initComponent() {
        buildTriggeringAlarm = Alarm(Alarm.ThreadToUse.pooledThread, this);
        value theBusConnection = model.ideaProject.messageBus.connect();
        busConnection = theBusConnection;
        theBusConnection.subscribe(fileEditorManagerTopic, this);
        theBusConnection.subscribe(psiDocumentTransactionTopic, this);
        theBusConnection.subscribe(buildManagerTopic, this);
        VirtualFileManager.instance.addVirtualFileListener(this);
        model.addModelListener(this);
    }
    
    shared actual void projectOpened() => 
            startupManager(model.ideaProject)
            .runWhenProjectIsInitialized(() {
                ideaProjectReady = true;
                scheduleModelUpdate(500);
                scheduleSubmitChanges();
            });
    
    projectClosed() => ideaProjectReady = false;
    
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
    
    shared void notifyFileContentChange(VirtualFile file) {
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
            notifyFileContentChange(virtualFile);
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
    
    propertyChanged(VirtualFilePropertyEvent evt)
            // TODO: Also manage the file rename
            => noop();

    transactionStarted(Document doc, PsiFile file) => noop();

    shared actual void transactionCompleted(Document document, PsiFile file) {
        value virtualFile = file.virtualFile;
        if (! file.directory) {
            notifyFileContentChange(virtualFile);
        }
    }
    
    /***************************************************************************
      FileEditorManagerListener implementation that:
       - forces to save the document when leaving an editor
       - triggers the typechecking when switching editors
     ***************************************************************************/
    
    fileClosed(FileEditorManager manager, VirtualFile file) => scheduleModelUpdate(0);
    
    fileOpened(FileEditorManager manager, VirtualFile file) => noop();
    
    selectionChanged(FileEditorManagerEvent evt) => scheduleModelUpdate(0);

    /***************************************************************************
      BuildManagerListener implementation that checks if the IDE is currently
      compiling something.
    ***************************************************************************/

    shared actual void beforeBuildProcessStarted(Project? project, UUID? sessionId) {}

    shared actual void buildStarted(Project? project, UUID? sessionId, Boolean isAutomake) {
        if (exists project, project == model.ideaProject) {
            pauseAutomaticModelUpdate();
        }
    }

    shared actual void buildFinished(Project? project, UUID? sessionId, Boolean isAutomake) {
        if (exists project, project == model.ideaProject) {
            resumeAutomaticModelUpdate();
        }
    }

    /***************************************************************************
      Utility functions
     ***************************************************************************/
}

"Manages Ceylon projects in the global model when the corresponding
 IJ module is opened/closed."
shared class CeylonProjectManager satisfies ModuleComponent {

    shared static CeylonProjectManager forModule(Module mod)
            => mod.getComponent(`CeylonProjectManager`);

    Module mod;
    variable IdeaCeylonProjects? ceylonModel;

    shared new (Module mod, IdeaCeylonProjects? ceylonModel) {
        this.mod = mod;
        this.ceylonModel = ceylonModel;
    }

    componentName => "CeylonProjectManager";

    shared actual void initComponent() {}

    "Unregisters the Ceylon project."
    shared actual void disposeComponent() {
        ceylonModel?.removeProject(mod);
    }

    "Registers the Ceylon project if the Ceylon facet is configured on this IJ module."
    shared actual void moduleAdded() {
        if (!exists _ = FacetManager.getInstance(mod).getFacetByType(CeylonFacet.id)) {
            return;
        }

        ceylonModel?.addProject(mod);
    }

    shared actual void projectOpened() {}
    shared actual void projectClosed() {}

    shared void addFacetToModule(Module mod, String? jdkProvider, Boolean forAndroid, Boolean showSettings) {
        if (!exists _ = ceylonModel) {
            value model = mod.project.getComponent(`IdeaCeylonProjects`);
            model.addProject(mod);
            ceylonModel = model;
        }
        assert (is IdeaCeylonProject ceylonProject = ceylonModel?.getProject(mod));
        if (forAndroid, exists jdkProvider) {
            ceylonProject.setupForAndroid(jdkProvider);
        }
        ceylonProject.configuration.save();
        CeylonFacet facet;
        if (exists f = CeylonFacet.forModule(mod)) {
            facet = f;
        } else {
            facet = ApplicationManager.application.runWriteAction(
                object satisfies Computable<CeylonFacet> {
                    compute() =>
                            FacetManager.getInstance(mod).addFacet(CeylonFacet.facetType, CeylonFacet.facetType.presentableName, null);
                }
            );
            facet.configuration.setModule(mod);
        }
        if (showSettings) {
            ModulesConfigurator.showFacetSettingsDialog(facet, CeylonFacetConfiguration.compilationTab);
        }
    }
}