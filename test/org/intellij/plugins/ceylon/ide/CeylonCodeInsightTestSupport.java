package org.intellij.plugins.ceylon.ide;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerInvoker;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;

import java.util.concurrent.TimeoutException;

public abstract class CeylonCodeInsightTestSupport extends LightCodeInsightFixtureTestCase {
    protected static final String PATH = "source/org/intellij/plugins/ceylon/codeInsight/resolve/";
    protected TypeChecker typeChecker;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TypeCheckerProvider typeCheckerProvider = myFixture.getModule().getComponent(TypeCheckerProvider.class);
        typeChecker = typeCheckerProvider.createTypeChecker();
    }

    protected CeylonFile initFile(String filename) throws InterruptedException, TimeoutException {
        myFixture.configureByFiles(PATH + filename);
        final CeylonFile ceylonFile = (CeylonFile) myFixture.getFile();

        TypeCheckerInvoker.invokeTypeChecker(ceylonFile, typeChecker);
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
