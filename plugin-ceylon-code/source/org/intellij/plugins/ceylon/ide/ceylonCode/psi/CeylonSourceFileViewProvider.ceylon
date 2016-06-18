import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    SingleRootFileViewProvider,
    PsiManager
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType,
    CeylonLanguage
}

shared class CeylonSourceFileViewProvider(
    PsiManager manager,
    VirtualFile virtualFile,
    Boolean eventSystemEnabled) 
        extends SingleRootFileViewProvider(
        manager, 
        virtualFile, 
        eventSystemEnabled, 
        CeylonLanguage.instance, 
        CeylonFileType.instance) {
    
    shared CeylonLocalAnalyzer ceylonLocalAnalyzer = CeylonLocalAnalyzer(virtualFile, manager.project);
    
    shared actual void markInvalidated() {
        ceylonSourceFileViewProviderFactoryLogger.debug(()=>"File view provider marked invalidated for virtual file `` virtualFile ``");
        ceylonLocalAnalyzer.dispose();
        super.markInvalidated();
    }
}