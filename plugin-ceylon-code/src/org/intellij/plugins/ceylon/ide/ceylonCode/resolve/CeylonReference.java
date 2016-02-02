package org.intellij.plugins.ceylon.ide.ceylonCode.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.ide.common.util.nodes_;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
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
    public static PsiNameIdentifierOwner resolveDeclaration(Referenceable declaration, Project project) {
        return new IdeaNavigation(project).gotoDeclaration(declaration);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }
}
