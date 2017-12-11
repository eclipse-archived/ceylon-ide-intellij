package org.eclipse.ceylon.ide.intellij.annotator;

import com.intellij.psi.PsiFile;
import org.eclipse.ceylon.ide.intellijInsightTestSupport;
import org.eclipse.ceylon.ide.intellij.psi.CeylonFile;
import org.eclipse.ceylon.ide.intellij.psi.CeylonLocalAnalyzer;

public class CeylonSyntaxAnnotatorTest extends CeylonCodeInsightTestSupport {

    @Override
    protected String getTestDataPath() {
        return "testdata/source";
    }

    public void testSyntaxAnnotator() throws Exception {
        testSyntax("SyntaxError", false, false, false);
    }

    private void testSyntax(String cuName, boolean checkWarnings, boolean checkInfos, boolean checkWeakWarnings) {

        PsiFile[] files = myFixture.configureByFiles("org/intellij/plugins/ceylon/annotator/" + cuName + ".ceylon");
        CeylonLocalAnalyzer localAnalyzer = ((CeylonFile) files[0]).getLocalAnalyzer();
        if (localAnalyzer != null) {
            localAnalyzer.ensureTypechecked();
        }
        myFixture.checkHighlighting(checkWarnings, checkInfos, checkWeakWarnings);
    }
}
