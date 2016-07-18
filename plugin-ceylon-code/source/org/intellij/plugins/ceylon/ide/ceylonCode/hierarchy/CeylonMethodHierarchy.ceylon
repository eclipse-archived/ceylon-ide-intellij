import ceylon.interop.java {
    createJavaObjectArray
}

import com.intellij.ide.hierarchy {
    HierarchyBrowser,
    HierarchyProvider,
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
    PsiElement
}
import com.redhat.ceylon.ide.common.util {
    types
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    ClassOrInterface,
    TypeDeclaration,
    ModelUtil,
    Type
}

import java.lang {
    ObjectArray
}
import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    resolveDeclaration
}

alias MemberDec => CeylonPsi.DeclarationPsi | CeylonPsi.SpecifierStatementPsi;

class CeylonMethodHierarchyBrowser(Project project, PsiElement element)
        extends CeylonHierarchyBrowser(project, element) {

    isApplicableElement(PsiElement psiElement) => psiElement is MemberDec;

    shared actual CeylonHierarchyNodeDescriptor build(PsiElement element, Declaration? model) {
        if (exists model,
            exists refined = types.getRefinedDeclaration(model),
            refined!=model) {
            if (exists psiElement = resolveDeclaration(refined, project)) {
                value parentDescriptor = build(psiElement, refined);
                value nodeDescriptor
                        = CeylonHierarchyNodeDescriptor(element, model, parentDescriptor);
                parentDescriptor.children = createJavaObjectArray { nodeDescriptor };
                return nodeDescriptor;
            }
        }
        return CeylonHierarchyNodeDescriptor(element, model);
    }

    Boolean directlyRefines(Declaration subtype, Declaration supertype) {
        assert (is TypeDeclaration subtypeScope = subtype.container,
            is TypeDeclaration supertypeScope = supertype.container);
        value interveningRefinements
                = ModelUtil.getInterveningRefinements(subtype.name,
                    ModelUtil.getSignature(subtype),
                    supertype.refinedDeclaration,
                    subtypeScope, supertypeScope);
        interveningRefinements.remove(supertype);
        return interveningRefinements.empty;
    }

    shared actual ObjectArray<CeylonHierarchyNodeDescriptor>
    aggregateSubtypes(CeylonHierarchyNodeDescriptor descriptor) {
        value result = ArrayList<CeylonHierarchyNodeDescriptor>();
        if (exists model = descriptor.model,
            model.classOrInterfaceMember,
            model.formal || model.default) {
            for (unit in phasedUnits) {
                for (declaration in unit.declarations) {
                    if (declaration.classOrInterfaceMember, declaration.actual) {
                        if (declaration.refines(model),
                            declaration != model,
                            directlyRefines(declaration, model)) {
                            if (exists psiElement = resolveDeclaration(declaration, project)) {
                                result.add(CeylonHierarchyNodeDescriptor(psiElement, declaration, descriptor));
                            }
                        }
                    }
                }
            }
        }
        return result.toArray(noDescriptors);
    }

    shared actual ObjectArray<CeylonHierarchyNodeDescriptor>
    aggregateSupertypes(CeylonHierarchyNodeDescriptor descriptor) {
        value result = ArrayList<CeylonHierarchyNodeDescriptor>();
        if (exists model = descriptor.model,
            is ClassOrInterface container = model.container,
            model.actual) {
            List<Type>? signature = ModelUtil.getSignature(model);
            for (supertype in container.supertypeDeclarations) {
                if (exists declaration = supertype.getDirectMember(model.name, signature, false, true),
                    declaration.default || declaration.formal) {
                    if (model.refines(declaration),
                        directlyRefines(model, declaration)) {
                        if (exists psiElement = resolveDeclaration(declaration, project)) {
                            result.add(CeylonHierarchyNodeDescriptor(psiElement, declaration, descriptor));
                        }
                    }
                }
            }
        }
        return result.toArray(noDescriptors);
    }

}

shared class CeylonMethodHierarchyProvider() satisfies HierarchyProvider {

    shared actual void browserActivated(HierarchyBrowser hierarchyBrowser) {
        assert (is CeylonHierarchyBrowser hierarchyBrowser);
        hierarchyBrowser.changeView(TypeHierarchyBrowserBase.typeHierarchyType);
    }

    shared actual PsiElement? getTarget(DataContext dataContext) {
        if (!CommonDataKeys.project.getData(dataContext) exists) {
            return null;
        }

        if (is MemberDec psi = CommonDataKeys.psiElement.getData(dataContext)) {
            return psi;
        }

        value caret = CommonDataKeys.caret.getData(dataContext);
        value file = CommonDataKeys.psiFile.getData(dataContext);
        if (exists caret, exists file) {
            variable value p = file.findElementAt(caret.offset);
            while (exists ep = p, !p is MemberDec) {
                p = ep.parent;
            }
            return p;
        }

        return null;
    }

    createHierarchyBrowser(PsiElement psiElement)
            => CeylonMethodHierarchyBrowser(psiElement.project, psiElement);
}
