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
    PsiNamedElement,
    PsiManager,
    PsiJavaFile,
    SmartPointerManager,
    PsiElement,
    LambdaUtil,
    PsiWildcardType,
    PsiType
}
import com.intellij.psi.impl.compiled {
    ClsFileImpl
}
import com.intellij.psi.impl.java.stubs.impl {
    PsiJavaFileStubImpl
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
    MethodMirror,
    FunctionalInterfaceType,
    TypeMirror
}
import com.redhat.ceylon.model.typechecker.model {
    Modules
}

import java.lang {
    ThreadLocal
}
import java.util {
    Arrays
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
                        dumbService(project).runReadActionInSmartMode(() {
                            value restoreCurrentPriority = withOriginalModelUpdatePriority();
                            try {
                                return concurrencyManager.outsideDumbMode(() {
                                    ref.set(action.call() else null);
                                });
                            } finally {
                                    restoreCurrentPriority();

                            }
                        });
                    } else {
                        application.runReadAction(() => ref.set(action.call() else null));
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
        if (exists strategy = concurrencyManager.noIndexStrategy,
            strategy == NoIndexStrategy.waitForIndexes) {
            resetJavaModelSourceIfNecessary(()
                => application.invokeAndWait(() {
                        assert (exists project =
                                ideaModuleManager.ceylonProject?.ideArtifact?.project);
                        dumbService(project).queueTask(UnindexedFilesUpdater(project, false));
                    },
                    ModalityState.nonModal));
        }
    }

    shared actual Boolean moduleContainsClass(BaseIdeModule ideModule,
        String packageName, String className) {
        
        assert (is IdeaModule ideModule);
        
        if (exists ceylonProject = ideModule.ceylonProject) {
            updateIndexIfnecessary();
            value ideaModule = ceylonProject.ideArtifact;
            value name = packageName + "." + className;
            value scope = ideaModule.getModuleScope(true);
            value facade = javaPsiFacade(ideaModule.project);
            return concurrencyManager.needIndexes(ideaModule.project,
                () => concurrencyManager.needReadAccess(()
                    => facade.findClass(name, scope) exists));
        }
        
        return false;
    }

    function pointer<Psi>(Psi el) given Psi satisfies PsiElement {
        assert (exists project = ideaModuleManager.ceylonProject?.ideArtifact?.project);
        return SmartPointerManager.getInstance(project).createSmartPsiElementPointer(el);
    }
    
    shared actual ClassMirror? buildClassMirrorInternal(String name) {
        assert(is IdeaCeylonProject ceylonProject = ideaModuleManager.ceylonProject);

        if (ceylonProject.validatingDependencies) {
            return findModuleDescriptorInArtifacts(ceylonProject, name);
        }

        value project = ceylonProject.ideArtifact.project;
        updateIndexIfnecessary();

        return concurrencyManager.needIndexes(project,
            () => concurrencyManager.needReadAccess(() {
                if (exists m = ideaModuleManager.ceylonProject?.ideArtifact) {
                    value scope = m.getModuleWithDependenciesAndLibrariesScope(true);
                    value facade = javaPsiFacade(m.project);

                    if (exists psi = facade.findClass(name, scope)) {
                        return PSIClass(pointer(psi));
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

                if (exists file = VirtualFileManager.instance.findFileByUrl(path)) {
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

        if (is PsiJavaFileStubImpl stub = ClsFileImpl.buildFileStub(file, file.contentsToByteArray()),
            stub.classes.size > 0,
            exists ideaProject = ideaModuleManager.ceylonProject?.ideArtifact?.project,
            is PsiJavaFile psiFile = PsiManager.getInstance(ideaProject).findFile(file)) {

            stub.psi = psiFile;
            value internalPsiClass = stub.classes.get(0);
            return PSIClass(pointer(internalPsiClass));
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
        
        packageMembers(String quotedPackageName) =>
            if (ideaModuleManager.isExternalModuleLoadedFromSource(ideModule.nameAsString))
            then null
            else concurrencyManager.needReadAccess(() {
                if (exists pkg = facade.findPackage(quotedPackageName)) {
                    value classes = pkg.getClasses(GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(mod));
                    
                    return {*classes};
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

    shared actual FunctionalInterfaceType? getFunctionalInterfaceType(TypeMirror typeMirror) {
        function noWildcard(PsiType type)
            => if (is PsiWildcardType type, exists bound = type.bound) then bound else type;

        if (is PSIType typeMirror,
            exists method = LambdaUtil.getFunctionalInterfaceMethod(typeMirror.psi),
            exists returnType = LambdaUtil.getFunctionalInterfaceReturnType(typeMirror.psi)) {

            value parameterTypes = Arrays.asList<TypeMirror>(
                for (idx in 0..method.parameterList.parametersCount)
                if (exists pType = LambdaUtil.getLambdaParameterFromType(typeMirror.psi, idx))
                PSIType(noWildcard(pType))
            );
            return FunctionalInterfaceType(PSIMethod(pointer(method)), PSIType(noWildcard(returnType)), parameterTypes, method.varArgs);
        }
        return super.getFunctionalInterfaceType(typeMirror);
    }

    isFunctionalInterface(ClassMirror klass)
            => if (is PSIClass klass,
                    exists method = LambdaUtil.getFunctionalInterfaceMethod(klass.psi),
                    !method.hasTypeParameters())
            then method.name
            else null;

    isFunctionalInterfaceType(TypeMirror typeMirror)
            => if (is PSIType typeMirror,
                    exists method = LambdaUtil.getFunctionalInterfaceMethod(typeMirror.psi),
                    !method.hasTypeParameters())
                then true
                else false;
}
