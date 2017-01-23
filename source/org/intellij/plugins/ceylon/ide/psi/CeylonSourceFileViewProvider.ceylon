import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    SingleRootFileViewProvider,
    PsiManager
}

import org.intellij.plugins.ceylon.ide.lang {
    ceylonFileType,
    ceylonLanguage
}

shared class CeylonSourceFileViewProvider(
    PsiManager manager,
    VirtualFile virtualFile,
    Boolean eventSystemEnabled) 
        extends SingleRootFileViewProvider(
        manager, 
        virtualFile, 
        eventSystemEnabled, 
        ceylonLanguage,
        ceylonFileType) {
}