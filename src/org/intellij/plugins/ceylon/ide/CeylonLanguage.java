package org.intellij.plugins.ceylon.ide;

import com.intellij.lang.Language;

public class CeylonLanguage extends Language {

    public static final CeylonLanguage INSTANCE = new CeylonLanguage();

    protected CeylonLanguage() {
        super("Ceylon", "text/ceylon");
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
