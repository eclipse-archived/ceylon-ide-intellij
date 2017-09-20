import java.lang {
    Types {
        nativeString
    },
    overloaded
}

import com.intellij.ide.projectView {
    PresentationData,
    ProjectViewNode,
    ProjectViewNodeDecorator
}
import com.intellij.ide.projectView.impl.nodes {
    ClassTreeNode,
    NamedLibraryElementNode,
    PsiDirectoryNode,
    PsiFileNode
}
import com.intellij.navigation {
    ColoredItemPresentation,
    ItemPresentation,
    ItemPresentationProvider
}
import com.intellij.openapi.editor.colors {
    TextAttributesKey,
    CodeInsightColors {
        deprecatedAttributes
    }
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.packageDependencies.ui {
    PackageDependenciesNode
}
import com.intellij.psi {
    PsiClass,
    PsiMethod,
    PsiNameIdentifierOwner,
    PsiModifierListOwner,
    PsiAnnotation,
    PsiLiteralExpression,
    PsiNamedElement,
    JavaDirectoryService,
    PsiType,
    PsiParameter,
    PsiModifier,
    PsiElement
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl,
    ClsMethodImpl,
    ClsFieldImpl
}
import com.intellij.psi.presentation.java {
    ClassPresentationProvider,
    MethodPresentationProvider,
    FieldPresentationProvider
}
import com.intellij.ui {
    ColoredTreeCellRenderer
}

import java.util {
    Arrays,
    Comparator
}

import org.intellij.plugins.ceylon.ide.compiled {
    classFileDecompilerUtil
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

String metaAnnotationName(String ann)
        => "com.redhat.ceylon.compiler.java.metadata." + ann;

Boolean hasAnnotation(PsiModifierListOwner owner, String ann)
        => owner.modifierList?.findAnnotation(metaAnnotationName(ann)) exists;

PsiAnnotation? getAnnotation(PsiModifierListOwner owner, String ann)
        => owner.modifierList?.findAnnotation(metaAnnotationName(ann));

String getName(PsiNameIdentifierOwner named, PsiModifierListOwner annotated)
        => if (exists ann = getAnnotation(annotated, "Name"))
        then nameValue(ann)
        else name(named).removeTerminal("_");

String nameValue(PsiAnnotation nameAnnotation) {
    assert (is PsiLiteralExpression lit
            = nameAnnotation.findAttributeValue(
                PsiAnnotation.defaultReferencedMethodName),
            exists val = lit.\ivalue);
    return val.string;
}

String getClassName(PsiClass clsClass) {
    String name
            = getName(clsClass, clsClass)
            .removeTerminal("$impl")
            .removeTerminal("$annotation$");
    return
        if (exists psiClass = clsClass.containingClass)
        then getClassName(psiClass) + "." + name
        else name;
}

shared class CeylonClassDecorator()
        satisfies ProjectViewNodeDecorator
                & ItemPresentationProvider<ClsClassImpl> {

    function getPresentableText(ClsClassImpl clsClass) {
        if (hasAnnotation(clsClass, "Ceylon")
            || clsClass.name.endsWith("$impl")
            || clsClass.name.endsWith("$annotation$")) {

            if (hasAnnotation(clsClass, "Module")) {
                return "module.ceylon";
            }
            else if (hasAnnotation(clsClass, "Package")) {
                return "package.ceylon";
            }
            else if (hasAnnotation(clsClass, "Method")) {
                String name = clsClass.name.removeTerminal("_");
                variable value methods = clsClass.findMethodsByName(name, false);
                if (methods.size == 0) {
                    methods = clsClass.findMethodsByName("$" + name, false);
                }
                Arrays.sort(methods, object satisfies Comparator<PsiMethod> {
                    compare(PsiMethod x, PsiMethod y)
                            => y.parameterList.parametersCount
                             - x.parameterList.parametersCount;
                    equals(Object that) => (super of Identifiable).equals(that);
                });
                PsiMethod clsMethod = methods.get(0);
                return name + "(``parameters(clsMethod)``)";
            }
//            else if (hasAnnotation(clsClass, "Object")) {
//                return getClassName(clsClass);
//            }
//            else if (hasAnnotation(clsClass, "Attribute")) {
//                return getClassName(clsClass);
//            }
            else {
                return getClassName(clsClass);
            }
        }
        return null;
    }

    overloaded
    shared actual void decorate(ProjectViewNode<out Object> node, PresentationData data) {
        if (is ClassTreeNode node) {
            if (is ClsClassImpl psiClass = node.psiClass,
                exists virtualFile = psiClass.containingFile.virtualFile,
                classFileDecompilerUtil.hasValidCeylonBinaryData(virtualFile)) {
                if (exists presentableText = getPresentableText(psiClass)) {
                    data.presentableText = presentableText;
                }
                if (psiClass.deprecated) {
                    data.setAttributesKey(deprecatedAttributes);
                }
                data.setIcon(icons.forClass(psiClass));
            }
            return;
        }

        if (is PsiDirectoryNode node) {
            for (child in node.children) {
                if (is PsiFileNode child,
                    exists file = child.virtualFile,
                    file.name == "module.ceylon"
                 || file.name == "package.ceylon") {
                    data.setIcon(icons.packageFolders);
                    break;
                }
            }
        }

        if (is NamedLibraryElementNode parentDescriptor = node.parentDescriptor,
            parentDescriptor.name.startsWith("Ceylon: ")) {
            parentDescriptor.icon = icons.moduleArchives;
        }
    }

    overloaded
    shared actual void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {}

    shared actual ItemPresentation getPresentation(ClsClassImpl item) {
        VirtualFile? virtualFile = item.containingFile.virtualFile;
        if (classFileDecompilerUtil.hasValidCeylonBinaryData(virtualFile)
            || item.name.endsWith("$impl")
            || item.containingClass exists
            && name(item.containingClass).endsWith("$impl")) {

            if (exists text = getPresentableText(item)) {
                return object satisfies ColoredItemPresentation {

                    shared actual TextAttributesKey? textAttributesKey {
                        if (item.deprecated) {
                            return deprecatedAttributes;
                        }
                        String name = item.name;
                        if (name.endsWith("_")) {
                            String realName = name.removeTerminal("_");
                            for (method in item.findMethodsByName(realName, false)) {
                                if (method.deprecated) {
                                    return deprecatedAttributes;
                                }
                            }
                            for (method in item.findMethodsByName("get_", false)) {
                                if (method.deprecated) {
                                    return deprecatedAttributes;
                                }
                            }
                        }
                        return null;
                    }

                    presentableText => text;
                    locationString => location(item);
                    getIcon(Boolean unused) => icons.forClass(item);
                };
            }
        }
        return ClassPresentationProvider().getPresentation(item);
    }
}

String? location(PsiElement psi) {
    value container
            = if (exists dir = psi.containingFile?.containingDirectory)
            then JavaDirectoryService.instance.getPackage(dir)
            else null;
    return if (exists name = container?.qualifiedName)
        then "(``name``)"
        else null;
}

Boolean isSetter(ClsMethodImpl clsMethod) {
    String name = clsMethod.name;
    return name.longerThan(3)
        && name.startsWith("set")
        && clsMethod.parameterList.parametersCount == 1;
}

Boolean isGetter(ClsMethodImpl clsMethod) {
    String name = clsMethod.name;
    return name.longerThan(3)
        && name.startsWith("get")
        && clsMethod.parameterList.parametersCount == 0;
}

String valueName(String name) => name[3:1].lowercased + name[4...];

String getMethodName(ClsMethodImpl clsMethod) {
    String name = getName(clsMethod, clsMethod);
    return isGetter(clsMethod) || isSetter(clsMethod)
        then valueName(name)
        else name + "(``parameters(clsMethod)``)";
}

String getFieldName(ClsFieldImpl clsField)
        => getName(clsField, clsField);

String parameters(PsiMethod clsMethod) {
    value params = StringBuilder();
    for (param in clsMethod.parameterList.parameters) {
        if (!isTypeDescriptor(param), !isSelfParameter(clsMethod, param)) {
            if (!params.empty) {
                params.append(", ");
            }
            if (exists tann = getAnnotation(param,"TypeInfo")) {
                params.append(getAnnotatedType(param, tann));
            } else {
                params.append(getCeylonType(param));
            }
            params.appendCharacter(' ');

            if (exists pann = getAnnotation(param, "Name")) {
                params.append(nameValue(pann));
            } else {
                params.append(name(param));
            }
        }
    }
    return params.string;
}

String getAnnotatedType(PsiParameter param, PsiAnnotation tann) {
    String type =
            nativeString(nameValue(tann))
                .replaceAll("[a-z]\\w*(\\.[a-z]\\w*)*::", "");
    if (hasAnnotation(param,"Sequenced")) {
        return type.endsWith("[]")
            then type.removeTerminal("[]") + "*"
            else type.removeInitial("[").removeTerminal("]");
    }
    else {
        return type;
    }
}

Boolean isSelfParameter(PsiMethod clsMethod, PsiParameter param) {
    if (clsMethod.modifierList.hasExplicitModifier(PsiModifier.static)) {
        PsiType type = param.type;
        switch (clsMethod.containingClass?.qualifiedName)
        case ("ceylon.language.String") {
            return "java.lang.String" == type.canonicalText;
        }
        case ("ceylon.language.Integer") {
            return PsiType.long==type;
        }
        case ("ceylon.language.Float") {
            return PsiType.double==type;
        }
        case ("ceylon.language.Byte") {
            return PsiType.byte==type;
        }
        else {
            return false;
        }
    } else {
        return false;
    }
}

String name(PsiNamedElement pne) {
    assert (exists name = pne.name);
    return name;
}

Boolean isTypeDescriptor(PsiParameter param)
        => name(param).startsWith("$reified$")
        || param.type.canonicalText
            == "com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor";

String getCeylonType(PsiParameter param) {
    PsiType pt = param.type;
    if (PsiType.boolean==pt) {
        return "Boolean";
    } else if (PsiType.long==pt) {
        return "Integer";
    } else if (PsiType.int==pt) {
        return "Character";
    } else if (PsiType.byte==pt) {
        return "Byte";
    } else if (PsiType.double==pt || PsiType.float==pt) {
        return "Float";
    } else if (pt.canonicalText.startsWith("ceylon.language.Callable")) {
        return "Callable";
    } else {
        return pt.presentableText
            .replace("? extends ", "")
            .replace("? super ", "");
    }
}

shared class CeylonMethodDecorator()
        satisfies ItemPresentationProvider<ClsMethodImpl> {

    function getPresentableText(ClsMethodImpl clsMethod) {
        PsiClass clsClass = clsMethod.containingClass;
        if (hasAnnotation(clsClass, "Method")
         || hasAnnotation(clsClass, "Attribute")) {
            return getMethodName(clsMethod);
        }
        if (hasAnnotation(clsClass, "Ceylon")
         || name(clsClass).endsWith("$impl")) {
            return getClassName(clsClass) + "."
                + getMethodName(clsMethod);
        }
        return null;
    }

    shared actual ItemPresentation getPresentation(ClsMethodImpl item) {
        VirtualFile? virtualFile = item.containingFile.virtualFile;
        if (classFileDecompilerUtil.hasValidCeylonBinaryData(virtualFile)
            || name(item.containingClass).endsWith("$impl")) {

            if (exists text = getPresentableText(item)) {
                return object satisfies ColoredItemPresentation {
                    textAttributesKey => item.deprecated then deprecatedAttributes;
                    presentableText => text;
                    locationString => location(item);
                    getIcon(Boolean unused) => icons.forMethod(item);
                };
            }
        }
        return MethodPresentationProvider().getPresentation(item);
    }
}

shared class CeylonFieldDecorator()
        satisfies ItemPresentationProvider<ClsFieldImpl> {

    function getPresentableText(ClsFieldImpl clsField) {
        PsiClass clsClass = clsField.containingClass;
        if (hasAnnotation(clsClass, "Attribute")) {
            return getFieldName(clsField);
        }
        if (hasAnnotation(clsClass, "Ceylon")
         || name(clsClass).endsWith("$impl")) {
            return getClassName(clsClass) + "."
                + getFieldName(clsField);
        }
        return null;
    }

    shared actual ItemPresentation getPresentation(ClsFieldImpl item) {
        VirtualFile? virtualFile = item.containingFile.virtualFile;
        if (classFileDecompilerUtil.hasValidCeylonBinaryData(virtualFile)
            || name(item.containingClass).endsWith("$impl")) {

            if (exists text = getPresentableText(item)) {
                return object satisfies ColoredItemPresentation {
                    textAttributesKey => item.deprecated then deprecatedAttributes;
                    presentableText => text;
                    locationString => location(item);
                    getIcon(Boolean unused) => icons.forField(item);
                };
            }
        }
        return FieldPresentationProvider().getPresentation(item);
    }
}
