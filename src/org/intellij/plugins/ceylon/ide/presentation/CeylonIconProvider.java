package org.intellij.plugins.ceylon.ide.presentation;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CeylonIconProvider extends IconProvider {
    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element, int flags) {
        if (element instanceof CeylonFile) {
            String fileName = ((CeylonFile) element).getName();

            if (fileName.equals(ModuleManager.PACKAGE_FILE)) {
                return PlatformIcons.PACKAGE_ICON;
            } else if (fileName.equals(ModuleManager.MODULE_FILE)) {
                return PlatformIcons.CLOSED_MODULE_GROUP_ICON;
            }
        }

        return null;
    }
}
