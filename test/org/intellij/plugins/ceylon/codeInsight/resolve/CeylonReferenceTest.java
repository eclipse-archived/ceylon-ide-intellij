package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import org.intellij.plugins.ceylon.annotator.TypeCheckerInvoker;
import org.intellij.plugins.ceylon.annotator.TypeCheckerManager;
import org.intellij.plugins.ceylon.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonPsi;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class CeylonReferenceTest extends LightCodeInsightFixtureTestCase {

    private static final String PATH = "source/org/intellij/plugins/ceylon/codeInsight/resolve/";
    private TypeChecker typeChecker;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TypeCheckerManager manager = ServiceManager.getService(myFixture.getProject(), TypeCheckerManager.class);
        typeChecker = manager.createTypeChecker();
    }

    public void testReferences() throws Exception {
        CeylonFile ceylonFile = initFile("referenceTest.ceylon");

        // caret starts at (1, 1)
        moveCaret(7, 9);
        assertRefdDeclarationAtCaret(ceylonFile, "BarBar", CeylonPsi.ClassOrInterfacePsi.class, 399);

        moveCaret(12, 0);
        assertRefdDeclarationAtCaret(ceylonFile, "BarBar", CeylonPsi.ClassOrInterfacePsi.class, 399);

        moveCaret(-12, 2);
        assertRefdDeclarationAtCaret(ceylonFile, "prout", CeylonPsi.AttributeDeclarationPsi.class, 164);

        moveCaret(4, 3);
        assertRefdDeclarationAtCaret(ceylonFile, "prout", CeylonPsi.AttributeDeclarationPsi.class, 164);

        moveCaret(-4, 2);
        assertRefdDeclarationAtCaret(ceylonFile, "bar2", CeylonPsi.AttributeDeclarationPsi.class, 183);

        moveCaret(3, 0);
        assertRefdDeclarationAtCaret(ceylonFile, "plop", CeylonPsi.MethodDefinitionPsi.class, 427);

        moveCaret(-3, 2);
        assertRefdDeclarationAtCaret(ceylonFile, "myHelloWorld", CeylonPsi.MethodDefinitionPsi.class, 100);

        moveCaret(0, 2);
        assertRefdDeclarationAtCaret(ceylonFile, "plop", CeylonPsi.MethodDefinitionPsi.class, 343);

        moveCaret(2, 11);
        assertRefdDeclarationAtCaret(ceylonFile, "myHelloWorld", CeylonPsi.MethodDefinitionPsi.class, 100);
    }

    @Override
    protected String getTestDataPath() {
        return "testdata";
    }

    private CeylonFile initFile(String filename) throws InterruptedException, TimeoutException {
        myFixture.configureByFiles(PATH + filename);
        final CeylonFile ceylonFile = (CeylonFile) myFixture.getFile();

        TypeCheckerInvoker.invokeTypeChecker(typeChecker, ceylonFile);
        return ceylonFile;
    }

    private void moveCaret(int columnShift, int lineShift) {
        myFixture.getEditor().getCaretModel().moveCaretRelatively(columnShift, lineShift, false, false, true);
    }

    private <PSI extends CeylonPsi.DeclarationPsi> PSI assertRefdDeclarationAtCaret(CeylonFile ceylonFile, String referencedId, Class<? extends PSI> psiClass, int referencedOffest) {
        PSI referenced = assertRefdAtCaret(ceylonFile, referencedOffest, psiClass);
        assertEquals(referencedId, referenced.getCeylonNode().getIdentifier().getText());
        return referenced;
    }

    private <PSI extends CeylonCompositeElement> PSI assertRefdAtCaret(CeylonFile ceylonFile, int referencedOffset, Class<? extends PSI> psiClass) {
        final PsiElement elementAtCaret = ceylonFile.findElementAt(myFixture.getCaretOffset());
        PsiElement element = PsiTreeUtil.getParentOfType(elementAtCaret, CeylonPsi.IdentifierPsi.class);
        assertNotNull("Identifier not found at offset " + myFixture.getCaretOffset(), element);
        final PSI referenced = getReferencedElement(element);
        assertTrue(String.format("%s (%s) not instance of %s", referenced, referenced.getClass(), psiClass), psiClass.isAssignableFrom(referenced.getClass()));
        assert referenced instanceof PsiNameIdentifierOwner : referenced;
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
