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
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    CeylonLogger
}



Boolean isInSourceArchive(VirtualFile virtualFile)
        => ".src!/" in virtualFile.path.lowercased;

CeylonLogger<CeylonSourceFileViewProviderFactory> ceylonSourceFileViewProviderFactoryLogger = CeylonLogger<CeylonSourceFileViewProviderFactory>();

shared class CeylonSourceFileViewProviderFactory() 
        satisfies FileViewProviderFactory {
    
    
    shared actual FileViewProvider? createFileViewProvider(VirtualFile virtualFile, Language? language, PsiManager psiManager, Boolean eventSystemEnabled) {
        if (exists language, language != CeylonLanguage.instance ) {
            return null;
        }
        
        if (isInSourceArchive(virtualFile)) {
            ceylonSourceFileViewProviderFactoryLogger.debug(() => "Creating a CeylonSourceFileViewProvider for the virtual file: `` virtualFile ``", 15);            
            return CeylonSourceFileViewProvider(psiManager, virtualFile, eventSystemEnabled);
        }
        
        if (virtualFile is LightVirtualFile) {
            ceylonSourceFileViewProviderFactoryLogger.debug(() => "Don't create a CeylonSourceFileViewProvider for the light virtual file: `` virtualFile ``", 15);            
            return null;
        }
        
        ceylonSourceFileViewProviderFactoryLogger.debug(() => "Creating a CeylonSourceFileViewProvider for the virtual file: `` virtualFile ``", 15);            
        return CeylonSourceFileViewProvider(psiManager, virtualFile, eventSystemEnabled);
    }
}