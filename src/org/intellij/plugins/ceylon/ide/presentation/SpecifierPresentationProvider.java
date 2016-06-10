package org.intellij.plugins.ceylon.ide.presentation;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.DeclarationPsiNameIdOwner;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl.SpecifierStatementPsiIdOwner;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.ideaIcons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by gavin on 6/10/16.
 */
public class SpecifierPresentationProvider implements ItemPresentationProvider<SpecifierStatementPsiIdOwner> {

    @Override
    public ItemPresentation getPresentation(@NotNull final SpecifierStatementPsiIdOwner item) {

        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                Declaration model =
                        item.getCeylonNode()
                                .getDeclaration();
                String name = model.getName();
                //TODO: make this recursive
                if (model.isClassOrInterfaceMember()) {
                    ClassOrInterface classOrInterface =
                            (ClassOrInterface)
                                    model.getContainer();
                    return classOrInterface.getName()
                            + "." + name;
                }
                else {
                    return name;
                }
            }

            @Nullable
            @Override
            public String getLocationString() {
                Declaration model =
                        item.getCeylonNode()
                                .getDeclaration();
                if (model == null) {
                    return null;
                }
                //TODO: make this recursive
                if (model.isClassOrInterfaceMember()) {
                    model =
                            (ClassOrInterface)
                                    model.getContainer();
                }
                String qualifiedNameString =
                        model.getContainer()
                                .getQualifiedNameString();
                if (StringUtil.isEmpty(qualifiedNameString)) {
                    qualifiedNameString = "default module";
                }
                return "(" + qualifiedNameString + ")";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return ideaIcons_.get_().forDeclaration(item.getCeylonNode());
            }
        };
    }
}
