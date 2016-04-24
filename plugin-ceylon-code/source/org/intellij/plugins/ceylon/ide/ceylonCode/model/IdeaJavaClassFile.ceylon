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
    JavaClassFile
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

// TODO should not restrict to PsiClass (can also be a PsiMethod)
class IdeaJavaClassFile(
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    Package pkg)
        extends JavaClassFile<Module,VirtualFile,VirtualFile,PsiClass,PsiClass>
        (cls, filename, relativePath, fullPath, pkg)
        satisfies IdeaJavaModelAware {
    
    javaClassRootToNativeFile(PsiClass javaClassRoot)
        => javaClassRoot.containingFile.virtualFile;
    
    javaClassRootToNativeRootFolder(PsiClass javaClassRoot)
        => javaClassRoot.containingFile.virtualFile.parent;
}
