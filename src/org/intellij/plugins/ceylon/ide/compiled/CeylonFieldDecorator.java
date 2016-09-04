package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsFieldImpl;
import com.intellij.psi.presentation.java.FieldPresentationProvider;
import com.redhat.ceylon.compiler.java.metadata.Attribute;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.classFileDecompilerUtil_;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.openapi.editor.colors.CodeInsightColors.DEPRECATED_ATTRIBUTES;

/**
 * Decorates results in Navigate > Symbol for compiled Ceylon class members.
 */
public class CeylonFieldDecorator
        implements ItemPresentationProvider<ClsFieldImpl> {

    @Nullable
    private String getPresentableText(ClsFieldImpl clsField) {
        PsiClass clsClass = clsField.getContainingClass();
        if (CeylonClassDecorator.is(clsClass, Attribute.class)) {
            return getName(clsField);
        }
        if (CeylonClassDecorator.is(clsClass, Ceylon.class)
                || clsClass.getName().endsWith("$impl")) {
            return CeylonClassDecorator.getName(clsClass)
                    + '.' + getName(clsField);
        }
        return null;
    }

    private String getName(ClsFieldImpl clsField) {
        return CeylonClassDecorator.getName(clsField, clsField);
    }

    @Override
    public ItemPresentation getPresentation(@NotNull final ClsFieldImpl item) {
        VirtualFile virtualFile = item.getContainingFile().getVirtualFile();
        if (classFileDecompilerUtil_.get_()
                .hasValidCeylonBinaryData(virtualFile)
                || item.getContainingClass().getName().endsWith("$impl")) {
            final String presentableText = getPresentableText(item);
            if (presentableText != null) {
                return new ColoredItemPresentation() {
                    @Nullable
                    @Override
                    public TextAttributesKey getTextAttributesKey() {
                        return item.isDeprecated() ?
                                DEPRECATED_ATTRIBUTES :
                                null;
                    }

                    @Nullable
                    @Override
                    public String getPresentableText() {
                        return presentableText;
                    }

                    @Nullable
                    @Override
                    public String getLocationString() {
                        PsiQualifiedNamedElement container = getContainer();
                        return container != null ?
                                "(" + container.getQualifiedName() + ")" :
                                null;
                    }

                    private PsiQualifiedNamedElement getContainer() {
                        PsiFile file = item.getContainingFile();
                        if (file == null) {
                            return null;
                        }
                        else {
                            PsiDirectory dir = file.getContainingDirectory();
                            if (dir == null) {
                                return null;
                            }
                            else {
                                return JavaDirectoryService.getInstance()
                                            .getPackage(dir);
                            }
                        }
                    }

                    @Nullable
                    @Override
                    public Icon getIcon(boolean unused) {
                        return icons_.get_().forField(item);
                    }
                };
            }
        }

        // we can't return null
        return new FieldPresentationProvider().getPresentation(item);
    }
}
