package org.intellij.plugins.ceylon.ide.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import org.intellij.plugins.ceylon.ide.psi.CeylonClass;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ClassPresentationProvider implements ItemPresentationProvider<CeylonClass> {
    @Override
    public ItemPresentation getPresentation(final CeylonClass item) {
        final Tree.ClassOrInterface parentClass = item.getCeylonNode();

        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                StringBuilder text = new StringBuilder(item.getQualifiedName());

                if (parentClass != null) {
                    ClassOrInterface model = parentClass.getDeclarationModel();

                    if (model != null) {
                        Scope container = model.getContainer();

                        while (container instanceof TypeDeclaration) {
                            text.append(" in ").append(((TypeDeclaration) container).getName());
                            container = container.getContainer();
                        }
                    }
                }

                return text.toString();
            }

            @Nullable
            @Override
            public String getLocationString() {
                if (parentClass != null) {
                    return "(" + parentClass.getUnit().getPackage().getModule().getNameAsString() + ")";
                }

                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                if (item != null) {
                    return item.isInterface() ? PlatformIcons.INTERFACE_ICON : PlatformIcons.CLASS_ICON;
                }

                return PlatformIcons.ERROR_INTRODUCTION_ICON;
            }
        };
    }
}
