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
import com.intellij.openapi.util {
    Ref
}
import com.intellij.openapi.vfs {
    VirtualFile,
    JarFileSystem,
    VirtualFileManager
}
import com.intellij.psi {
    JavaPsiFacade {
        javaPsiFacade=getInstance
    },
    PsiClass,
    PsiNamedElement
}
import com.intellij.psi.impl.compiled {
    ClsFileImpl
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
import com.redhat.ceylon.ide.common.platform {
    Status
}
import com.redhat.ceylon.model.cmr {
    ArtifactResult
}
import com.redhat.ceylon.model.loader.mirror {
    ClassMirror,
    MethodMirror
}
import com.redhat.ceylon.model.typechecker.model {
    Modules
}

import java.lang {
    ThreadLocal
}
import java.util.concurrent {
    Callable
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    ideaPlatformUtils
}

shared class IdeaModelLoader(IdeaModuleManager ideaModuleManager,
    IdeaModuleSourceMapper ideaModuleSourceMapper, Modules modules)
        extends IdeModelLoader<IJModule,VirtualFile,VirtualFile,VirtualFile,PsiClass,PsiClass>
        (ideaModuleManager, ideaModuleSourceMapper, modules) {

    value isSynchronizing = object extends ThreadLocal<Boolean>() {
        initialValue() => false;
    };
    
    shared actual T? embeddingSync<T>(Callable<T> action)
            given T satisfies Object {
        value ref = Ref<T?>();
        ref.set(null);
        Boolean synchronizing = isSynchronizing.get();
        if (! synchronizing) {
            isSynchronizing.set(true);
            try {
                if (application.readAccessAllowed) {
                    ref.set(action.call() else null);
                } else {
                    if (exists currentStrategy = concurrencyManager.noIndexStrategy,
                        currentStrategy == NoIndexStrategy.waitForIndexes) {
                        updateIndexIfnecessary();
                        assert(exists project = ideaModuleManager.ceylonProject?.ideArtifact?.project);
                        dumbService(project).runReadActionInSmartMode(JavaRunnable(()
                            => concurrencyManager.outsideDumbMode(() {
                                ref.set(action.call() else null);
                        })));
                    } else {
                        application.runReadAction(JavaRunnable(() {
                            ref.set(action.call() else null);
                        }));
                    }
                }
            } finally {
                isSynchronizing.set(false);
            }
        } else {
            ref.set(action.call() else null);
        }
        return  ref.get();
    }

    shared actual void addModuleToClasspathInternal(ArtifactResult? artifact) {
        if (exists artifact,
            is IdeaCeylonProject project = ideaModuleManager.ceylonProject) {

            print("Add module ``artifact.name()``");
            project.addDependencyToClasspath(artifact);
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
                }, ModalityState.nonModal);
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
            return concurrencyManager.needIndexes(ideaModule.project, () => concurrencyManager.needReadAccess(() 
                => facade.findClass(name, scope) exists));
        }
        
        return false;
    }
    
    shared actual ClassMirror? buildClassMirrorInternal(String name) {
        assert(is IdeaCeylonProject ceylonProject = ideaModuleManager.ceylonProject);

        if (ceylonProject.validatingDependencies) {
            return findModuleDescriptorInArtifacts(ceylonProject, name);
        }

        value project = ceylonProject.ideArtifact.project;
        updateIndexIfnecessary();

        return concurrencyManager.needIndexes(project, () => concurrencyManager.needReadAccess(() {
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

    "Reads a module descriptor from bytecode instead of adding its containing artifact to
     the classpath and wait for IntelliJ to finish indexing it."
    ClassMirror? findModuleDescriptorInArtifacts(IdeaCeylonProject project, String name) {
        if (name.endsWith(".$module_") || name.endsWith(".module_")) {
            value entryPath = JarFileSystem.jarSeparator + name.replace(".", "/") + ".class";

            for (dep in project.dependenciesToAdd) {
                value path = JarFileSystem.protocolPrefix + dep.artifact().absolutePath + entryPath;

                if (exists file = VirtualFileManager.instance.refreshAndFindFileByUrl(path)) {
                    return buildClassMirrorFromBytecode(file);
                }
            }
        } else {
            ideaPlatformUtils.log(Status._WARNING,
                "Unexpected class name '``name``' while trying to read module descriptor from bytecode");
        }

        return null;
    }

    ClassMirror? buildClassMirrorFromBytecode(VirtualFile file) {

        if (exists stub = ClsFileImpl.buildFileStub(file, file.contentsToByteArray()),
            stub.classes.size > 0) {

            return PSIClass(stub.classes.get(0));
        }

        return null;
    }

    shared actual String typeName(PsiClass type)
            => (type of PsiNamedElement).name else "<unknown>";

    shared actual class PackageLoader(BaseIdeModule ideModule)
             extends super.PackageLoader(ideModule) {
        
        assert(is IdeaModule ideModule);
        assert(exists mod = ideModule.ceylonProject?.ideArtifact);
        value project = mod.project;

        value facade = javaPsiFacade(project);
        
        shared actual Boolean packageExists(String quotedPackageName) 
            => concurrencyManager.needReadAccess(() => facade.findPackage(quotedPackageName) exists);
        
        shared actual {PsiClass*}? packageMembers(String quotedPackageName)
            => concurrencyManager.needReadAccess(() {
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