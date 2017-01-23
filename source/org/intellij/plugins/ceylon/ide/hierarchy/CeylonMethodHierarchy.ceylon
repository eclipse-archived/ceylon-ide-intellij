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
import com.redhat.ceylon.ide.common.hierarchy {
    hierarchyManager
}
import com.redhat.ceylon.ide.common.util {
    types
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.lang {
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.resolve {
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
                        = CeylonHierarchyNodeDescriptor {
                            element = element;
                            model = model;
                            parent = parentDescriptor;
                        };
                parentDescriptor.children = ObjectArray(1, nodeDescriptor);
                return nodeDescriptor;
            }
        }
        return CeylonHierarchyNodeDescriptor(element, model);
    }

    shared actual ObjectArray<CeylonHierarchyNodeDescriptor>
    aggregateSubtypes(CeylonHierarchyNodeDescriptor descriptor) {
        if (exists model = descriptor.model) {
            value subtypes = hierarchyManager.findSubtypes(model, {*phasedUnits});
            return ObjectArray.with {
                for (declaration in subtypes)
                    if (exists psiElement = resolveDeclaration(declaration, project))
                        CeylonHierarchyNodeDescriptor(psiElement, declaration, descriptor)
            };
        }
        return noDescriptors;
    }

    shared actual ObjectArray<CeylonHierarchyNodeDescriptor>
    aggregateSupertypes(CeylonHierarchyNodeDescriptor descriptor) {
        if (exists model = descriptor.model) {
            value supertypes = hierarchyManager.findSupertypes(model);
            return ObjectArray.with {
                for (declaration in supertypes)
                if (exists psiElement = resolveDeclaration(declaration, project))
                    CeylonHierarchyNodeDescriptor(psiElement, declaration, descriptor)
            };
        }
        return noDescriptors;
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
