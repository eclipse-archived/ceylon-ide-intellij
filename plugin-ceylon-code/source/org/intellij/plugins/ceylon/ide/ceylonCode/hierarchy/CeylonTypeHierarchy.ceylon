import ceylon.interop.java {
    createJavaObjectArray
}

import com.intellij.ide.hierarchy {
    HierarchyNodeDescriptor,
    HierarchyProvider,
    HierarchyBrowser
}
import com.intellij.openapi.actionSystem {
    DataContext,
    CommonDataKeys
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiElement
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

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
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

    shared actual CeylonHierarchyNodeDescriptor? build(PsiElement element, Declaration? dec) {
        if (is ClassOrInterface dec) {
            if (exists extendedType = dec.extendedType) {
                value extended = extendedType.declaration;
                if (exists psiElement = resolveDeclaration(extended, project)) {
                    if (exists parentDescriptor = build(psiElement, extended)) {
                        value nodeDescriptor
                                = CeylonHierarchyNodeDescriptor(element, dec, parentDescriptor);
                        parentDescriptor.children = createJavaObjectArray { nodeDescriptor };
                        return nodeDescriptor;
                    }
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
                        if (is Class model) {
                            if (exists extendedType = declaration.extendedType,
                                extendedType.declaration == model) {
                                if (exists psiElement = resolveDeclaration(declaration, project)) {
                                    result.add(CeylonHierarchyNodeDescriptor(psiElement, declaration, descriptor));
                                }
                            }
                        }
                        if (is Interface model) {
                            for (Type? satisfiedType in declaration.satisfiedTypes) {
                                if (exists satisfiedType,
                                    satisfiedType.declaration == model) {
                                    if (exists psiElement = resolveDeclaration(declaration, project)) {
                                        result.add(CeylonHierarchyNodeDescriptor(psiElement, declaration, descriptor));
                                    }
                                }
                            }
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
            if (exists cl = model.extendedType) {
                TypeDeclaration td = cl.declaration;
                if (exists psiElement = resolveDeclaration(td, project)) {
                    result.add(CeylonHierarchyNodeDescriptor(psiElement, td, descriptor));
                }
            }
            for (type in model.satisfiedTypes) {
                TypeDeclaration td = type.declaration;
                if (exists psiElement = resolveDeclaration(td, project)) {
                    result.add(CeylonHierarchyNodeDescriptor(psiElement, td, descriptor));
                }
            }
        }
        return result.toArray(noDescriptors);
    }
}

shared class CeylonTypeHierarchyProvider() satisfies HierarchyProvider {

    shared actual void browserActivated(HierarchyBrowser hierarchyBrowser) {
        assert (is CeylonTypeHierarchyBrowser hierarchyBrowser);
        hierarchyBrowser.changeView(CeylonTypeHierarchyBrowser.typeHierarchyType);
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
