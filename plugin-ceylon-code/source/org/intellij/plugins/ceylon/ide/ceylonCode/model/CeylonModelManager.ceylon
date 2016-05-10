import ceylon.interop.java {
    JavaRunnable,
    synchronize
}

import com.intellij.codeInsight.daemon {
    DaemonCodeAnalyzer
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
    FileEditorManager,
    FileDocumentManager {
        fileDocumentManager=instance
    },
    FileEditorManagerEvent,
    FileEditorManagerListener
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.progress {
    ProgressManager,
    Task {
        Backgroundable
    },
    ProgressIndicator,
    PerformInBackgroundOption
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
import com.intellij.testFramework {
    LightVirtualFile
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
import java.util {
    Timer,
    TimerTask
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model.parsing {
    ProgressIndicatorMonitor
}

shared class CeylonModelManager(model) 
    satisfies ProjectComponent
    & VirtualFileListener
    & FileEditorManagerListener
    & ModelListenerAdapter<Module, VirtualFile, VirtualFile, VirtualFile>
    & ChangeAware<Module, VirtualFile, VirtualFile, VirtualFile>
    & ModelAliases<Module, VirtualFile, VirtualFile, VirtualFile> {
    shared IdeaCeylonProjects model;
    shared variable Integer typecheckingPeriod = 1000;
    variable value typecheckingPlanned = false;
    variable value ideaProjectReady = false;
    value timer = Timer(true);

    componentName => "CeylonModelManager";
    
    object buildBackgroundTask extends Backgroundable(
        model.ideaProject, 
        "ceylon model update", 
        true, 
        PerformInBackgroundOption.\iALWAYS_BACKGROUND) {
        
        run(ProgressIndicator progressIndicator) => synchronize(this, () {
            typecheckingPlanned = false;
            value monitor = ProgressIndicatorMonitor.wrap(progressIndicator);
            value ticks = model.ceylonProjectNumber * 1000;
            try (progress = monitor.Progress(ticks, "Updating Ceylon Model")) {
                for (ceylonProject in model.ceylonProjectsInTopologicalOrder) {
                    ceylonProject.build.performBuild(progress.newChild(1000));
                }
            }
            application.invokeLater(JavaRunnable(() =>
                DaemonCodeAnalyzer.getInstance(model.ideaProject).restart()));
        });
    }

    shared void startBuild() {
        if (ideaProjectReady
            && !typecheckingPlanned
                && model.ceylonProjects.any((ceylonProject) 
            => ceylonProject.build.somethingToDo)) {
            typecheckingPlanned = true;
            ProgressManager.instance.run(buildBackgroundTask);
        }
        
    }
    
    object timerTask extends TimerTask() { 
        void catchAnyThrowable(void run()) {
            try {
                run();
            } catch(Throwable t) {
                platformUtils.log {
                    status = Status._ERROR;
                    value message {
                        variable value msg = "Ceylon Model Update task submission failed";
                        if (! t is Exception) {
                            msg += " with error : `` t ``";
                        }
                        return msg;
                    }
                    e = if (is Exception t) then t else null;
                };
            }
        }
        run () => catchAnyThrowable { 
            run() => application.invokeAndWait( JavaRunnable { 
                run () => catchAnyThrowable { 
                    run() => startBuild(); 
                }; 
            }, ModalityState.\iNON_MODAL); 
        }; 
    }
    
    /***************************************************************************
      ModelListenerAdapter implementations
     ***************************************************************************/
    
    ceylonProjectAdded(CeylonProjectAlias ceylonProject) =>
            startBuild();
    
    /***************************************************************************
      ProjectComponent implementations
     ***************************************************************************/
    
    shared actual void disposeComponent() {
        VirtualFileManager.instance.removeVirtualFileListener(this);
        model.removeModelListener(this);
    }
    
    shared actual void initComponent() {
        VirtualFileManager.instance.addVirtualFileListener(this);
        model.ideaProject.messageBus.connect()
                .subscribe(FileEditorManagerListener.\iFILE_EDITOR_MANAGER, this);
        model.addModelListener(this);
    }

    shared actual void projectOpened() => 
            startupManager(model.ideaProject)
                .runWhenProjectIsInitialized(JavaRunnable(() {
                    ideaProjectReady = true;
                    timer.schedule(timerTask, 0, typecheckingPeriod);
                }));

    shared actual void projectClosed() {
        ideaProjectReady = false;
        timer.cancel();
    }

    /***************************************************************************
      VirtualFileListener implementation that notifies the file changes
      to the Ceylon Model
     ***************************************************************************/

    beforeContentsChange(VirtualFileEvent evt) => noop();
    beforeFileDeletion(VirtualFileEvent evt) => noop();
    beforeFileMovement(VirtualFileMoveEvent evt) => noop();
    beforePropertyChange(VirtualFilePropertyEvent evt) => noop();
    
    shared actual void contentsChanged(VirtualFileEvent evt) {
        value file = evt.file;
        if (! file.directory) {
            model.fileTreeChanged({
                NativeFileContentChange(file)
            });
        }
    }
        
    fileCopied(VirtualFileCopyEvent evt) => fileCreated(evt);
    
    shared actual void fileCreated(VirtualFileEvent evt) {
        value file = evt.file;
        model.fileTreeChanged({
            if (file.directory)
            then NativeFolderAddition(file)
            else NativeFileAddition(file)
        });
    }
    
    shared actual void fileDeleted(VirtualFileEvent evt) {
        value file = evt.file;
        model.fileTreeChanged({
            if (file.directory)
            then NativeFolderRemoval(file, null)
            else NativeFileRemoval(file, null)
        });
    }
    
    shared actual void fileMoved(VirtualFileMoveEvent evt) {
        value file = evt.file;
        value oldParent = evt.oldParent;
        value oldFile = object extends LightVirtualFile(file.name) {
            parent => oldParent;
            directory => file.directory;
        };
        
        model.fileTreeChanged(
            if (file.directory)
            then { NativeFolderRemoval(file, oldFile), NativeFolderAddition(file) }
            else { NativeFileRemoval(file, oldFile), NativeFileAddition(file) }
        );
    }

    shared actual void propertyChanged(VirtualFilePropertyEvent evt) {
        // TODO Manage the file rename !
    }

    /***************************************************************************
      FileEditorManagerListener implementation that:
       - forces to save the document when leaving an editor
       - triggers the typechecking when switching editors
     ***************************************************************************/
    
    fileClosed(FileEditorManager manager, VirtualFile file) => save(file);

    fileOpened(FileEditorManager manager, VirtualFile file) => noop();
    
    shared actual void selectionChanged(FileEditorManagerEvent evt) {
        if (exists oldFile = evt.oldFile,
            exists newFile = evt.newFile) {
            save(oldFile);
            // TODO: if the fileChange event is already triggered and managed (=> synchronous) then
            // we could schedule a Build. Knowing that scheduling it again does nothing it the first one is not started.
            startBuild();
        }
    }
    
    void save(VirtualFile file) {
        assert(!file.directory);
        if (exists ceylonProject = ceylonProjectForFile(file),
            ceylonProject.isCeylon(file),
            ceylonProject.isFileInSourceFolder(file),
            exists doc = fileDocumentManager.getCachedDocument(file)) {
            
            // Will trigger contentsChanged(), which will call the typechecker
            fileDocumentManager.saveDocument(doc);
        }
    }

    /***************************************************************************
      Utility functions
     ***************************************************************************/
    
    CeylonProjectAlias? ceylonProjectForFile(VirtualFile? file) =>
            if (exists file) 
            then model.getProject(projectRootManager(model.ideaProject).fileIndex.getModuleForFile(file))
            else null;
}
