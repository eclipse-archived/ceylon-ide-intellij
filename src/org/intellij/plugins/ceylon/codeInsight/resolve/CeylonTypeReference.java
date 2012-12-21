package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.annotator.SourceCodeVirtualFile;
import org.intellij.plugins.ceylon.annotator.TypeCheckerManager;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonImportDeclaration;
import org.intellij.plugins.ceylon.psi.CeylonTypeName;
import org.intellij.plugins.ceylon.psi.impl.CeylonIdentifier;
import org.intellij.plugins.ceylon.psi.stub.ClassIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CeylonTypeReference<T extends PsiElement> extends PsiReferenceBase<T> {

    public CeylonTypeReference(T element, TextRange range, boolean soft) {
        super(element, range, soft);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (!(myElement instanceof CeylonIdentifier)) {
            throw new UnsupportedOperationException();
        }

        if (myElement.getParent() instanceof CeylonClass) {
            return null; // type definition does not resolve to itself
        }
        if (true) {
            PsiFile file = myElement.getContainingFile();
            TypeCheckerManager manager = ServiceManager.getService(file.getProject(), TypeCheckerManager.class);

            TypeChecker typeChecker = manager.getTypeChecker();

            SourceCodeVirtualFile sourceCodeVirtualFile = new SourceCodeVirtualFile(file);
            PhasedUnit phasedUnit = typeChecker.getPhasedUnit(sourceCodeVirtualFile);

            if (phasedUnit == null) {
                return null;
            }

            int textOffset = myElement.getTextOffset();
            Node node = getNodeAtOffset(textOffset, phasedUnit.getCompilationUnit());
            if (node == null) {
                return null;
            }
            if (node instanceof Tree.MemberOrTypeExpression) {
                FindReferencedNodeVisitor visitor = new FindReferencedNodeVisitor(((Tree.MemberOrTypeExpression) node).getDeclaration());
                visitor.visit(phasedUnit.getCompilationUnit());
                Node declaration = visitor.getDeclarationNode();
                if (declaration instanceof Tree.Declaration) {
                    return file.findElementAt(((Tree.Declaration) declaration).getIdentifier().getStartIndex()).getParent();
                }
            } else if (node instanceof Tree.Identifier) {
            }

            return null;
        }
        String name = myElement.getText();

        CeylonFile containingFile = (CeylonFile) myElement.getContainingFile();
        CeylonImportDeclaration[] potentialImports = containingFile.getPotentialImportsForType((CeylonTypeName) myElement);

        String fqn;

        if (potentialImports.length > 0) {
            for (CeylonImportDeclaration potentialImport : potentialImports) {
                fqn = potentialImport.getPackagePath().getText() + "." + name;
                PsiElement resolved = resolveByFqn(fqn);

                if (resolved != null) {
                    return resolved;
                }
            }
        }

        CeylonClass parentClass;
        PsiElement element = myElement;
        PsiElement resolved;

        // TODO if multiple interfaces Foo in the same file, only one should be matched
        while ((parentClass = PsiTreeUtil.getParentOfType(element, CeylonClass.class)) != null) {
            fqn = parentClass.getQualifiedName() + "." + name;
            resolved = resolveByFqn(fqn);
            if (resolved != null) {
                return resolved;
            }
            element = parentClass;
        }


        fqn = containingFile.getPackageName() + "." + name;
        resolved = resolveByFqn(fqn);
        if (resolved != null) {
            return resolved;
        }

        Project project = myElement.getProject();

        Collection<CeylonClass> classes = ClassIndex.getInstance().get("ceylon.language." + name, project, GlobalSearchScope.allScope(project));

        if (!classes.isEmpty()) {
            return classes.iterator().next();
        }

        // TODO handle references to type parameter
        return null;
//        return JavaPsiFacade.getInstance(project).findClass("ceylon.language." + name, GlobalSearchScope.allScope(project));

//        CeylonQualifiedType qualifiedType = PsiTreeUtil.getParentOfType(myElement, CeylonQualifiedType.class);
//        if (qualifiedType != null) {
//            // TODO surely incomplete, test foo.bar.MyClass:MySubclass (should get qualified name instead of text)
//            // Also test class A { class B; class C extends B} and in separate file, class B
//            CeylonSupertypeQualifier supertypeQualifier = qualifiedType.getSupertypeQualifier();
//            if (supertypeQualifier != null && !supertypeQualifier.getTypeName().equals(myElement)) {
//                name = supertypeQualifier.getTypeName().getText() + "." + name;
//            }
//        }
//
//        if (name == null) {
//            return null;
//        }
    }

    private PsiElement resolveByFqn(String fqn) {
        Collection<CeylonClass> decls = ClassIndex.getInstance().get(fqn, myElement.getProject(),
                GlobalSearchScope.projectScope(myElement.getProject()));

        if (!decls.isEmpty()) {
            return decls.iterator().next().getNameIdentifier();
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }

    private Node getNodeAtOffset(int offset, Node root) {
        if (root == null || root.getStartIndex() == null || root.getStopIndex() == null) {
            return null;
        }
        if (root.getStartIndex() <= offset && offset <= root.getStopIndex()) {
            for (Node node : root.getChildren()) {
                Integer startIndex = node.getStartIndex();
                if (startIndex != null && startIndex == offset) {
                    if (node instanceof Tree.MemberOrTypeExpression || node.getChildren().isEmpty()) {
                        return node;
                    } else {
                        return getNodeAtOffset(offset, node);
                    }
                } else {
                    Node subNode = getNodeAtOffset(offset, node);
                    if (subNode != null) {
                        return subNode;
                    }
                }
            }
        }
        return null;
    }
}
