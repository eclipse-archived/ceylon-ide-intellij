package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsFieldImpl;
import com.intellij.psi.presentation.java.FieldPresentationProvider;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.classFileDecompilerUtil_;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Decorates results in Navigate > Symbol for compiled Ceylon class members.
 */
public class CeylonFieldDecorator
        implements ItemPresentationProvider<ClsFieldImpl> {

    private static final classFileDecompilerUtil_ decompilerUtil = classFileDecompilerUtil_.get_();

    @Nullable
    private String getPresentableText(ClsFieldImpl clsMethod) {
        PsiClass clsClass = clsMethod.getContainingClass();
        if (CeylonClassDecorator.is(clsClass, Ceylon.class)
                || clsClass.getName().endsWith("$impl")) {
            return CeylonClassDecorator.getName(clsClass)
                    + '.' + getName(clsMethod);
        }
        return null;
    }

    private String getName(ClsFieldImpl clsMethod) {
        PsiAnnotation ann = CeylonClassDecorator.nameAnnotation(clsMethod);

        String name = clsMethod.getName();
        if (ann != null) {
            name = CeylonClassDecorator.nameValue(ann);
        } else if (name.endsWith("_")) {
            name = name.substring(0, name.length() - 1);
        }

        return name;
    }

    @Override
    public ItemPresentation getPresentation(@NotNull final ClsFieldImpl item) {
        if (decompilerUtil.hasValidCeylonBinaryData(item.getContainingFile().getVirtualFile())
                || item.getContainingClass().getName().endsWith("$impl")) {
            final String presentableText = getPresentableText(item);
            if (presentableText != null) {
                return new ColoredItemPresentation() {
                    @Nullable
                    @Override
                    public TextAttributesKey getTextAttributesKey() {
                        if (item.isDeprecated()) {
                            return CodeInsightColors.DEPRECATED_ATTRIBUTES;
                        }
                        return null;
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
                        if (container != null) {
                            return "(" + container.getQualifiedName() + ")";
                        }
                        return null;
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
