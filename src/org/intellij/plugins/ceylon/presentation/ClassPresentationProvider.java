package org.intellij.plugins.ceylon.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonTypeName;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ClassPresentationProvider implements ItemPresentationProvider<CeylonTypeName> {
    @Override
    public ItemPresentation getPresentation(final CeylonTypeName item) {

        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                String text = item.getText();

                // TODO not nice, make it recursive
                CeylonClass parentClass = PsiTreeUtil.getParentOfType(item.getParent().getParent(), CeylonClass.class);

                if (parentClass != null) {
                    text += " in " + parentClass.getNameIdentifier().getText();
                }

                return text;
            }

            @Nullable
            @Override
            public String getLocationString() {
                PsiElement aClass = item.getParent().getParent();

                if (aClass instanceof CeylonClass) {
                    return "(" + ((CeylonClass) aClass).getQualifiedName() + ")";
                }
                return "(" + item.getContainingFile().getName() + ")";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                PsiElement aClass = item.getParent().getParent();

                if (aClass instanceof CeylonClass) {
                    return ((CeylonClass) aClass).isInterface() ? PlatformIcons.INTERFACE_ICON : PlatformIcons.CLASS_ICON;
                }

                return PlatformIcons.ERROR_INTRODUCTION_ICON;
            }
        };
    }
}
