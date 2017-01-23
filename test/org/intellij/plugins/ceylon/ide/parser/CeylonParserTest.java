package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.testFramework.ParsingTestCase;
import org.intellij.plugins.ceylon.ide.parser.CeylonParserDefinition;

public class CeylonParserTest extends ParsingTestCase {

    public CeylonParserTest() {
        super("parsing", "ceylon", new CeylonParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "testdata";
    }

    @Override
    protected boolean skipSpaces() {
        return true;
    }

    public void testEmpty() { doTest(true); }
    public void testComments() { doTest(true); }
    public void testClass() { doTest(true); }
    public void testModule() { doTest(true); }
    public void testClassDeclarations() { doTest(true); }
    public void testAlgebraic() { doTest(true); }
    public void testSimpleTest() { doTest(true); }
    public void testExpressions() { doTest(true); }
    public void testTopLevelMethod() { doTest(true); }
    public void testTrailingTokens() { doTest(true); }
}
