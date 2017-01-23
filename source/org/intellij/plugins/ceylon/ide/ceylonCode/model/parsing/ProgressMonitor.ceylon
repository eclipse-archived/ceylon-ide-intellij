import com.intellij.openapi.progress {
    ProgressIndicator
}
import com.redhat.ceylon.ide.common.util {
    ProgressMonitorImpl
}

shared class DummyProgressMonitor extends ProgressMonitorImpl<String> {
    shared new child(ProgressMonitorImpl<String> parent, Integer allocatedWork)
            extends super.child(parent, allocatedWork) {
    }
    
    shared new wrap(String? monitor) 
            extends super.wrap(monitor) {
    }
    
    cancelled => false;
    
    newChild(Integer allocatedWork) => this;
    
    shared actual void subTask(String subTaskDescription) {}
    
    shared actual void updateRemainingWork(Integer remainingWork) {}
    
    shared actual void worked(Integer amount) {}
    
    wrapped => "";
    
}

shared class ProgressIndicatorMonitor
        extends ProgressMonitorImpl<ProgressIndicator> {

    variable Integer initialWork;
    ProgressIndicator ind;

    shared new child(ProgressMonitorImpl<ProgressIndicator> parent, Integer allocatedWork)
            extends super.child(parent, allocatedWork) {
        ind = parent.wrapped;
        initialWork = allocatedWork;
    }
    
    shared new wrap(ProgressIndicator monitor) 
            extends super.wrap(monitor) {
        ind = monitor;
        initialWork = 0;
    }

    variable Integer remainingWork = 0;
    variable String text2 = "";
    
    shared actual void subTask(String desc) { 
        text2 = desc;
        ind.text2 = desc;
    }
    
    shared actual Float worked(Integer amount) {
        updateRemainingWork(remainingWork - amount);
        return 0.0;
    }
    
    cancelled => ind.canceled;
    
    shared actual void updateRemainingWork(Integer remainingWork) {
        this.remainingWork = remainingWork;
        
        if (initialWork == 0 || remainingWork < 0) {
            ind.fraction = 0.0;
        } else {
            ind.text2 = text2;
            ind.fraction = (initialWork.float - remainingWork) / initialWork;
        }
    }
    
    shared actual void done() {}
    
    newChild(Integer allocatedWork) => child(this, allocatedWork);
    
    wrapped => ind;
}
