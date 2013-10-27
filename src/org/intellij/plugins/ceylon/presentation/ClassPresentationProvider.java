package org.intellij.plugins.ceylon.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.util.PlatformIcons;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ClassPresentationProvider implements ItemPresentationProvider<CeylonPsi.IdentifierPsi> {
    @Override
    public ItemPresentation getPresentation(final CeylonPsi.IdentifierPsi item) {
        if (item.getParent() instanceof CeylonClass) {
            final CeylonClass myClass = (CeylonClass) item.getParent();

            return new ItemPresentation() {
                @Nullable
                @Override
                public String getPresentableText() {
                    StringBuilder text = new StringBuilder(item.getText());
                    CeylonClass parentClass = myClass;

                    while ((parentClass = parentClass.getParentClass()) != null) {
                        text.append(" in ").append(parentClass.getName());
                    }

                    return text.toString();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    if (item.getContainingFile() instanceof CeylonFile) {
                        return ((CeylonFile) item.getContainingFile()).getPackageName();
                    }

                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean unused) {
                    if (myClass != null) {
                        return myClass.isInterface() ? PlatformIcons.INTERFACE_ICON : PlatformIcons.CLASS_ICON;
                    }

                    return PlatformIcons.ERROR_INTRODUCTION_ICON;
                }
            };
        }

        return null;
    }
}
