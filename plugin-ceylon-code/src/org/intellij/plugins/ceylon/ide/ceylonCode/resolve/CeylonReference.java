package org.intellij.plugins.ceylon.ide.ceylonCode.resolve;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.model.CeylonUnit;
import com.redhat.ceylon.ide.common.util.FindDeclarationNodeVisitor;
import com.redhat.ceylon.ide.common.util.nodes_;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Unit;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.*;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub.ClassIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CeylonReference<T extends PsiElement> extends PsiReferenceBase<T> {
    protected Referenceable declaration;

    public CeylonReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (!(myElement instanceof CeylonPsi.IdentifierPsi)) {
            throw new UnsupportedOperationException();
        }
        if (myElement.getParent() instanceof CeylonPsi.DeclarationPsi) {
            return null;
        }

        if (((CeylonFile) myElement.getContainingFile()).ensureTypechecked() == null) {
            return null;
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
        declaration = nodes_.get_().getReferencedExplicitDeclaration(parentNode, compilationUnit);
        if (declaration == null) {
            return null;
        }

        Unit unit = declaration.getUnit();
        PsiFile containingFile = myElement.getContainingFile();

        if (unit != compilationUnit.getUnit()) {
            CeylonFile ceylonFile = (CeylonFile) containingFile;
            Module module = ModuleUtil.findModuleForFile(ceylonFile.getVirtualFile(), ceylonFile.getProject());

            if (module != null) {
                TypeChecker tc = module.getComponent(ITypeCheckerProvider.class).getTypeChecker();
                return resolveDeclaration(declaration, tc, myElement.getProject());
            }
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
    public static CeylonCompositeElement resolveDeclaration(Referenceable declaration, TypeChecker tc, Project project) {
        Node declarationNode = nodes_.get_().getReferencedNode(declaration);
//        Tree.CompilationUnit compilationUnit = ((CeylonUnit) declaration.getUnit()).getCompilationUnit();
//
//        FindDeclarationNodeVisitor visitor = new FindDeclarationNodeVisitor(declaration);
//        compilationUnit.visit(visitor);
//        Tree.StatementOrArgument declarationNode = visitor.getDeclarationNode();

        if (declarationNode != null) {
            FindMatchingPsiNodeVisitor psiVisitor = new FindMatchingPsiNodeVisitor(declarationNode, CeylonPsi.StatementOrArgumentPsi.class);
            PsiFile declaringFile = CeylonTreeUtil.getDeclaringFile(declaration.getUnit(), project);
            if (declaringFile instanceof CeylonFile) {
                //declaringFile.putUserData(IdeaCeylonParser.FORCED_CU_KEY, compilationUnit);
            }
            psiVisitor.visitFile(declaringFile);
            return psiVisitor.getPsi();
        }

        return null;
    }

    @Nullable
    private PsiElement resolveDirectory(PsiElement path, Tree.ImportPath descriptor) {
        for (int i = 0; i < descriptor.getIdentifiers().size(); i++) {
            Tree.Identifier identifier = descriptor.getIdentifiers().get(i);

            if (identifier.getStartIndex().equals(path.getTextRange().getStartOffset())) {
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
