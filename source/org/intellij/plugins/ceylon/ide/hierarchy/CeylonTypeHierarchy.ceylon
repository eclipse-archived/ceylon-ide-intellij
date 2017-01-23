import com.intellij.ide.hierarchy {
    HierarchyNodeDescriptor,
    HierarchyProvider,
    HierarchyBrowser,
    TypeHierarchyBrowserBase
}
import com.intellij.openapi.actionSystem {
    DataContext,
    CommonDataKeys
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiElement,
    PsiMethod
}
import com.redhat.ceylon.model.typechecker.model {
    ...
}

import java.lang {
    ObjectArray
}
import java.util {
    ...
}

import javax.swing {
    ...
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.resolve {
    resolveDeclaration
}

alias TypeDec
        => CeylonPsi.ClassOrInterfacePsi
         | CeylonPsi.ObjectDefinitionPsi
         | CeylonPsi.ObjectExpressionPsi
         | CeylonPsi.ObjectArgumentPsi;

class CeylonTypeHierarchyBrowser(Project project, PsiElement element)
        extends CeylonHierarchyBrowser(project, element) {

    isApplicableElement(PsiElement psiElement) => psiElement is TypeDec;

    function skipConstructor(TypeDeclaration extended)
            => if (is Constructor extended)
            then extended.extendedType.declaration
            else extended;

    function skipConstructorElement(PsiElement psiElement) {
        if (is PsiMethod psiElement) {
            assert (psiElement.constructor,
                exists cl = psiElement.containingClass);
            return cl;
        }
        else {
            return psiElement;
        }
    }
    shared actual CeylonHierarchyNodeDescriptor? build(PsiElement element, Declaration? dec) {
        if (is ClassOrInterface dec) {
            if (exists extendedType = dec.extendedType) {
                value extended = skipConstructor(extendedType.declaration);
                if (exists psiElement = resolveDeclaration(extended, project),
                    exists parentDescriptor = build(skipConstructorElement(psiElement), extended)) {
                    value nodeDescriptor
                            = CeylonHierarchyNodeDescriptor {
                                element = element;
                                model = dec;
                                parent = parentDescriptor;
                            };
                    parentDescriptor.children = ObjectArray(1, nodeDescriptor);
                    return nodeDescriptor;
                }
            }
            return CeylonHierarchyNodeDescriptor(element, dec);
        }
        else {
            return null;
        }
    }

    shared actual ObjectArray<CeylonHierarchyNodeDescriptor>
    aggregateSubtypes(CeylonHierarchyNodeDescriptor descriptor) {
        value result = ArrayList<CeylonHierarchyNodeDescriptor>();
        if (is ClassOrInterface model = descriptor.model, !model.final) {
            for (unit in phasedUnits) {
                for (declaration in unit.declarations) {
                    if (is ClassOrInterface declaration) {
                        switch (model)
                        case (is Class) {
                            if (exists extendedType = declaration.extendedType,
                                skipConstructor(extendedType.declaration) == model) {
                                if (exists psiElement = resolveDeclaration(declaration, project)) {
                                    result.add(CeylonHierarchyNodeDescriptor(psiElement, declaration, descriptor));
                                }
                            }
                        }
                        case (is Interface) {
                            for (satisfiedType in declaration.satisfiedTypes) {
                                if (satisfiedType.declaration == model) {
                                    if (exists psiElement = resolveDeclaration(declaration, project)) {
                                        result.add(CeylonHierarchyNodeDescriptor(psiElement, declaration, descriptor));
                                    }
                                }
                            }
                        }
                        else {
                            assert (false);
                        }
                    }
                }
            }
        }
        return result.toArray(noDescriptors);
    }

    shared actual ObjectArray<CeylonHierarchyNodeDescriptor> aggregateSupertypes(
            CeylonHierarchyNodeDescriptor descriptor) {
        value result = ArrayList<HierarchyNodeDescriptor>();
        if (is ClassOrInterface model = descriptor.model) {
            if (exists extendedType = model.extendedType) {
                value extended = skipConstructor(extendedType.declaration);
                if (exists psiElement = resolveDeclaration(extended, project)) {
                    result.add(CeylonHierarchyNodeDescriptor(skipConstructorElement(psiElement), extended, descriptor));
                }
            }
            for (type in model.satisfiedTypes) {
                value satisfied = type.declaration;
                if (exists psiElement = resolveDeclaration(satisfied, project)) {
                    result.add(CeylonHierarchyNodeDescriptor(psiElement, satisfied, descriptor));
                }
            }
        }
        return result.toArray(noDescriptors);
    }
}

shared class CeylonTypeHierarchyProvider() satisfies HierarchyProvider {

    shared actual void browserActivated(HierarchyBrowser hierarchyBrowser) {
        assert (is CeylonTypeHierarchyBrowser hierarchyBrowser);
        hierarchyBrowser.changeView(TypeHierarchyBrowserBase.typeHierarchyType);
    }

    shared actual PsiElement? getTarget(DataContext dataContext) {
        if (!CommonDataKeys.project.getData(dataContext) exists) {
            return null;
        }

        if (is TypeDec psi = CommonDataKeys.psiElement.getData(dataContext)) {
            return psi;
        }

        value caret = CommonDataKeys.caret.getData(dataContext);
        value file = CommonDataKeys.psiFile.getData(dataContext);
        if (exists caret, exists file) {
            variable value p = file.findElementAt(caret.offset);
            while (exists ep = p, !p is TypeDec) {
                p = ep.parent;
            }
            return p;
        }

        return null;
    }

    createHierarchyBrowser(PsiElement psiElement)
            => CeylonTypeHierarchyBrowser(psiElement.project, psiElement);
}
