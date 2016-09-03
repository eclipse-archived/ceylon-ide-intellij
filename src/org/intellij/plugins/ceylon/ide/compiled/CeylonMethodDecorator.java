package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsMethodImpl;
import com.intellij.psi.presentation.java.MethodPresentationProvider;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.classFileDecompilerUtil_;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Decorates results in Navigate > Symbol for compiled Ceylon class members.
 */
public class CeylonMethodDecorator
        implements ItemPresentationProvider<ClsMethodImpl> {

    private static final classFileDecompilerUtil_ decompilerUtil = classFileDecompilerUtil_.get_();

    @Nullable
    private String getPresentableText(ClsMethodImpl clsMethod) {
        PsiClass clsClass = clsMethod.getContainingClass();
        if (CeylonClassDecorator.is(clsClass, Ceylon.class)
                || clsClass.getName().endsWith("$impl")) {
            return CeylonClassDecorator.getName(clsClass)
                    + '.' + getName(clsMethod);
        }
        return null;
    }

    private String getName(ClsMethodImpl clsMethod) {
        PsiAnnotation ann = CeylonClassDecorator.nameAnnotation(clsMethod);

        String name = clsMethod.getName();
        if (ann != null) {
            name = CeylonClassDecorator.nameValue(ann);
        } else if (name.endsWith("_")) {
            name = name.substring(0, name.length() - 1);
        } else if (name.length()>3
                && name.startsWith("get")
                && Character.isUpperCase(name.codePointAt(3))
                && clsMethod.getParameterList().getParametersCount()==0) {
            char[] chars = Character.toChars(Character.toLowerCase(name.codePointAt(3)));
            return String.valueOf(chars) + name.substring(3 + chars.length);
        }

        return name + '(' + parameters(clsMethod) + ')';
    }

    @NotNull
    static StringBuilder parameters(PsiMethod clsMethod) {
        StringBuilder params = new StringBuilder();
        for (PsiParameter param: clsMethod.getParameterList().getParameters()) {
            PsiAnnotation pann = CeylonClassDecorator.nameAnnotation(param);
            if (pann!=null) {
                if (params.length() > 0) {
                    params.append(", ");
                }
                params.append(CeylonClassDecorator.nameValue(pann));
            }
            else if (!param.getName().startsWith("$reified$")) {
                if (params.length() > 0) {
                    params.append(", ");
                }
                params.append(param.getName());
            }
        }
        return params;
    }

    @Override
    public ItemPresentation getPresentation(@NotNull final ClsMethodImpl item) {
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
                        return icons_.get_().forMethod(item);
                    }
                };
            }
        }

        // we can't return null
        return new MethodPresentationProvider().getPresentation(item);
    }
}
