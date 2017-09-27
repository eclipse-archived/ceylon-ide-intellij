import com.intellij.debugger.engine {
    JavaDebugAware
}
import org.eclipse.ceylon.ide.intellij.lang {
    CeylonFileType
}
import com.intellij.psi {
    PsiFile
}

shared class CeylonDebugAware() extends JavaDebugAware() {
    isBreakpointAware(PsiFile psiFile)
            => psiFile.fileType == CeylonFileType.instance;
}
