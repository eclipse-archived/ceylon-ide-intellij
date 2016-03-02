import com.redhat.ceylon.ide.common.util {
    ProgressMonitor
}

shared class DummyProgressMonitor() extends ProgressMonitor<String>() {
    shared actual void subTask(String? desc) {}
    
    shared actual void worked(Integer amount) {}
    shared actual Boolean cancelled => false;
    
    shared actual ProgressMonitor<String> convert(Integer work, String taskName) => this;
    
    shared actual ProgressMonitor<String> newChild(Integer work) => this;
    
    shared actual void updateRemainingWork(Integer remainingWork) {}
    
    shared actual String wrapped => "";
    
}
