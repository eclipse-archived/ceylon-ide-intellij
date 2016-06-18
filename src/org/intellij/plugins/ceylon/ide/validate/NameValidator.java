package org.intellij.plugins.ceylon.ide.validate;

import ceylon.interop.java.JavaIterable;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.ide.common.util.escaping_;

import java.util.regex.Pattern;

/**
 * Contains static methods to validate Ceylon compilation unit names
 */
public class NameValidator {

    private static final String keywordsExpression;

    static {
        StringBuilder sb = new StringBuilder();

        JavaIterable<ceylon.language.String> it = new JavaIterable<>(TypeDescriptor.klass(String.class),
                escaping_.get_().getKeywords());
        for (ceylon.language.String kw : it) {
            sb.append(kw).append('|');
        }
        sb.setLength(sb.length() - 1);
        keywordsExpression = sb.toString();
    }

    private static final Pattern packageNamePattern = Pattern.compile("^[a-z_]\\w*(\\.[a-z_]\\w*)*$");
    private static final Pattern unitNamePattern = Pattern.compile("^[a-z_]\\w*(\\.[a-z_]\\w*)*$");
    private static final Pattern forbiddenWords = Pattern.compile(".*\\b(" + keywordsExpression + ")\\b.*");

    public static boolean packageNameIsLegal(String packageName) {
        return packageName.isEmpty() ||
                matches(packageNamePattern, packageName) &&
                !matches(forbiddenWords, packageName);
    }

    public static boolean unitNameIsLegal(String unitName) {
        return unitName.isEmpty() ||
                matches(unitNamePattern, unitName) &&
                !matches(forbiddenWords, unitName);
    }

    private static boolean matches(Pattern pattern, String name) {
        return pattern.matcher(name).matches();
    }

}
