import com.intellij.lang {
    Language
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    FileViewProviderFactory,
    FileViewProvider,
    PsiManager,
    SingleRootFileViewProvider
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    ceylonLanguage
}
import com.intellij.testFramework {
    LightVirtualFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    CeylonLogger
}



shared Boolean isInSourceArchive(VirtualFile? virtualFile) {
    if (exists path = virtualFile?.path) {
        return ".src!/" in path.lowercased;
    } else {
        return false;
    }
}

CeylonLogger<CeylonSourceFileViewProviderFactory> ceylonSourceFileViewProviderFactoryLogger = CeylonLogger<CeylonSourceFileViewProviderFactory>();

shared class CeylonSourceFileViewProviderFactory() 
        satisfies FileViewProviderFactory {
    
    
    shared actual FileViewProvider createFileViewProvider(VirtualFile virtualFile, Language? language, PsiManager psiManager, Boolean eventSystemEnabled) {
        if (exists language, language != ceylonLanguage) {
            return SingleRootFileViewProvider(psiManager, virtualFile, eventSystemEnabled);
        }
        
        if (isInSourceArchive(virtualFile)) {
            ceylonSourceFileViewProviderFactoryLogger.trace(() => "Creating a CeylonSourceFileViewProvider for the virtual file: `` virtualFile ``");
            return CeylonSourceFileViewProvider(psiManager, virtualFile, eventSystemEnabled);
        }
        
        if (virtualFile is LightVirtualFile) {
            ceylonSourceFileViewProviderFactoryLogger.trace(() => "Don't create a CeylonSourceFileViewProvider for the light virtual file: `` virtualFile ``");
            return SingleRootFileViewProvider(psiManager, virtualFile, eventSystemEnabled);
        }
        
        ceylonSourceFileViewProviderFactoryLogger.trace(() => "Creating a CeylonSourceFileViewProvider for the virtual file: `` virtualFile ``");
        return CeylonSourceFileViewProvider(psiManager, virtualFile, eventSystemEnabled);
    }
}