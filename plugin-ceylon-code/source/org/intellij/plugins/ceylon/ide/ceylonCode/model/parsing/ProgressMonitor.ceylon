import com.intellij.openapi.progress {
    ProgressIndicator
}
import com.redhat.ceylon.ide.common.util {
    ProgressMonitor
}

shared class DummyProgressMonitor() extends ProgressMonitor<String>() {
    shared actual void subTask(String? desc) {}
    
    shared actual void worked(Integer amount) {}
    shared actual Boolean cancelled => false;
    
    shared actual ProgressMonitor<String> convert(Integer work, String taskName) => this;
    
    shared actual ProgressMonitor<String> newChild(Integer work, Boolean prependMainLabelToSubtask) => this;
    
    shared actual void updateRemainingWork(Integer remainingWork) {}
    
    shared actual String wrapped => "";
    
    shared actual void done() {}
}

shared class ProgressIndicatorMonitor(ind, initialWork = 0)
        extends ProgressMonitor<ProgressIndicator>() {
    
    ProgressIndicator ind;
    variable Integer initialWork;
    variable Integer remainingWork = 0;
    variable String text2 = "";
    
    shared actual void subTask(String? desc) { 
        if (exists desc) {
            changeText(desc);
        }
    }
    
    shared actual Float worked(Integer amount) {
        updateRemainingWork(remainingWork - amount);
        return 0.0;
    }
    
    cancelled => ind.canceled;
    
    shared actual ProgressIndicatorMonitor convert(Integer work, String taskName) { 
        this.initialWork = work;
        changeText(taskName);
        return this; 
    }
    
    void changeText(String text) {
        text2 = text;
        ind.text2 = text;
    }
    
    newChild(Integer work, Boolean prependMainLabelToSubtask) => ProgressIndicatorMonitor(ind, work);
    
    shared actual void updateRemainingWork(Integer remainingWork) {
        this.remainingWork = remainingWork;
        
        if (initialWork == 0 || remainingWork < 0) {
            ind.fraction = 0.0;
        } else {
            ind.text2 = text2;
            ind.fraction = (initialWork.float - remainingWork) / initialWork;
        }
    }
    
    wrapped => ind;
    
    shared actual void done() {}
}
