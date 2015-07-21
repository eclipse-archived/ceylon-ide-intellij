package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;

public class CeylonSyntaxAnnotatorTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/source";
    }

    public void testSyntaxAnnotator() throws Exception {
        testSyntax("SyntaxError", false, false, false);
    }

/*
    public void testInexistentPackage() throws Exception {
        testSyntax("InexistentPackage", false, false, false);
        // todo: Make sure CeylonTypeCheckerAnnotator finishes its job to test this
    }
*/

    private void testSyntax(String cuName, boolean checkWarnings, boolean checkInfos, boolean checkWeakWarnings) {
        final PsiFile[] files = myFixture.configureByFiles("org/intellij/plugins/ceylon/annotator/" + cuName + ".ceylon");
        final CeylonFile file = (CeylonFile) files[0];
//        TypeCheckerInvoker.invokeTypeChecker(file);
//        System.out.println(file.getContainingDirectory());
//        System.out.println(file.getCompilationUnit().getUnit());
        myFixture.checkHighlighting(checkWarnings, checkInfos, checkWeakWarnings);
    }
}
