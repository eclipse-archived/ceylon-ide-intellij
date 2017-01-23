import org.intellij.plugins.ceylon.ide.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.psi.CeylonTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Generates CeylonPsiVisitor.java
 */
public class CeylonPsiVisitorGenerator {

    public static void main(String[] args) throws IOException {
        FileOutputStream os = new FileOutputStream(new File(args[0], "org/intellij/plugins/ceylon/ide/psi/CeylonPsiVisitor.java"));

        os.write(("package org.intellij.plugins.ceylon.ide.psi;\n" +
                "\n" +
                "import com.intellij.psi.PsiElement;\n" +
                "import com.intellij.psi.PsiElementVisitor;\n" +
                "import org.jetbrains.annotations.NotNull;\n" +
                "\n" +
                "import static org.intellij.plugins.ceylon.ide.psi.CeylonPsi.*;\n" +
                "\n" +
                "public class CeylonPsiVisitor extends PsiElementVisitor {\n" +
                "\n" +
                "    private boolean recursive;\n" +
                "\n" +
                "    public CeylonPsiVisitor(boolean recursive) {\n" +
                "        this.recursive = recursive;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void visitElement(PsiElement element) {\n" +
                "        super.visitElement(element);\n" +
                "        if (false) {\n" +
                "\n").getBytes());

        Class<?>[] interfaces = CeylonPsi.class.getClasses();


        for (Field field : CeylonTypes.class.getDeclaredFields()) {
            if (field.getName().equals("CEYLON_FILE")) {
                continue;
            }

            String psiClass = toCamelCase(field.getName()) + "Psi";

            os.write(("        } else if (element instanceof " + psiClass + ") {\n").getBytes());
            os.write(("            visit" + psiClass + "((" + psiClass + ") element);\n").getBytes());
        }

        os.write("        }\n\n".getBytes());
        os.write("        if (recursive) {\n".getBytes());
        os.write("            element.acceptChildren(this);\n".getBytes());
        os.write("        }\n".getBytes());
        os.write("    }\n".getBytes());

        for (Class<?> anInterface : interfaces) {
            Class<?>[] parentInterfaces = anInterface.getInterfaces();
            String parentCall = shouldCallParent(parentInterfaces) ? String.format(" visit%s(element); ", parentInterfaces[0].getSimpleName()) : "";
            os.write(("    public void visit" + anInterface.getSimpleName() + "(@NotNull " + anInterface.getSimpleName() + " element) {" + parentCall + "}\n").getBytes());
        }
        os.write("}\n".getBytes());
    }

    private static boolean shouldCallParent(Class<?>[] parentInterfaces) {
        return parentInterfaces.length == 1 && parentInterfaces[0] != CeylonCompositeElement.class;
    }

    static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toPwoperCase(part);
        }
        return camelCaseString;
    }

    static String toPwoperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
}
