package org.intellij.plugins.ceylon.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.util.PlatformIcons;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
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
                    Tree.ClassOrInterface parentClass = (Tree.ClassOrInterface) myClass.getCeylonNode();

                    if (parentClass != null) {
                        ClassOrInterface model = parentClass.getDeclarationModel();

                        // TODO model is always null (need to use DeclarationVisitor?)
                        if (model != null && model.getSuperTypeDeclarations() != null) {
                            for (TypeDeclaration superType : model.getSuperTypeDeclarations()) {
                                text.append(" in ").append(superType.getName());
                            }
                        }
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
