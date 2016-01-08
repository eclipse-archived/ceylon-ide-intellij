package org.intellij.plugins.ceylon.ide.ceylonCode.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.model.CeylonUnit;
import com.redhat.ceylon.ide.common.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.ide.common.util.nodes_;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Unit;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.*;
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
            return null;
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

        FindDeclarationNodeVisitor visitor = new FindDeclarationNodeVisitor(declaration);
        compilationUnit.visit(visitor);
        Tree.StatementOrArgument declarationNode = visitor.getDeclarationNode();

        if (declarationNode != null) {
            return CeylonTreeUtil.findPsiElement(declarationNode, containingFile);
        }
        return containingFile;
    }

    @Nullable
    public static CeylonCompositeElement resolveDeclaration(Referenceable declaration, Project project) {
        Node declarationNode = nodes_.get_().getReferencedNode(declaration);

        if (declarationNode != null) {
            FindMatchingPsiNodeVisitor psiVisitor = new FindMatchingPsiNodeVisitor(declarationNode, CeylonPsi.StatementOrArgumentPsi.class);
            PsiFile declaringFile = CeylonTreeUtil.getDeclaringFile(declaration.getUnit(), project);

            if (declaringFile instanceof CeylonFile && declaration.getUnit() instanceof CeylonUnit) {
                // make our parser use the compilation unit created by the typechecker
                CeylonUnit ceylonUnit = (CeylonUnit) declaration.getUnit();
                declaringFile.putUserData(IdeaCeylonParser.FORCED_CU_KEY, ceylonUnit.getCompilationUnit());
                ((CeylonFile) declaringFile).setPhasedUnit(ceylonUnit.getPhasedUnit());
            }
            psiVisitor.visitFile(declaringFile);
            return psiVisitor.getPsi();
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }
}
