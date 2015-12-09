import com.redhat.ceylon.ide.common.model {
    IdeModelLoader
}
import com.redhat.ceylon.model.cmr {
    ArtifactResult
}
import com.redhat.ceylon.model.loader.mirror {
    ClassMirror,
    MethodMirror
}
import com.redhat.ceylon.model.loader.model {
    LazyFunction,
    AnnotationProxyClass,
    AnnotationProxyMethod
}
import com.redhat.ceylon.model.typechecker.model {
    Module,
    Parameter,
    UnknownType,
    Modules
}

import java.util {
    List
}

// TODO wait for IdeModelLoader to be finished
shared class IdeaModelLoader(IdeaModuleManager ideaModuleManager,
    IdeaModuleSourceMapper ideaModuleSourceMapper, Modules modules)
        extends IdeModelLoader() {
    
    shared actual void addJDKModuleToClassPath(Module jdkModule) {}
    
    shared actual void addModuleToClassPath(Module? \imodule, ArtifactResult? artifactResult) {}
    
    shared actual Module findModuleForClassMirror(ClassMirror? classMirror) => nothing;
    
    shared actual Boolean isOverloadingMethod(MethodMirror? methodMirror) => nothing;
    
    shared actual Boolean isOverridingMethod(MethodMirror? methodMirror) => nothing;
    
    shared actual Boolean loadPackage(Module? \imodule, String? string, Boolean boolean) => nothing;
    
    shared actual void logError(String? string) {}
    
    shared actual void logVerbose(String? string) {}
    
    shared actual void logWarning(String? string) {}
    
    shared actual ClassMirror lookupNewClassMirror(Module? \imodule, String? string) => nothing;
    
    shared actual void makeInteropAnnotationConstructorInvocation(AnnotationProxyMethod? annotationProxyMethod, AnnotationProxyClass? annotationProxyClass, List<Parameter>? list) {}
    
    shared actual UnknownType.ErrorReporter makeModelErrorReporter(Module? \imodule, String? string) => nothing;
    
    shared actual void setAnnotationConstructor(LazyFunction? lazyFunction, MethodMirror? methodMirror) {}    
}