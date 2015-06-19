package doc;

import ceylon.language.language_;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.LightCodeInsightTestCase;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.ceylonCode.doc.docGenerator_;

import java.io.File;
import java.io.IOException;

public class CeylonDocProviderTest extends LightCodeInsightTestCase {

    private final TypeChecker tc;

    public CeylonDocProviderTest() {
        tc = new TypeCheckerBuilder()
                .addSrcDirectory(new File("plugin-ceylon-code/test-resources/"))
                .getTypeChecker();

        tc.process();
    }

    void testDoc(String baseName, int cursorOffset) throws IOException {
        testDoc(baseName, cursorOffset, "");
    }

    void testDoc(String baseName, int cursorOffset, String testName) throws IOException {
        String sourceFileName = "documentation/" + baseName + ".ceylon";
        String suffix = testName.isEmpty() ? "" : "." + testName;
        String expectedFileName = sourceFileName + suffix + ".html";

        Tree.CompilationUnit cu = tc.getPhasedUnitFromRelativePath(sourceFileName).getCompilationUnit();

        String doc = docGenerator_.get_().getDocumentation(cu, cursorOffset);

        File expectedFile = new File("plugin-ceylon-code/test-resources/" + expectedFileName);
        if (expectedFile.exists()) {
            String expectedContent = FileUtil.loadFile(expectedFile);

            assertEquals(expectedContent.replace("{VERSION}", language_.get_().getVersion()), doc);
        } else {
            System.out.println("WARNING: expected file " + expectedFileName + " does not exist, creating it");
            FileUtil.writeToFile(expectedFile, doc);
        }
    }

    public void testToplevelFunction() throws IOException {
        testDoc("toplevelFunction", 70);
        testDoc("toplevelFunction", 120, "quack");
    }

    public void testClasses() throws IOException {
        testDoc("classes", 46, "Foo");
        testDoc("classes", 197, "Woot");
    }

    public void testInferred() throws IOException {
        testDoc("inferred", 20);
    }

    public void testLiterals() throws IOException {
        testDoc("literals", 45, "String");
        testDoc("literals", 114, "Character");
        testDoc("literals", 155, "BinInt");
        testDoc("literals", 195, "HexInt");
        testDoc("literals", 230, "Float");
    }

    public void testLinks() throws IOException {
        testDoc("links", 35, "String");
    }
}
