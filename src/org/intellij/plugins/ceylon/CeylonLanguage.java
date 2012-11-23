package org.intellij.plugins.ceylon;

import com.intellij.lang.Language;

public class CeylonLanguage extends Language {

    public static final CeylonLanguage INSTANCE = new CeylonLanguage();

    protected CeylonLanguage() {
        super("Ceylon", "text/ceylon");
    }
}
