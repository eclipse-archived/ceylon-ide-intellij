package org.intellij.plugins.ceylon.ide.ceylonCode.lang;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;

public class CeylonFileType extends LanguageFileType {

    public static final CeylonFileType INSTANCE = new CeylonFileType();
    public static final String DEFAULT_EXTENSION = "ceylon";

    protected CeylonFileType() {
        super(CeylonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Ceylon";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ceylon source file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        // can't use ideaIcons because it's compiled after this file
        return IconLoader.getIcon("/icons/ceylonFile.png");
    }
}
