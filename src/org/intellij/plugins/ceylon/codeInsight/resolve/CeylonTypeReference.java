package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
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

        fqn = containingFile.getPackageName() + "." + name;
        PsiElement resolved = resolveByFqn(fqn);
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
}
