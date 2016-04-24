import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiClass
}
import com.redhat.ceylon.ide.common.model {
    JavaCompilationUnit
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

// TODO should not restrict to PsiClass (can also be a PsiMethod)
class IdeaJavaCompilationUnit(
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends JavaCompilationUnit<Module,VirtualFile,VirtualFile,PsiClass,PsiClass>
        (cls, filename, relativePath, fullPath, pkg)
        satisfies IdeaJavaModelAware {
    
    shared actual VirtualFile javaClassRootToNativeFile(PsiClass cls)
            => cls.containingFile.virtualFile;
    
    shared actual VirtualFile javaClassRootToNativeRootFolder(PsiClass cls)
            => cls.containingFile.virtualFile.parent;
}
