package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.patterns.PsiJavaElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.intellij.plugins.ceylon.psi.CeylonTypes;

import static com.intellij.patterns.PsiJavaPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public class CeylonReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        @SuppressWarnings("unchecked")
        PsiJavaElementPattern.Capture<PsiElement> typeNames = psiElement(CeylonTypes.TYPE_NAME_DECLARATION).withParent(or(
                psiElement(CeylonTypes.CLASS_DECLARATION),
                psiElement(CeylonTypes.INTERFACE_DECLARATION)
        ));

        registrar.registerReferenceProvider(typeNames, new CeylonTypeReferenceProvider());
    }
}
