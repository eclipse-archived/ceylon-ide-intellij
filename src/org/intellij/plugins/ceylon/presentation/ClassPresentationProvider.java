package org.intellij.plugins.ceylon.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.compiler.typechecker.model.*;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ClassPresentationProvider implements ItemPresentationProvider<CeylonPsi.IdentifierPsi> {
    @Override
    public ItemPresentation getPresentation(final CeylonPsi.IdentifierPsi item) {
        if (item.getParent() instanceof CeylonClass) {
            final CeylonClass myClass = (CeylonClass) item.getParent();
            final Tree.ClassOrInterface parentClass = (Tree.ClassOrInterface) myClass.getCeylonNode();

            return new ItemPresentation() {
                @Nullable
                @Override
                public String getPresentableText() {
                    StringBuilder text = new StringBuilder(item.getText());

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
