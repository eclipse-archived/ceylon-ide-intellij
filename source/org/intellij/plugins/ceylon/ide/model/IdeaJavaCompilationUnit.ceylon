import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiClass,
    PsiMethod
}
import org.eclipse.ceylon.ide.common.model {
    JavaCompilationUnit
}
import org.eclipse.ceylon.model.loader.model {
    LazyPackage
}

shared class IdeaJavaCompilationUnit(
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    LazyPackage pkg)
        extends JavaCompilationUnit<Module,VirtualFile,VirtualFile,PsiClass,PsiClass|PsiMethod>
        (cls, filename, relativePath, fullPath, pkg)
        satisfies IdeaJavaModelAware {
    
    shared actual VirtualFile javaClassRootToNativeFile(PsiClass cls)
            => cls.containingFile.virtualFile;
    
    shared actual VirtualFile javaClassRootToNativeRootFolder(PsiClass cls)
            => cls.containingFile.virtualFile.parent;
}
