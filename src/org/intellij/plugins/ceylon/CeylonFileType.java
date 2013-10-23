package org.intellij.plugins.ceylon;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
        return "Support for the Ceylon programming language";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/ceylon.png");
    }
}
