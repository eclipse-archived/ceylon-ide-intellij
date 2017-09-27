import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiMethod,
    PsiClass
}
import org.eclipse.ceylon.ide.common.model {
    CrossProjectJavaCompilationUnit,
    BaseCeylonProject
}
import org.eclipse.ceylon.model.loader.model {
    LazyPackage
}

shared class IdeaCrossProjectJavaCompilationUnit(
    BaseCeylonProject ceylonProject,
    PsiClass cls,
    String filename,
    String relativePath,
    String fullPath,
    LazyPackage pkg)
        extends CrossProjectJavaCompilationUnit<Module,VirtualFile, VirtualFile,PsiClass,PsiClass|PsiMethod>
        (ceylonProject, cls, filename, relativePath, fullPath, pkg)
        satisfies IdeaJavaModelAware {
    
    shared actual VirtualFile javaClassRootToNativeFile(PsiClass cls)
            => cls.containingFile.virtualFile;
    
    shared actual VirtualFile javaClassRootToNativeRootFolder(PsiClass cls)
            => cls.containingFile.virtualFile.parent;
}
