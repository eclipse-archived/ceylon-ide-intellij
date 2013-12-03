package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.code.resolve.FindDeclarationNodeVisitor;
import com.redhat.ceylon.ide.code.resolve.FindReferenceVisitor;
import org.intellij.plugins.ceylon.psi.*;
import org.intellij.plugins.ceylon.psi.stub.ClassIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.redhat.ceylon.ide.code.resolve.CeylonReferenceResolver.getReferencedExplicitDeclaration;

public class CeylonTypeReference<T extends PsiElement> extends PsiReferenceBase<T> {

    public CeylonTypeReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (!(myElement instanceof CeylonPsi.IdentifierPsi)) {
            throw new UnsupportedOperationException();
        }

        Tree.Identifier node = ((CeylonPsi.IdentifierPsi) myElement).getCeylonNode();

        if (node == null) {
            return null;
        }

        Tree.CompilationUnit compilationUnit = ((CeylonFile) myElement.getContainingFile()).getCompilationUnit();

        // Try using ClassIndex
        PsiElement resolved = resolveByFqn(node.getText());
        if (resolved != null) {
            return resolved;
        }

        // Try using the type checker
        Node parentNode = ((CeylonCompositeElement) myElement.getParent()).getCeylonNode();

        if (parentNode instanceof Tree.InvocationExpression) {
            parentNode = parentNode.getChildren().get(0); // TODO uh oh
        }
        Declaration declaration = getReferencedExplicitDeclaration(parentNode, compilationUnit);
        FindReferenceVisitor frVisitor = new FindReferenceVisitor(declaration);
        compilationUnit.visit(frVisitor);
        FindDeclarationNodeVisitor visitor = new FindDeclarationNodeVisitor(frVisitor.getDeclaration());
        compilationUnit.visit(visitor);
        Tree.Declaration declarationNode = visitor.getDeclarationNode();

        if (declarationNode != null) {
            return CeylonTreeUtil.findPsiElement(declarationNode, myElement.getContainingFile());
        }

        if (declaration == null) {
            return null;
        }
        // Try implicit ceylon.language.*
        String fqn = declaration.getContainer().getQualifiedNameString() + "." + node.getText();
        resolved = JavaPsiFacade.getInstance(myElement.getProject()).findClass(fqn, GlobalSearchScope.allScope(myElement.getProject()));

        if (resolved != null) {
            return resolved;
        }

        fqn += "_";
        return JavaPsiFacade.getInstance(myElement.getProject()).findClass(fqn, GlobalSearchScope.allScope(myElement.getProject()));
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return element.equals(resolve());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }

    private PsiElement resolveByFqn(String fqn) {
        Collection<CeylonClass> decls = ClassIndex.getInstance().get(fqn, myElement.getProject(),
                GlobalSearchScope.allScope(myElement.getProject()));

        if (!decls.isEmpty()) {
            return decls.iterator().next();
        }

        return null;
    }
}
