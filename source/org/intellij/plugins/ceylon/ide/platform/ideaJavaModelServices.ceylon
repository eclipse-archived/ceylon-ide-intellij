import com.intellij.psi {
    PsiClass
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    JavaModelServices
}
import com.redhat.ceylon.model.loader.mirror {
    ClassMirror
}
import com.redhat.ceylon.model.loader.model {
    LazyPackage
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}

import org.intellij.plugins.ceylon.ide.model {
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