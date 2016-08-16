import ceylon.interop.java {
    javaString
}
import ceylon.language {
    langNull=null,
    langTrue=true,
    langFalse=false
}

import com.intellij.codeInsight.completion {
    AllClassesGetter
}
import com.intellij.codeInsight.completion.impl {
    CamelHumpMatcher
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.\imodule.impl.scopes {
    ModuleWithDependenciesScope
}
import com.intellij.openapi.vfs {
    JarFileSystem,
    VirtualFile
}
import com.intellij.psi {
    PsiClassOwner,
    PsiModifier,
    PsiNamedElement,
    PsiClass,
    PsiModifierList,
    PsiType
}
import com.intellij.util {
    Processor
}
import com.redhat.ceylon.ide.common.completion {
    completionManager {
        Proposals
    }
}
import com.redhat.ceylon.ide.common.model.asjava {
    getJavaQualifiedName
}
import com.redhat.ceylon.model.loader {
    AbstractModelLoader {
        ceylonTypeAliasAnnotation,
        ceylonContainerAnnotation,
        ceylonIgnoreAnnotation,
        ceylonMethodAnnotation,
        ceylonAnnotationInstantiationAnnotation,
        ceylonObjectAnnotation,
        ceylonAttributeAnnotation
    }
}
import com.redhat.ceylon.model.typechecker.model {
    TypeParameter,
    DeclarationWithProximity,
    Function,
    Class,
    Type,
    Declaration,
    ParameterList,
    Interface,
    Unit,
    Package,
    Value,
    TypeAlias,
    ModelUtil,
    Module_=Module {
        getUnimportedProximity
    }
}

import java.io {
    File
}
import java.lang {
    JString=String
}
import java.util {
    HashMap,
    Collections {
        emptyList
    },
    JList=List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.compiled {
    classFileDecompilerUtil
}

shared interface FakeCompletionDeclaration {
    shared formal Declaration? realDeclaration;
    shared formal PsiClass psiClass;
}

Proposals scanJavaIndex(IdeaModule that, Unit sourceUnit,
        IdeaModuleManager moduleManager, Module mod,
        String startingWith, Integer proximity) {

    value noTypeParameters = emptyList<TypeParameter>();
    value noTypes = emptyList<Type>();

    value result = HashMap<JString,DeclarationWithProximity>();
    value allDependencies
            = [ for (m in that.transitiveDependencies)
                if (is IdeaModule m, exists a = m.artifact)
                a ];

    value ceylonDependency
            = if (is IdeaModule lang = that.languageModule)
            then lang.artifact
            else null;

    object processor satisfies Processor<PsiClass> {
        String? findName(PsiClass cls) {
            value defaultName = (cls of PsiNamedElement).name;

            if (exists defaultName,
                defaultName.endsWith("_"),
                classFileDecompilerUtil.isCeylonCompiledFile(cls.containingFile.virtualFile)) {

                return defaultName.removeTerminal("_");
            }

            return defaultName;
        }

        function findOrCreateDeclaration(PsiClass cls, PsiModifierList modifiers, Package pkg) {

            if (!pkg.shared) {
                return null;
            }
            if (exists qName = cls.qualifiedName) {
                for (imp in sourceUnit.imports) {
                    if (getJavaQualifiedName(imp.declaration) == qName) {
                        return null;
                    }
                }
            }
            if (modifiers.findAnnotation(ceylonContainerAnnotation) exists
                || modifiers.findAnnotation(ceylonIgnoreAnnotation) exists) {
                return null;
            }

            value clsName = findName(cls);
            Declaration lightModel;

            if (cls.\iinterface) {
                lightModel = object extends Interface() satisfies FakeCompletionDeclaration {
                    variable Interface? lazyRealIntf = langNull;

                    function computeRealIntf() {
                        if (is Interface decl
                                = pkg.getMember(clsName, noTypes, langFalse)) {
                            return decl;
                        }

                        return langNull;
                    }

                    value realIntf => lazyRealIntf else (lazyRealIntf = computeRealIntf());
                    realDeclaration => realIntf;

                    value hasTypeParams = cls.hasTypeParameters();

                    shared actual JList<TypeParameter> typeParameters
                            => if (hasTypeParams)
                            then (realIntf?.typeParameters else noTypeParameters)
                            else noTypeParameters;
                    assign typeParameters {}

                    type => langNull;

                    psiClass => cls;

                    members => realIntf?.members;
                };

            } else if (modifiers.findAnnotation(ceylonMethodAnnotation) exists) {
                lightModel = object extends Function() satisfies FakeCompletionDeclaration {
                    variable Function? lazyRealFunction = langNull;

                    function computeRealFunction() {
                        if (is Function decl
                                = pkg.getMember(clsName, noTypes, langFalse)) {
                            return decl;
                        }

                        return langNull;
                    }

                    value realFunction => lazyRealFunction else (lazyRealFunction = computeRealFunction());
                    realDeclaration => realFunction;

                    parameterLists => realFunction?.parameterLists else emptyList<ParameterList>();

                    shared actual Type? type => realFunction?.type;
                    assign type {}

                    annotation = modifiers.findAnnotation(ceylonAnnotationInstantiationAnnotation) exists;

                    psiClass => cls;

                    shared actual Boolean declaredVoid
                            => let (methods = cls.findMethodsByName(clsName, langFalse))
                            if (methods.size>0, //workaround for compiler bug #6421
                                exists method = methods[0],
                                exists type = method.returnType)
                            then type == PsiType.\ivoid
                            else langFalse;
                    assign declaredVoid {}

                };
            } else if (modifiers.findAnnotation(ceylonObjectAnnotation) exists
                        || modifiers.findAnnotation(ceylonAttributeAnnotation) exists) {
                lightModel = object extends Value() satisfies FakeCompletionDeclaration {
                    variable Value? lazyRealValue = langNull;

                    function computeRealValue() {
                        if (is Value decl
                                = pkg.getMember(clsName, noTypes, langFalse)) {
                            return decl;
                        }
                        return langNull;
                    }

                    value realValue => lazyRealValue else (lazyRealValue =computeRealValue());
                    realDeclaration => realValue;

                    shared actual Type? type => realValue?.type;
                    assign type {}

                    psiClass => cls;
                };
            } else if (modifiers.findAnnotation(ceylonTypeAliasAnnotation) exists) {
                lightModel = object extends TypeAlias() satisfies FakeCompletionDeclaration {
                    variable TypeAlias? lazyRealAlias = langNull;

                    function computeRealAlias() {
                        if (is TypeAlias decl
                                = pkg.getMember(clsName, noTypes, langFalse)) {
                            return decl;
                        }
                        return langNull;
                    }

                    value realAlias
                            => lazyRealAlias else (lazyRealAlias = computeRealAlias());
                    shared actual Declaration? realDeclaration => realAlias;

                    value hasTypeParams = cls.hasTypeParameters();

                    shared actual JList<TypeParameter> typeParameters
                            => if (hasTypeParams)
                            then (realAlias?.typeParameters else noTypeParameters)
                            else noTypeParameters;

                    assign typeParameters {}

                    psiClass => cls;
                };
            } else {
                lightModel = object extends Class() satisfies FakeCompletionDeclaration {
                    variable Class? lazyRealClass = langNull;

                    function computeRealClass() {
                        if (exists decl
                                = pkg.getMember(clsName, noTypes, langFalse)) {
                            if (is Class decl) {
                                if (ModelUtil.isOverloadedVersion(decl),
                                    is Class abst = decl.extendedType?.declaration) {
                                    return abst;
                                }
                                else {
                                    return decl;
                                }
                            } else {
                                print("Expected member of type Class but was ``className(decl)``");
                            }
                        }
                        return langNull;
                    }

                    value realClass => lazyRealClass else (lazyRealClass = computeRealClass());
                    realDeclaration => realClass;

                    parameterLists => realClass?.parameterLists else emptyList<ParameterList>();

                    value hasTypeParams = cls.hasTypeParameters();

                    shared actual JList<TypeParameter> typeParameters
                            => if (hasTypeParams)
                            then (realClass?.typeParameters else noTypeParameters)
                            else noTypeParameters;
                    assign typeParameters {}

                    abstraction
                            = !modifiers.findAnnotation(Annotations.ceylon.className) exists
                            && cls.constructors.size>1;

                    shared actual JList<Declaration>? overloads
                            => if (abstraction)
                            then (realClass?.overloads else emptyList<Declaration>())
                            else langNull;
                    assign overloads {}

                    type => realClass?.type;

                    abstract = modifiers.hasModifierProperty(PsiModifier.abstract);
                    final = modifiers.hasModifierProperty(PsiModifier.final);
                    annotation = modifiers.findAnnotation(Annotations.annotationType.className) exists;

                    psiClass => cls;

                    members => realClass?.members;
                };
            }

            lightModel.name = clsName;
            lightModel.container = pkg;
            lightModel.deprecated = modifiers.findAnnotation(Annotations.deprecated.className) exists;

            value unit = Unit();
            unit.\ipackage = pkg;
            lightModel.unit = unit;

            lightModel.shared = langTrue;

            return lightModel;
        }

        shared actual Boolean process(PsiClass cls) {
            if (exists modifiers = cls.modifierList,
                modifiers.hasExplicitModifier(PsiModifier.public),
                is PsiClassOwner file = cls.containingFile,
                exists pkg = moduleManager.modelLoader.findPackage(file.packageName),
                exists lightModel = findOrCreateDeclaration(cls, modifiers, pkg),
                exists qname = cls.qualifiedName) {
                result.put(javaString(qname),
                    DeclarationWithProximity(lightModel,
                        getUnimportedProximity(proximity, pkg.languagePackage, lightModel.name),
                        langTrue));
            }
            return langTrue;
        }
    }
    object scope extends ModuleWithDependenciesScope(mod, ModuleWithDependenciesScope.libraries) {
        shared actual Boolean contains(VirtualFile file) {
            if (exists jar = JarFileSystem.instance.getVirtualFileForJar(file)) {
                // skip inner and internal classes
                if ('$' in file.name) {
                    return false;
                }

                // automatically add language declarations
                value jarFile = File(jar.path);
                if (exists ceylonDependency, ceylonDependency == jarFile) {
                    return true;
                }

                // don't go further if the prefix is empty
                if (startingWith.empty) {
                    return false;
                }

                // accept dependencies of the current module
                if (jarFile in allDependencies) {
                    return true;
                }

                // check for Java modules and other dependencies that have no artifact
                value sep = file.path.indexOf(JarFileSystem.jarSeparator);
                if (sep>0) {
                    value entryPath = file.path.spanFrom(sep + JarFileSystem.jarSeparator.size);
                    if (exists sep2 = entryPath.lastIndexWhere('/'.equals)) {
                        value pkg = entryPath[...sep2-1].replace("/", ".");
                        if (moduleManager.modelLoader.findPackage(pkg) exists) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    value before = system.milliseconds;
    AllClassesGetter.processJavaClasses(object extends CamelHumpMatcher(startingWith) {
        prefixMatches(String name)
                => name[0..0]==startingWith[0..0]
                && super.prefixMatches(name);
    }, mod.project, scope, processor);
    print("processed Java index in ``system.milliseconds - before``ms => ``result.size()`` results");
    return result;
}