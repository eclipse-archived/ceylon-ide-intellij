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
    },
    Project
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
    PsiType,
    PsiModifier,
    PsiMethod
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
    ThreadLocal,
    JBoolean=Boolean,
    Types
}
import java.lang.reflect {
    Modifier
}
import java.util {
    Arrays
}
import java.util.concurrent {
    Callable
}

import org.intellij.plugins.ceylon.ide.platform {
    ideaPlatformUtils
}
import com.intellij.psi.search.searches {
    SuperMethodsSearch
}
import com.redhat.ceylon.model.loader {
    AbstractModelLoader
}
import com.intellij.psi.util {
    MethodSignatureUtil
}
import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        outsideDumbMode,
        dontCancel,
        needReadAccess,
        needIndexes,
        noIndexStrategy
    }
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
                    ref.set(action.call());
                } else {
                    if (exists currentStrategy = noIndexStrategy,
                        currentStrategy == NoIndexStrategy.waitForIndexes) {
                        updateIndexIfnecessary();
                        assert(exists project = ideaModuleManager.ceylonProject?.ideArtifact?.project);
                        dumbService(project).runReadActionInSmartMode(() {
                            value restoreCurrentPriority = withOriginalModelUpdatePriority();
                            try {
                                return outsideDumbMode(() {
                                    ref.set(action.call());
                                });
                            } finally {
                                restoreCurrentPriority();
                            }
                        });
                    } else {
                        application.runReadAction(() => ref.set(action.call()));
                    }
                }
            } finally {
                isSynchronizing.set(false);
            }
        } else {
            ref.set(action.call());
        }
        return  ref.get();
    }

    shared actual void addModuleToClasspathInternal(ArtifactResult? artifact) {
        if (exists artifact,
            is IdeaCeylonProject project = ideaModuleManager.ceylonProject) {

            project.addDependencyToClasspath(artifact);
        }
    }

    Boolean nameEquals(PsiClass clazz, String className)
            => clazz.qualifiedName?.equals(className) else false;

    shared actual Boolean isOverridingMethod(MethodMirror method) {
        assert (is PSIMethod method);
        return method.withContainingClass((clazz) {
            if (nameEquals(clazz, "ceylon.language.Object")) {
                return false;
            }
            if (nameEquals(clazz,"ceylon.language.Identifiable")) {
                return true;
            }
            return null;
        }, null)
        else needIndexes(method.psi.project,
            () => dontCancel(
                () => SuperMethodsSearch.search(method.psi, null, true, false)
                        .findFirst() exists));
    }

    shared actual Boolean isOverloadingMethod(MethodMirror method) {
        assert (is PSIMethod method);
        return method.withContainingClass((clazz) {
            if (nameEquals(clazz, "ceylon.language.Exception")
                || nameEquals(clazz, "ceylon.language.Object")
                || nameEquals(clazz,"ceylon.language.Identifiable")) {
                return false;
            }
            value psiMethod = method.psi;
            for (method in clazz.findMethodsByName(psiMethod.name, true)) {
                if (method != psiMethod
                        && isOverloadedVersion(method, psiMethod)) {
                    return true;
                }
            }
            else {
                return false;
            }
        }, false);
    }

    Boolean isOverloadedVersion(PsiMethod method, PsiMethod mine)
            => !method.modifierList.hasModifierProperty(PsiModifier.private)
//           && !m.modifierList.hasModifierProperty(PsiModifier.static)
            && !method.modifierList.findAnnotation(AbstractModelLoader.ceylonIgnoreAnnotation) exists
            && !MethodSignatureUtil.isSuperMethod(method, mine);

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
        if (exists strategy = noIndexStrategy,
            strategy == NoIndexStrategy.waitForIndexes) {
            resetJavaModelSourceIfNecessary(() =>
                    application.invokeAndWait(() {
                        assert (exists ceylonProject = ideaModuleManager.ceylonProject);
                        value project = ceylonProject.ideArtifact.project;
                        dumbService(project).queueTask(newUnindexedFilesUpdater(project));
                    },
                    ModalityState.nonModal));
        }
    }

    UnindexedFilesUpdater newUnindexedFilesUpdater(Project project) {
        for (ctor in Types.classForType<UnindexedFilesUpdater>().constructors) {
            if (Modifier.isPublic(ctor.modifiers)) {
                if (ctor.parameterCount == 1,
                    is UnindexedFilesUpdater up = ctor.newInstance(project)) {
                    return up;
                } else if (ctor.parameterCount == 2,
                    is UnindexedFilesUpdater up = ctor.newInstance(project, JBoolean.false)) {
                    return up;
                }
            }
        }

        throw Exception("Could not instantiate UnindexedFilesUpdater");
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
            if (ideaModule.project.isDisposed()) {
                return false;
            }
            return needIndexes(ideaModule.project,
                () => needReadAccess(()
                    => facade.findClass(name, scope) exists));
        }
        
        return false;
    }

    function pointer<Psi>(Psi el) given Psi satisfies PsiElement {
        assert (exists ceylonProject = ideaModuleManager.ceylonProject);
        value project = ceylonProject.ideArtifact.project;
        return SmartPointerManager.getInstance(project)
            .createSmartPsiElementPointer(el);
    }
    
    shared actual ClassMirror? buildClassMirrorInternal(String name) {
        assert (is IdeaCeylonProject ceylonProject = ideaModuleManager.ceylonProject);

        if (ceylonProject.validatingDependencies) {
            return findModuleDescriptorInArtifacts(ceylonProject, name);
        }

        value project = ceylonProject.ideArtifact.project;
        updateIndexIfnecessary();

        return needIndexes(project,
            () => needReadAccess(() {
                if (exists ceylonProject = ideaModuleManager.ceylonProject) {
                    value artifact = ceylonProject.ideArtifact;
                    if (!artifact.isDisposed()) {
                        value scope = artifact.getModuleWithDependenciesAndLibrariesScope(true);
                        value facade = javaPsiFacade(artifact.project);

                        if (exists psi = facade.findClass(name, scope)) {
                            return PSIClass(pointer(psi));
                        }
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
            exists ceylonProject = ideaModuleManager.ceylonProject) {
            value ideaProject = ceylonProject.ideArtifact.project;
            if (is PsiJavaFile psiFile = PsiManager.getInstance(ideaProject).findFile(file)) {
                stub.psi = psiFile;
                value internalPsiClass = stub.classes.get(0);
                return PSIClass(pointer(internalPsiClass));
            }
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
            => needReadAccess(() => if (project.isDisposed()) then false else facade.findPackage(quotedPackageName) exists);
        
        packageMembers(String quotedPackageName) =>
            if (ideaModuleManager.isExternalModuleLoadedFromSource(ideModule.nameAsString))
            then null
            else needReadAccess(() {
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

    getFunctionalInterfaceType(TypeMirror typeMirror) =>
        dontCancel(() {
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
        });

    isFunctionalInterface(ClassMirror klass) =>
            dontCancel(() {
                try {
                    if (is PSIClass klass,
                        exists method = LambdaUtil.getFunctionalInterfaceMethod(klass.psi),
                        !method.hasTypeParameters()) {
                        return method.name;
                    }
                }
                catch (PsiElementGoneException e) {}
                return null;
            });

    isFunctionalInterfaceType(TypeMirror typeMirror) =>
            dontCancel(() =>
                if (is PSIType typeMirror,
                    exists method = LambdaUtil.getFunctionalInterfaceMethod(typeMirror.psi),
                    !method.hasTypeParameters())
                then true
                else false
            );

    isValidMirror(ClassMirror mirror) =>
            if (is PSIClass mirror)
            then mirror.valid
            else super.isValidMirror(mirror);
}
