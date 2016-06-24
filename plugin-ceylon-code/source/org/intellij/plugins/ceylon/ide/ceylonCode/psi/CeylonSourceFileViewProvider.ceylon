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
}