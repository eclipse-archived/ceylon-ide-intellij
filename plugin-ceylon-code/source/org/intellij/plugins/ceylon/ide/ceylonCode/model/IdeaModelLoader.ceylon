import ceylon.interop.java {
    javaString
}

import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.\imodule {
    IJModule=Module
}
import com.intellij.openapi.roots {
    ModuleRootManager,
    OrderRootType
}
import com.intellij.openapi.roots.libraries {
    Library
}
import com.intellij.openapi.util {
    Disposer
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VirtualFileManager,
    JarFileSystem
}
import com.intellij.psi {
    JavaPsiFacade,
    PsiClass
}
import com.redhat.ceylon.ide.common.model {
    IdeModelLoader,
    BaseIdeModule
}
import com.redhat.ceylon.ide.common.util {
    synchronize
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

import java.lang {
    Runnable
}

shared class IdeaModelLoader(IdeaModuleManager ideaModuleManager,
    IdeaModuleSourceMapper ideaModuleSourceMapper, Modules modules)
        extends IdeModelLoader<IJModule,VirtualFile,VirtualFile,VirtualFile,PsiClass>
        (ideaModuleManager, ideaModuleSourceMapper, modules) {

    shared actual void addModuleToClasspathInternal(ArtifactResult? artifact) {
        if (exists artifact,
            exists vfile = VirtualFileManager.instance.findFileByUrl(
                VirtualFileManager.constructUrl(JarFileSystem.\iPROTOCOL, 
                    artifact.artifact().absolutePath) + JarFileSystem.\iJAR_SEPARATOR
            ),
            exists mod = ideaModuleManager.ceylonProject?.ideArtifact) {
            
            // TODO everytime we add a jar, it's indexed, which is slowing down
            // the process
            ApplicationManager.application.invokeLater(object satisfies Runnable {
                shared actual void run() {
                    value lock = ApplicationManager.application.acquireWriteActionLock(null);        
                    
                    try {
                        value model = ModuleRootManager.getInstance(mod).modifiableModel;
                        value lib = model.moduleLibraryTable.getLibraryByName("Ceylon Stuff")
                        else model.moduleLibraryTable.createLibrary("Ceylon Stuff");
                        
                        Library.ModifiableModel libModel = lib.modifiableModel;
                        
                        if (libModel.getUrls(OrderRootType.\iCLASSES).array.coalesced
                            .find((name) => name == vfile.path) exists) {
                            
                            Disposer.dispose(libModel);
                            model.dispose();
                        } else {
                            libModel.addRoot(vfile, OrderRootType.\iCLASSES);
                        
                            libModel.commit();
                            model.commit();
                        } 
                    } finally {
                        lock.finish();
                    }
                }
            });
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
    
    // TODO
    shared actual Boolean loadPackage(Module? mod, String? packageName, 
        Boolean loadDeclarations) {
        
        if (exists mod,
            exists packageName,
            exists key = cacheKeyByModule(mod, packageName)) {
            
            return synchronize(lock, () {
                if(loadDeclarations && !loadedPackages.add(javaString(key))) {
                    return true;
                }
                
                if (is IdeaModule mod) {
                    //mod.
                }
                
                return false;
            });
        }
        
        return false;
    }

    // TODO
    shared actual Boolean moduleContainsClass(BaseIdeModule ideModule,
        String packageName, String className) {
        
        assert(is IdeaModule ideModule);
        if (exists cp = ideModule.ceylonProject) {
            value p = cp.ideArtifact;
            value name = packageName + "." + className;
            value scope = p.getModuleScope(true);
            
            return JavaPsiFacade.getInstance(p.project).findClass(name.replace("$", "."), scope)
                exists;
        }
        
        return false;
    }
    
    shared actual ClassMirror? buildClassMirrorInternal(String name) {
        return doWithLock(() {
            if (exists m = ideaModuleManager.ceylonProject?.ideArtifact) { 
                value scope = m.getModuleWithDependenciesAndLibrariesScope(true);
                value facade = JavaPsiFacade.getInstance(m.project);
                
                if (exists psi = facade.findClass(name.replace("$", "."), scope)) {
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