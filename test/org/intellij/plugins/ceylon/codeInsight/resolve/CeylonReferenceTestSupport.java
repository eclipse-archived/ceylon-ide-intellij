package org.intellij.plugins.ceylon.codeInsight.resolve;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import org.intellij.plugins.ceylon.annotator.TypeCheckerInvoker;
import org.intellij.plugins.ceylon.annotator.TypeCheckerManager;
import org.intellij.plugins.ceylon.psi.CeylonFile;

import java.util.concurrent.TimeoutException;

public class CeylonReferenceTestSupport extends LightCodeInsightFixtureTestCase {
    protected static final String PATH = "source/org/intellij/plugins/ceylon/codeInsight/resolve/";
    protected TypeChecker typeChecker;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TypeCheckerManager manager = ServiceManager.getService(myFixture.getProject(), TypeCheckerManager.class);
        typeChecker = manager.createTypeChecker();
    }

    protected CeylonFile initFile(String filename) throws InterruptedException, TimeoutException {
        myFixture.configureByFiles(PATH + filename);
        final CeylonFile ceylonFile = (CeylonFile) myFixture.getFile();

        TypeCheckerInvoker.invokeTypeChecker(typeChecker, ceylonFile);
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
