package org.intellij.plugins.ceylon;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Generates TokenTypes.java using tokens recognized by CeylonLexer.java.
 */
public class TokenTypesGenerator {

    public static void main(String[] args) throws IOException, IllegalAccessException {
        FileOutputStream os = new FileOutputStream("src/org/intellij/plugins/ceylon/psi/TokenTypes.java");

        os.write(("package org.intellij.plugins.ceylon.psi;\n\n" +
            "import com.intellij.psi.tree.IElementType;\n" +
            "import org.intellij.plugins.ceylon.CeylonLanguage;\n\n").getBytes());
        os.write("public enum TokenTypes {\n\n".getBytes());

        for (Field field : CeylonLexer.class.getFields()) {
            os.write(("    " + field.getName() + "(" + field.get(null) + "),\n").getBytes());
        }

        os.write("\n    ;\n\n".getBytes());

        os.write(("    private final int value;\n" +
            "    private IElementType tokenType;\n" +
            "    \n" +
            "    private TokenTypes(int value) {\n" +
            "        this.value = value;\n" +
            "        tokenType = new IElementType(name(), CeylonLanguage.INSTANCE);\n" +
            "    }\n" +
            "\n" +
            "    public IElementType getTokenType() {\n" +
            "        return tokenType;\n" +
            "    }\n" +
            "    \n" +
            "    public static IElementType fromInt(int value) {\n" +
            "        for (TokenTypes tokenType : values()) {\n" +
            "            if (tokenType.value == value) {\n" +
            "                return tokenType.tokenType;\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        return null;\n" +
            "    }").getBytes());
        os.write("\n}".getBytes());
        os.close();
    }
}
