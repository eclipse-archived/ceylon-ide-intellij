package org.intellij.plugins.ceylon.ide.highlighting;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.AbstractCeylonColorSettingsPage;
import org.jetbrains.annotations.NotNull;

public class CeylonColorSettingsPage extends AbstractCeylonColorSettingsPage {

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new CeylonHighlighter();
    }
}
