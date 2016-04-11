package org.intellij.plugins.ceylon.ide.ceylonCode.resolve;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.nodes_;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonReference<T extends PsiElement> extends PsiReferenceBase<T> {

    public CeylonReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (myElement.getParent() instanceof CeylonPsi.DeclarationPsi) {
            Tree.Declaration node = ((CeylonPsi.DeclarationPsi) myElement.getParent()).getCeylonNode();

            //noinspection StatementWithEmptyBody
            if (node.getDeclarationModel() instanceof TypedDeclaration
                    && ((TypedDeclaration) node.getDeclarationModel()).getOriginalDeclaration() != null) {
                // we need to resolve the original declaration
            } else if (ApplicationInfo.getInstance().getBuild().getBaselineVersion() >= 145) {
                // IntelliJ 15+ can show usages on ctrl-click, if we return null here
                // For older versions, we have to continue resolving
                return null;
            }
        }

        if (((CeylonFile) myElement.getContainingFile()).ensureTypechecked() == null) {
            return null;
        }

        Tree.CompilationUnit compilationUnit = ((CeylonFile) myElement.getContainingFile()).getCompilationUnit();

        Node node;
        if (myElement instanceof CeylonPsi.ImportPathPsi) {
            node = ((CeylonPsi.ImportPathPsi) myElement).getCeylonNode();
        } else {
            node = ((CeylonCompositeElement) myElement.getParent()).getCeylonNode();
        }

        Referenceable declaration = nodes_.get_().getReferencedExplicitDeclaration(node, compilationUnit);
        if (declaration == null) {
            return null;
        }

        Unit unit = declaration.getUnit();
        PsiFile containingFile = myElement.getContainingFile();

        if (unit != compilationUnit.getUnit()) {
            return resolveDeclaration(declaration, myElement.getProject());
        }

        Node declarationNode = nodes_.get_().getReferencedNode(declaration);

        if (declarationNode != null) {
            return CeylonTreeUtil.findPsiElement(declarationNode, containingFile);
        }
        return containingFile;
    }

    @Nullable
    public static PsiNameIdentifierOwner resolveDeclaration(Referenceable declaration, Project project) {
        return new IdeaNavigation(project).gotoDeclaration(declaration);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }

    // Make constructor references equivalent to their containing class
    @Override
    public boolean isReferenceTo(PsiElement element) {
        PsiElement resolved = resolve();
        if (getElement().getManager().areElementsEquivalent(resolved, element)) {
            return true;
        }
        if (resolved instanceof PsiMethod && ((PsiMethod) resolved).isConstructor()) {
            PsiClass parent = ((PsiMethod) resolved).getContainingClass();
            return getElement().getManager().areElementsEquivalent(parent, element);
        }
        if (element instanceof PsiMethod && ((PsiMethod) element).isConstructor()) {
            PsiClass parent = ((PsiMethod) element).getContainingClass();
            return getElement().getManager().areElementsEquivalent(resolved, parent);
        }

        return false;
    }
}
