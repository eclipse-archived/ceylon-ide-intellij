package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.util.FindDeclarationNodeVisitor;
import org.intellij.plugins.ceylon.psi.*;
import org.intellij.plugins.ceylon.psi.stub.ClassIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.redhat.ceylon.ide.util.Nodes.getReferencedExplicitDeclaration;

public class CeylonReference<T extends PsiElement> extends PsiReferenceBase<T> {
    protected Declaration declaration;

    public CeylonReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (!(myElement instanceof CeylonPsi.IdentifierPsi)) {
            throw new UnsupportedOperationException();
        }

        // Try using the type checker
        Tree.CompilationUnit compilationUnit = ((CeylonFile) myElement.getContainingFile()).getCompilationUnit();

        Node parentNode = ((CeylonCompositeElement) myElement.getParent()).getCeylonNode();

        if (parentNode instanceof Tree.InvocationExpression) {
            parentNode = ((Tree.InvocationExpression) parentNode).getPrimary();
        }
        declaration = getReferencedExplicitDeclaration(parentNode, compilationUnit);
        if (declaration == null) {
            return null;
        }
        FindDeclarationNodeVisitor visitor = new FindDeclarationNodeVisitor(declaration);
        compilationUnit.visit(visitor);
        Tree.Declaration declarationNode = visitor.getDeclarationNode();

        if (declarationNode != null) {
            return CeylonTreeUtil.findPsiElement(declarationNode, myElement.getContainingFile());
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }

    protected PsiElement resolveByFqn(String fqn) {
        Collection<CeylonClass> decls = ClassIndex.getInstance().get(fqn, myElement.getProject(),
                GlobalSearchScope.allScope(myElement.getProject()));

        if (!decls.isEmpty()) {
            return decls.iterator().next();
        }

        return null;
    }
}
