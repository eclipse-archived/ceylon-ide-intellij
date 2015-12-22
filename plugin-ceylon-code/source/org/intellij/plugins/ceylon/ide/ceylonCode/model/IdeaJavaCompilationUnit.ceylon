import com.redhat.ceylon.ide.common.model {
    JavaCompilationUnit
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiClass
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration
}
import com.redhat.ceylon.ide.common.util {
    BaseProgressMonitor
}

class IdeaJavaCompilationUnit(
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends JavaCompilationUnit<Module,VirtualFile,VirtualFile,PsiClass,PsiClass>
        (cls, filename, relativePath, fullPath, pkg) {
    
    shared actual VirtualFile javaClassRootToNativeFile(PsiClass cls)
            => cls.containingFile.virtualFile;
    
    shared actual Module javaClassRootToNativeProject(PsiClass cls)
            => nothing;
    
    shared actual VirtualFile javaClassRootToNativeRootFolder(PsiClass cls)
            => cls.containingFile.virtualFile.parent;
    
    shared actual PsiClass? toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor) => nothing;
}
