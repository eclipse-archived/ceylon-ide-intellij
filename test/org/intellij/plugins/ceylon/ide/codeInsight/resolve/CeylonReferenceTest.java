package org.intellij.plugins.ceylon.ide.codeInsight.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.ideInsightTestSupport;
import org.intellij.plugins.ceylon.ide.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;

import java.util.List;

public class CeylonReferenceTest extends CeylonCodeInsightTestSupport {

    public void testReferences() throws Exception {
        CeylonFile ceylonFile = initFile("referenceTest.ceylon");

        // caret starts at (1, 1)
        moveCaret(7, 9);
        assertRefdDeclarationAtCaret(ceylonFile, "BarBar", CeylonPsi.ClassOrInterfacePsi.class, 422);

        moveCaret(12, 0);
        assertRefdDeclarationAtCaret(ceylonFile, "BarBar", CeylonPsi.ClassOrInterfacePsi.class, 422);

        moveCaret(-12, 2);
        assertRefdDeclarationAtCaret(ceylonFile, "prout", CeylonPsi.AttributeDeclarationPsi.class, 164);

        moveCaret(4, 3);
        assertRefdDeclarationAtCaret(ceylonFile, "prout", CeylonPsi.AttributeDeclarationPsi.class, 164);

        moveCaret(-4, 2);
        assertRefdDeclarationAtCaret(ceylonFile, "bar2", CeylonPsi.AttributeDeclarationPsi.class, 183);

        moveCaret(3, 0);
        assertRefdDeclarationAtCaret(ceylonFile, "plop", CeylonPsi.MethodDefinitionPsi.class, 473);

        moveCaret(-3, 2);
        assertRefdDeclarationAtCaret(ceylonFile, "myHelloWorld", CeylonPsi.MethodDefinitionPsi.class, 100);

        moveCaret(0, 2);
        assertRefdDeclarationAtCaret(ceylonFile, "plop", CeylonPsi.MethodDefinitionPsi.class, 366);

        moveCaret(10, 2);
//        assertRefdParamDeclarationAtCaret(ceylonFile, "attr", CeylonPsi.ValueParameterDeclarationPsi.class, 444);

        moveCaret(-8, 11);
        assertRefdDeclarationAtCaret(ceylonFile, "myHelloWorld", CeylonPsi.MethodDefinitionPsi.class, 100);

        moveCaret(2, 5);
        assertRefdParamDeclarationAtCaret(ceylonFile, "par", CeylonPsi.ValueParameterDeclarationPsi.class, 544);
    }

    private <PSI extends CeylonPsi.DeclarationPsi> PSI assertRefdDeclarationAtCaret(CeylonFile ceylonFile, String referencedId, Class<? extends PSI> psiClass, int referencedOffest) {
        PSI referenced = assertRefdAtCaret(ceylonFile, referencedOffest, psiClass);
        assertEquals(referencedId, referenced.getCeylonNode().getIdentifier().getText());
        return referenced;
    }

    private <PSI extends CeylonPsi.ValueParameterDeclarationPsi> PSI assertRefdParamDeclarationAtCaret(CeylonFile ceylonFile, String referencedId, Class<? extends PSI> psiClass, int referencedOffest) {
        CeylonPsi.AttributeDeclarationPsi referenced = assertRefdAtCaret(ceylonFile, referencedOffest, CeylonPsi.AttributeDeclarationPsi.class);
        PSI parent = (PSI) referenced.getParent();
        assertEquals(referencedId, parent.getCeylonNode().getTypedDeclaration().getIdentifier().getText());
        return parent;
    }

    private <PSI extends CeylonCompositeElement> PSI assertRefdAtCaret(CeylonFile ceylonFile, int referencedOffset, Class<? extends PSI> psiClass) {
        final PsiElement elementAtCaret = ceylonFile.findElementAt(myFixture.getCaretOffset());
        PsiElement element = PsiTreeUtil.getParentOfType(elementAtCaret, CeylonPsi.IdentifierPsi.class);
        assertNotNull("Identifier not found at offset " + myFixture.getCaretOffset(), element);
        final PSI referenced = getReferencedElement(element);
        assertTrue(String.format("%s (%s) not instance of %s", referenced, referenced.getClass(), psiClass), psiClass.isAssignableFrom(referenced.getClass()));
        assertTrue(referenced.toString(), referenced instanceof PsiNameIdentifierOwner);
        final PsiElement nameIdentifier = ((PsiNameIdentifierOwner) referenced).getNameIdentifier();
        assertNotNull(nameIdentifier);
        assertEquals(referencedOffset, nameIdentifier.getTextOffset());
        return referenced;
    }

    private <T> T getReferencedElement(PsiElement element) {
        final PsiReference ref = getSingleReference(element);
        return resolveReference(ref);
    }

    private <T> T resolveReference(PsiReference ref) {
        @SuppressWarnings("unchecked")
        final T referenced = (T) ref.resolve();
        assertNotNull("Reference resolved to null.", referenced);
        return referenced;
    }

    private PsiReference getSingleReference(PsiElement element) {
        final List<PsiReference> refs = PsiReferenceService.getService().getReferences(element, new PsiReferenceService.Hints());
        assertSize(1, refs);
        return refs.get(0);
    }
}
