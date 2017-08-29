package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.lang.Language;

/**
 * Created by gavin on 7/6/16.
 */
public class CeylonLanguage extends Language {

    public static final CeylonLanguage INSTANCE = new CeylonLanguage();

    private CeylonLanguage() {
        super("Ceylon", "text/ceylon");
    }

    @Override
    public boolean isCaseSensitive() {
        return true;
    }
}
