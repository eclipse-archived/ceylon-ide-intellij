import com.redhat.ceylon.ide.common.util {
    escaping
}

import java.util.regex {
    Pattern
}

shared class NameValidator {

    static value packageNamePattern = Pattern.compile("^[a-z_]\\w*(\\.[a-z_]\\w*)*$");
    static value unitNamePattern = Pattern.compile("^[a-z_]\\w*(\\.[a-z_]\\w*)*$");
    static value forbiddenWords = Pattern.compile(".*\\b(``"|".join(escaping.keywords)``)\\b.*");

    static Boolean matches(Pattern pattern, String name)
            => pattern.matcher(name).matches();

    shared static Boolean packageNameIsLegal(String packageName)
            => packageName.empty
            || matches(packageNamePattern, packageName)
            && !matches(forbiddenWords, packageName);

    shared static Boolean unitNameIsLegal(String unitName)
            => unitName.empty
            || matches(unitNamePattern, unitName)
            && !matches(forbiddenWords, unitName);

    new() {}
}
