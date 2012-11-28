package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.psi.*;
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
        CeylonQualifiedType qualifiedType = PsiTreeUtil.getParentOfType(myElement, CeylonQualifiedType.class);

        CeylonFile containingFile = (CeylonFile) myElement.getContainingFile();
        CeylonImportDeclaration[] potentialImports = containingFile.getPotentialImportsForType((CeylonTypeName) myElement);

        String fqn;

        if (potentialImports.length > 0) {
            for (CeylonImportDeclaration potentialImport : potentialImports) {
                fqn = potentialImport.getPackagePath().getText() + "." + myElement.getText();
                PsiElement resolved = resolveByFqn(fqn);

                if (resolved != null) {
                    return resolved;
                }
            }
        }

        fqn = containingFile.getPackageName() + "." + myElement.getText();
        return resolveByFqn(fqn);
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
