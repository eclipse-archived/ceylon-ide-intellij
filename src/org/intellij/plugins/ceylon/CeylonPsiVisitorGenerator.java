package org.intellij.plugins.ceylon;

import org.intellij.plugins.ceylon.psi.CeylonPsi;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Generates CeylonPsiVisitor.java
 */
public class CeylonPsiVisitorGenerator {

    public static void main(String[] args) throws IOException {
        FileOutputStream os = new FileOutputStream("gen/org/intellij/plugins/ceylon/psi/CeylonPsiVisitor.java");

        os.write(("package org.intellij.plugins.ceylon.psi;\n" +
                "\n" +
                "import com.intellij.psi.PsiElement;\n" +
                "import com.intellij.psi.PsiElementVisitor;\n" +
                "import org.jetbrains.annotations.NotNull;\n" +
                "\n" +
                "import static org.intellij.plugins.ceylon.psi.CeylonPsi.*;\n" +
                "\n" +
                "public class CeylonPsiVisitor extends PsiElementVisitor {\n" +
                "\n" +
                "    @Override\n" +
                "    public void visitElement(PsiElement element) {\n" +
                "        super.visitElement(element);\n" +
                "\n").getBytes());

        Class<?>[] interfaces = CeylonPsi.class.getClasses();

        for (int i = 0; i < interfaces.length; i++) {
            Class iface = interfaces[i];

            os.write("        ".getBytes());
            if (i > 0) {
                os.write("else ".getBytes());
            }

            os.write(("if (element instanceof " + iface.getSimpleName() + ") {\n").getBytes());
            os.write(("            visit" + iface.getSimpleName() + "((" + iface.getSimpleName() + ") element);\n").getBytes());
            os.write(                "        }\n".getBytes());
        }

        os.write("    }\n".getBytes());

        for (Class<?> anInterface : interfaces) {
            os.write(("    public void visit" + anInterface.getSimpleName() + "(@NotNull " + anInterface.getSimpleName() + " element) {}\n").getBytes());
        }
        os.write("}\n".getBytes());
    }
}
