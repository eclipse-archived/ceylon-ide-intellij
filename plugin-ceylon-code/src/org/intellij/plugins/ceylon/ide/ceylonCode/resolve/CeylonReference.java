package org.intellij.plugins.ceylon.ide.ceylonCode.resolve;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.ClassIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static org.intellij.plugins.ceylon.ide.ceylonCode.resolve.Nodes.getReferencedExplicitDeclaration;

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

        if (parentNode instanceof Tree.ImportPath) {
            return resolveDirectory(myElement, (Tree.ImportPath) parentNode);
        }
        if (parentNode instanceof Tree.InvocationExpression) {
            parentNode = ((Tree.InvocationExpression) parentNode).getPrimary();
        }
        if (parentNode instanceof Tree.ExtendedType) {
            parentNode = ((Tree.ExtendedType) parentNode).getType();
        }
        declaration = getReferencedExplicitDeclaration(parentNode, compilationUnit);
        if (declaration == null) {
            return null;
        }

        Unit unit = declaration.getUnit();
        PsiFile containingFile = myElement.getContainingFile();

        if (unit != compilationUnit.getUnit()) {
            containingFile = CeylonTreeUtil.getDeclaringFile(unit, containingFile.getProject());
            if (containingFile instanceof CeylonFile) {
                compilationUnit = ((CeylonFile) containingFile).getCompilationUnit();
            }
        }

        FindDeclarationNodeVisitor visitor = new FindDeclarationNodeVisitor(declaration);
        compilationUnit.visit(visitor);
        Tree.Declaration declarationNode = visitor.getDeclarationNode();

        if (declarationNode != null) {
            return CeylonTreeUtil.findPsiElement(declarationNode, containingFile);
        }
        return containingFile;
    }

    @Nullable
    private PsiElement resolveDirectory(PsiElement path, Tree.ImportPath descriptor) {
        for (int i = 0; i < descriptor.getIdentifiers().size(); i++) {
            Tree.Identifier identifier = descriptor.getIdentifiers().get(i);

            if (identifier.getStartIndex().equals(path.getTextOffset())) {
                int nbUpLevels = descriptor.getIdentifiers().size() - i - 1;
                PsiDirectory directory = path.getContainingFile().getParent();

                for (int j = 0; j < nbUpLevels; j++) {
                    if (directory != null) {
                        directory = directory.getParentDirectory();
                    }
                }

                if (directory != null && directory.getName().equals(path.getText())) {
                    return directory;
                } else {
                    Logger.getInstance(CeylonReference.class).warn("Could not find directory " + path.getText() + " from package " + descriptor.getText());
                }
            }
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
