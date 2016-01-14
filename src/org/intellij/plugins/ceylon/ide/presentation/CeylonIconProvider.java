package org.intellij.plugins.ceylon.ide.presentation;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.lightpsi.CeyLightClass;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
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
                return ideaIcons_.get_().getPackages();
            } else if (fileName.equals(ModuleManager.MODULE_FILE)) {
                return ideaIcons_.get_().getModules();
            }
        }

        if (element instanceof CeyLightClass) {
            return ((CeyLightClass) element).isInterface() ? PlatformIcons.INTERFACE_ICON : PlatformIcons.CLASS_ICON;
        }
        return null;
    }
}
