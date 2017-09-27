import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    SingleRootFileViewProvider,
    PsiManager
}

import org.eclipse.ceylon.ide.intellij.lang {
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