import com.intellij.psi {
    PsiClass
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration
}
import com.redhat.ceylon.ide.common.model {
    JavaClassFile
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

class IdeaJavaClassFile(
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends JavaClassFile<Module,VirtualFile,VirtualFile,PsiClass,PsiClass>
        (cls, filename, relativePath, fullPath, pkg) {
    
    shared actual VirtualFile? javaClassRootToNativeFile(PsiClass javaClassRoot) => nothing;
    
    shared actual Module javaClassRootToNativeProject(PsiClass javaClassRoot) => nothing;
    
    shared actual VirtualFile? javaClassRootToNativeRootFolder(PsiClass javaClassRoot) => nothing;
    
    shared actual PsiClass? toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor) => nothing;
    
    
}