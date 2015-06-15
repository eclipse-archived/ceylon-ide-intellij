import ceylon.test {
    test,
    assertEquals
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import java.io {
    JFile = File
}
import ceylon.file {
    parsePath,
    File,
    Nil,
    lines
}
import org.intellij.plugins.ceylon.ide.ceylonCode.doc {
    docGenerator
}
import com.redhat.ceylon.compiler.typechecker {
    TypeCheckerBuilder,
    TypeChecker
}

TypeChecker tc = buildTc();

TypeChecker buildTc() {
    TypeChecker tc = TypeCheckerBuilder()
            .addSrcDirectory(JFile("test-resources/"))
            .typeChecker;
    
    tc.process();
    
    return tc;    
}

void testDoc(String baseName, Integer cursorOffset, String testName = "") {
    value sourceFileName = "documentation/``baseName``.ceylon";
    value suffix = if (!testName.empty) then "." + testName else "";
    value expectedFileName = sourceFileName + suffix + ".html";
    
    if (is File sourceFile = parsePath("test-resources/" + sourceFileName).resource) {
        Tree.CompilationUnit cu = tc.getPhasedUnitFromRelativePath(sourceFileName).compilationUnit;
        
        value doc = docGenerator.getDocumentation(cu, cursorOffset)?.string else "<no doc>";
        
        value expectedResource = parsePath("test-resources/" + expectedFileName).resource;
        if (is File expectedFile = expectedResource) {
            value expectedContent = "\n".join(lines(expectedFile));
            
            assertEquals(doc, expectedContent.replace("{VERSION}", language.version));
        } else if (is Nil expectedResource) {
            print("WARNING: expected file ``expectedFileName`` does not exist, creating it");
            value file = expectedResource.createFile();
            
            value writer = file.Overwriter();
            writer.write(doc);
            writer.close();
        }
    }
}

test shared void toplevelFunction() {
    testDoc("toplevelFunction", 70);
    testDoc("toplevelFunction", 120, "quack");
}

test shared void classes() {
    testDoc("classes", 46, "Foo");
    testDoc("classes", 197, "Woot");
}

test shared void inferred() {
    testDoc("inferred", 20);
}

test shared void literals() {
    testDoc("literals", 45, "String");
    testDoc("literals", 114, "Character");
    testDoc("literals", 155, "BinInt");
    testDoc("literals", 195, "HexInt");
    testDoc("literals", 230, "Float");
}
