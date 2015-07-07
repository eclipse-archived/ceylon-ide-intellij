package org.intellij.plugins.ceylon.ide.presentation;

import com.intellij.icons.AllIcons;
import com.intellij.ide.IconProvider;
import com.intellij.openapi.util.IconLoader;
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
                return IconLoader.getIcon("/icons/package.png");
            } else if (fileName.equals(ModuleManager.MODULE_FILE)) {
                return AllIcons.Nodes.Artifact;
            }
        }

        return null;
    }
}
