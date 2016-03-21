import com.intellij.openapi.application {
    ApplicationManager,
    ModalityState
}
import com.intellij.openapi.\imodule {
    IJModule=Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    JavaPsiFacade,
    PsiClass,
    PsiNamedElement
}
import com.intellij.psi.search {
    GlobalSearchScope
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
    Modules,
    Unit
}

import java.lang {
    Runnable
}

shared class IdeaModelLoader(IdeaModuleManager ideaModuleManager,
    IdeaModuleSourceMapper ideaModuleSourceMapper, Modules modules)
        extends IdeModelLoader<IJModule,VirtualFile,VirtualFile,VirtualFile,PsiClass,PsiClass>
        (ideaModuleManager, ideaModuleSourceMapper, modules) {

    shared actual void addModuleToClasspathInternal(ArtifactResult? artifact) {
        if (exists artifact,
            is IdeaCeylonProject project = ideaModuleManager.ceylonProject) {
            
            // TODO everytime we add a jar, it's indexed, which is slowing down
            // the process
            ApplicationManager.application.invokeAndWait(object satisfies Runnable {
                shared actual void run() {
                    project.addLibrary(artifact.artifact().absolutePath);
                }
            }, ModalityState.any());
        }
    }
    
    shared actual Boolean isOverloadingMethod(MethodMirror method) {
        assert(is PSIMethod method);
        return method.isOverloading;
    }
    
    shared actual Boolean isOverridingMethod(MethodMirror method) {
        assert(is PSIMethod method);
        return method.isOverriding;
    }

    // TODO something like ModelLoaderNameEnvironment
    shared actual Boolean moduleContainsClass(BaseIdeModule ideModule,
        String packageName, String className) {
        
        assert(is IdeaModule ideModule);
        
        if (exists cp = ideModule.ceylonProject) {
            value p = cp.ideArtifact;
            value name = packageName + "." + className;
            value scope = p.getModuleScope(true);
            PsiClass? cls = JavaPsiFacade.getInstance(p.project)
                    .findClass(name, scope);
            
            return cls exists;
        }
        
        return false;
    }
    
    shared actual ClassMirror? buildClassMirrorInternal(String name) {
        assert(exists project = ideaModuleManager.ceylonProject?.ideArtifact?.project);
        
        return doWithIndex(project, () {
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
    
    shared actual String typeName(PsiClass type) => (type of PsiNamedElement).name;

    shared actual class PackageLoader(BaseIdeModule ideModule)
             extends super.PackageLoader(ideModule) {
        
        assert(is IdeaModule ideModule);
        assert(exists mod = ideModule.ceylonProject?.ideArtifact);
        assert(exists project = mod.project);

        value facade = JavaPsiFacade.getInstance(project);
        
        shared actual Boolean packageExists(String quotedPackageName) 
            => doWithLock(() => facade.findPackage(quotedPackageName) exists);
        
        shared actual {PsiClass*}? packageMembers(String quotedPackageName)
            => doWithLock(() {
                if (exists pkg = facade.findPackage(quotedPackageName)) {
                    value classes = pkg.getClasses(GlobalSearchScope.moduleScope(mod));
                    
                    return classes.iterable.coalesced;
                }
                return null;
            });
        
        // TODO remove inner/anonymous classes?
        shared actual Boolean shouldBeOmitted(PsiClass type) => false;
    }

    shared actual Boolean typeExists(PsiClass type) => true;
}