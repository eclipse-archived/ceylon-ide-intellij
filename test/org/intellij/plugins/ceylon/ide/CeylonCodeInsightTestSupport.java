package org.intellij.plugins.ceylon.ide;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonLocalAnalyzer;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;

import java.util.concurrent.TimeoutException;

public abstract class CeylonCodeInsightTestSupport extends LightCodeInsightFixtureTestCase {
    protected static final String PATH = "source/org/intellij/plugins/ceylon/codeInsight/resolve/";
    protected TypeChecker typeChecker;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (FacetManager.getInstance(myModule).getFacetByType(CeylonFacet.ID) == null) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    FacetManager.getInstance(myModule).addFacet(CeylonFacet.getFacetType(), CeylonFacet.getFacetType().getPresentableName(), null);
                }
            });
        }

        ITypeCheckerProvider component = myModule.getComponent(ITypeCheckerProvider.class);
        if (component instanceof TypeCheckerProvider) {
            ((TypeCheckerProvider) component).moduleAdded();
            typeChecker = component.getTypeChecker();
        }
    }

    protected CeylonFile initFile(String filename) throws InterruptedException, TimeoutException {
        myFixture.configureByFiles(PATH + filename);
        final CeylonFile ceylonFile = (CeylonFile) myFixture.getFile();

        CeylonLocalAnalyzer localAnalyzer = ceylonFile.getLocalAnalyzer();
        if (localAnalyzer != null) {
            localAnalyzer.ensureTypechecked();
        }
        return ceylonFile;
    }

    protected void moveCaret(int columnShift, int lineShift) {
        myFixture.getEditor().getCaretModel().moveCaretRelatively(columnShift, lineShift, false, false, true);
    }

    @Override
    protected String getTestDataPath() {
        return "testdata";
    }
}
