package com.redhat.ceylon.ide.util;

import com.redhat.ceylon.compiler.typechecker.model.*;
import com.redhat.ceylon.compiler.typechecker.model.Package;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.util.Arrays.asList;

public class Escaping {

    public static String escape(String suggestedName) {
        if (KEYWORDS.contains(suggestedName)) {
            return "\\i" + suggestedName;
        }
        else {
            return suggestedName;
        }
    }

    public static String escapePackageName(Package p) {
        List<String> path = p.getName();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<path.size(); i++) {
            String pathPart = path.get(i);
            if (!pathPart.isEmpty()) {
                if (KEYWORDS.contains(pathPart)) {
                    pathPart = "\\i" + pathPart;
                }
                sb.append(pathPart);
                if (i<path.size()-1) sb.append('.');
            }
        }
        return sb.toString();
    }

    public static String escapeName(DeclarationWithProximity d) {
        return escapeAliasedName(d.getDeclaration(), d.getName());
    }

    public static String escapeName(Declaration d) {
        return escapeAliasedName(d, d.getName());
    }

    public static String escapeAliasedName(Declaration d, String alias) {
        if (alias==null) {
            return "";
        }
        char c = alias.charAt(0);
        if (d instanceof TypedDeclaration &&
                (isUpperCase(c) || KEYWORDS.contains(alias))) {
            return "\\i" + alias;
        }
        else if (d instanceof TypeDeclaration &&
                isLowerCase(c) && !d.isAnonymous()) {
            return "\\I" + alias;
        }
        else {
            return alias;
        }
    }

    public static final Set<String> KEYWORDS = new LinkedHashSet<String>(asList("import", "assert",
            "alias", "class", "interface", "object", "given", "value", "assign", "void", "function",
            "assembly", "module", "package", "of", "extends", "satisfies", "abstracts", "in", "out",
            "return", "break", "continue", "throw", "if", "else", "switch", "case", "for", "while",
            "try", "catch", "finally", "this", "outer", "super", "is", "exists", "nonempty", "then",
            "dynamic", "new", "let"));

}
