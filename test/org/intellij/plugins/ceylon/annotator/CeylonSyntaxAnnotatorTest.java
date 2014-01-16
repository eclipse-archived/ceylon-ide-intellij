package org.intellij.plugins.ceylon.annotator;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.intellij.plugins.ceylon.psi.CeylonFile;

public class CeylonSyntaxAnnotatorTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata";
    }

    public void testSyntaxAnnotator() throws Exception {
        final PsiFile[] files = myFixture.configureByFiles("source/org/intellij/plugins/ceylon/annotator/SyntaxError.ceylon");
        final PsiFile file = files[0];
        TypeCheckerInvoker.invokeTypeChecker((CeylonFile) file);
        myFixture.checkHighlighting(false, false, false);
    }
}
