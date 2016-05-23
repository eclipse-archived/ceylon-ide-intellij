import ceylon.interop.java {
    JavaRunnable
}

import com.intellij.openapi.application {
    ApplicationManager {
        application
    },
    ModalityState
}
import com.intellij.openapi.\imodule {
    IJModule=Module
}
import com.intellij.openapi.project {
    DumbService {
        dumbService=getInstance
    }
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    JavaPsiFacade {
        javaPsiFacade=getInstance
    },
    PsiClass,
    PsiNamedElement
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.intellij.util.indexing {
    UnindexedFilesUpdater
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
            application.invokeAndWait(object satisfies Runnable {
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


    void updateIndexIfnecessary() {
        if (application.readAccessAllowed) {
            // We are in a UI or command action, 
            // and starting the index task (and waiting for its end) 
            // could produce a deadlock
            // The only place where the indexes should be up-to-date 
            // is the central model update that should never hold a 
            // read lock
            return;
        }
        resetJavaModelSourceIfNecessary(JavaRunnable { 
                run() => application.invokeAndWait(JavaRunnable { 
                    void run() { 
                        assert (exists project = 
                            ideaModuleManager.ceylonProject?.ideArtifact?.project);
                        dumbService(project).queueTask(UnindexedFilesUpdater(project, false));
                    }
                }, ModalityState.\iNON_MODAL);
        });
    }

    shared actual Boolean moduleContainsClass(BaseIdeModule ideModule,
        String packageName, String className) {
        
        assert(is IdeaModule ideModule);
        
        if (exists ceylonProject = ideModule.ceylonProject) {
            updateIndexIfnecessary();
            value ideaModule = ceylonProject.ideArtifact;
            value name = packageName + "." + className;
            value scope = ideaModule.getModuleScope(true);
            value facade = javaPsiFacade(ideaModule.project);
            return concurrencyManager.needIndexes(ideaModule.project, () => doWithLock(() 
                => facade.findClass(name, scope) exists));
        }
        
        return false;
    }
    
    shared actual ClassMirror? buildClassMirrorInternal(String name) {
        assert(exists project = ideaModuleManager.ceylonProject?.ideArtifact?.project);
        
        updateIndexIfnecessary();
        return concurrencyManager.needIndexes(project, () => doWithLock(() {
            if (exists m = ideaModuleManager.ceylonProject?.ideArtifact) { 
                value scope = m.getModuleWithDependenciesAndLibrariesScope(true);
                value facade = javaPsiFacade(m.project);
                
                if (exists psi = facade.findClass(name, scope)) {
                    return PSIClass(psi);
                }
            }
            return null;
        }));
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

        value facade = javaPsiFacade(project);
        
        shared actual Boolean packageExists(String quotedPackageName) 
            => doWithLock(() => facade.findPackage(quotedPackageName) exists);
        
        shared actual {PsiClass*}? packageMembers(String quotedPackageName)
            => doWithLock(() {
                if (exists pkg = facade.findPackage(quotedPackageName)) {
                    value classes = pkg.getClasses(GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(mod));
                    
                    return classes.iterable.coalesced;
                }
                return null;
            });
        
        // TODO remove inner/anonymous classes?
        shared actual Boolean shouldBeOmitted(PsiClass type) => false;
    }

    shared actual Boolean typeExists(PsiClass type) => true;

    shared actual String? alternateJdkModuleSpec
        => if (is IdeaCeylonProject project = ideaModuleManager.ceylonProject, project.isAndroid)
           then project.configuration.projectJdkProvider
           else null;
}