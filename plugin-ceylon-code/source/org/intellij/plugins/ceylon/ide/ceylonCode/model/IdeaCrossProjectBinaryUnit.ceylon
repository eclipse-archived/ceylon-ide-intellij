import com.intellij.psi {
    PsiClass
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration
}
import com.redhat.ceylon.ide.common.model {
    CrossProjectBinaryUnit
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitor
}

class IdeaCrossProjectBinaryUnit(PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends CrossProjectBinaryUnit<Module,VirtualFile,VirtualFile,VirtualFile,PsiClass,PsiClass>
        (cls, filename, relativePath, fullPath, pkg) {
    
    shared actual Module javaClassRootToNativeProject(PsiClass javaClassRoot) => nothing;
    
    shared actual PsiClass? toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor) => nothing;
    
    
}