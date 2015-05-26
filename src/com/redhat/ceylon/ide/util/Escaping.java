package com.redhat.ceylon.ide.util;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class Escaping {

    public static final Set<String> KEYWORDS = new LinkedHashSet<String>(asList("import", "assert",
            "alias", "class", "interface", "object", "given", "value", "assign", "void", "function",
            "assembly", "module", "package", "of", "extends", "satisfies", "abstracts", "in", "out",
            "return", "break", "continue", "throw", "if", "else", "switch", "case", "for", "while",
            "try", "catch", "finally", "this", "outer", "super", "is", "exists", "nonempty", "then",
            "dynamic", "new", "let"));

}
