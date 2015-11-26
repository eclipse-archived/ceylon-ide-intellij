package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;

public class CeylonSyntaxAnnotatorTest extends LightCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "testdata/source";
    }

    public void testSyntaxAnnotator() throws Exception {
        testSyntax("SyntaxError", false, false, false);
    }

    private void testSyntax(String cuName, boolean checkWarnings, boolean checkInfos, boolean checkWeakWarnings) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                FacetManager.getInstance(myModule).addFacet(CeylonFacet.getFacetType(), CeylonFacet.getFacetType().getPresentableName(), null);
            }
        });

        ITypeCheckerProvider component = myModule.getComponent(ITypeCheckerProvider.class);
        if (component instanceof TypeCheckerProvider) {
            ((TypeCheckerProvider) component).moduleAdded();
        }

        PsiFile[] files = myFixture.configureByFiles("org/intellij/plugins/ceylon/annotator/" + cuName + ".ceylon");
        TypeCheckerInvoker.invokeTypeChecker((CeylonFile) files[0]);

        myFixture.checkHighlighting(checkWarnings, checkInfos, checkWeakWarnings);
    }
}
