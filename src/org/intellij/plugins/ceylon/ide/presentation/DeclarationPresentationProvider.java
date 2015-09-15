package org.intellij.plugins.ceylon.ide.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.DeclarationPsiNameIdOwner;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DeclarationPresentationProvider implements ItemPresentationProvider<DeclarationPsiNameIdOwner> {

    @Override
    public ItemPresentation getPresentation(@NotNull final DeclarationPsiNameIdOwner item) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return item.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                Declaration model = item.getCeylonNode().getDeclarationModel();
                return model == null ? null : "(" + model.getContainer().getQualifiedNameString() + ")";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ideaIcons_.get_().forDeclaration(item.getCeylonNode());
            }
        };
    }
}
