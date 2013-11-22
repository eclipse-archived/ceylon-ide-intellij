package org.intellij.plugins.ceylon.lang;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;

/**
 * Injects the Ceylon language into backticked strings.
 */
public class BacktickLanguageInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (host instanceof CeylonPsi.MetaLiteralPsi && host.getTextLength() > 2) {
            TextRange range = new TextRange(1, host.getTextLength() - 1);
            injectionPlacesRegistrar.addPlace(CeylonLanguage.INSTANCE, range, null, ";");
        }
    }
}
