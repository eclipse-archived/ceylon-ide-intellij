import com.intellij.psi {
    PsiClass
}
import org.eclipse.ceylon.ide.common.model {
    BaseCeylonProject
}
import org.eclipse.ceylon.ide.common.platform {
    JavaModelServices
}
import org.eclipse.ceylon.model.loader.mirror {
    ClassMirror
}
import org.eclipse.ceylon.model.loader.model {
    LazyPackage
}
import org.eclipse.ceylon.model.typechecker.model {
    Unit
}

import org.eclipse.ceylon.ide.intellij.model {
    IdeaJavaCompilationUnit,
    IdeaJavaClassFile,
    IdeaCrossProjectBinaryUnit,
    PSIClass,
    IdeaCeylonBinaryUnit,
    IdeaCrossProjectJavaCompilationUnit
}

object ideaJavaModelServices satisfies JavaModelServices<PsiClass> {
    shared actual PsiClass getJavaClassRoot(ClassMirror classMirror) {
        assert(is PSIClass classMirror);
        return classMirror.psi;
    }
    
    shared actual Unit newCeylonBinaryUnit(PsiClass cls,
        String relativePath, String fileName, String fullPath, LazyPackage pkg)
            => IdeaCeylonBinaryUnit(cls, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newCrossProjectBinaryUnit(PsiClass cls,
        String relativePath, String fileName, String fullPath, LazyPackage pkg)
            => IdeaCrossProjectBinaryUnit(cls, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newJavaClassFile(PsiClass cls, String relativePath,
        String fileName, String fullPath, LazyPackage pkg)
            => IdeaJavaClassFile(cls, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newJavaCompilationUnit(PsiClass cls,
        String relativePath, String fileName, String fullPath, LazyPackage pkg)
            => IdeaJavaCompilationUnit(cls, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newCrossProjectJavaCompilationUnit(
        BaseCeylonProject ceylonProject, 
        PsiClass cls, String relativePath, String fileName, String fullPath, LazyPackage pkg)
            => IdeaCrossProjectJavaCompilationUnit(ceylonProject, cls, fileName, relativePath, fullPath, pkg);
    
}