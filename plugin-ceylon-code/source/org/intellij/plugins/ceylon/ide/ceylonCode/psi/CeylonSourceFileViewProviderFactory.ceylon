import com.intellij.lang {
    Language
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    FileViewProviderFactory,
    FileViewProvider,
    PsiManager
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}
import com.intellij.testFramework {
    LightVirtualFile
}



Boolean isInSourceArchive(VirtualFile virtualFile)
        => ".src!/" in virtualFile.path.lowercased;

shared class CeylonSourceFileViewProviderFactory() 
        satisfies FileViewProviderFactory {
    
    
    shared actual FileViewProvider? createFileViewProvider(VirtualFile virtualFile, Language? language, PsiManager psiManager, Boolean eventSystemEnabled) {
        if (exists language, language != CeylonLanguage.instance ) {
            return null;
        }
        
        if (isInSourceArchive(virtualFile)) {
            return CeylonSourceFileViewProvider(psiManager, virtualFile, eventSystemEnabled);
        }
        
        if (virtualFile is LightVirtualFile) {
            // TODO : manage the case of source archives
            return null;
        }
        
        return CeylonSourceFileViewProvider(psiManager, virtualFile, eventSystemEnabled);
    }
}