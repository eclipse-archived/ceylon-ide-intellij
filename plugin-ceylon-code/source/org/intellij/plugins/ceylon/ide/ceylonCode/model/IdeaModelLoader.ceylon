import com.intellij.openapi.\imodule {
    IJModule=Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    JavaPsiFacade,
    PsiClass
}
import com.redhat.ceylon.ide.common.model {
    IdeModelLoader,
    BaseIdeModule
}
import com.redhat.ceylon.model.cmr {
    ArtifactResult
}
import com.redhat.ceylon.model.loader.mirror {
    ClassMirror,
    MethodMirror
}
import com.redhat.ceylon.model.loader.model {
    LazyPackage
}
import com.redhat.ceylon.model.typechecker.model {
    Module,
    Modules,
    Unit
}

shared class IdeaModelLoader(IdeaModuleManager ideaModuleManager,
    IdeaModuleSourceMapper ideaModuleSourceMapper, Modules modules)
        extends IdeModelLoader<IJModule,VirtualFile,VirtualFile,VirtualFile,PsiClass>
        (ideaModuleManager, ideaModuleSourceMapper, modules) {

    // TODO
    shared actual void addModuleToClasspathInternal(
        ArtifactResult? artifactResult) {}
    
    shared actual Boolean isOverloadingMethod(MethodMirror method) {
        assert(is PSIMethod method);
        return method.isOverloading;
    }
    
    shared actual Boolean isOverridingMethod(MethodMirror method) {
        assert(is PSIMethod method);
        return method.isOverriding;
    }
    
    // TODO
    shared actual Boolean loadPackage(Module? mod, String? name,
        Boolean boolean) => true;

    // TODO
    shared actual Boolean moduleContainsClass(BaseIdeModule ideModule,
        String packageName, String className) {
        
        assert(is IdeaModule ideModule);
        if (exists cp = ideModule.ceylonProject) {
            value p = cp.ideArtifact;
            value name = packageName + "." + className;
            value scope = p.getModuleScope(true);
            
            return JavaPsiFacade.getInstance(p.project).findClass(name, scope)
                exists;
        }
        
        return false;
    }
    
    shared actual ClassMirror? buildClassMirrorInternal(String name) {
        return doWithLock(() {
            if (exists m = ideaModuleManager.ceylonProject?.ideArtifact) { 
                value scope = m.getModuleWithDependenciesAndLibrariesScope(true);
                value facade = JavaPsiFacade.getInstance(m.project);
                
                if (exists psi = facade.findClass(name, scope)) {
                    return PSIClass(psi);
                }
            }
            return null;
        });
    }
    
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
}