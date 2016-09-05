package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsMethodImpl;
import com.intellij.psi.presentation.java.MethodPresentationProvider;
import com.redhat.ceylon.compiler.java.metadata.Attribute;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Method;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.classFileDecompilerUtil_;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.openapi.editor.colors.CodeInsightColors.DEPRECATED_ATTRIBUTES;
import static org.intellij.plugins.ceylon.ide.compiled.CeylonClassDecorator.nameAnnotation;
import static org.intellij.plugins.ceylon.ide.compiled.CeylonClassDecorator.nameValue;

/**
 * Decorates results in Navigate > Symbol for compiled Ceylon class members.
 */
public class CeylonMethodDecorator
        implements ItemPresentationProvider<ClsMethodImpl> {

    @Nullable
    private String getPresentableText(ClsMethodImpl clsMethod) {
        PsiClass clsClass = clsMethod.getContainingClass();
        if (CeylonClassDecorator.is(clsClass, Method.class)
            || CeylonClassDecorator.is(clsClass, Attribute.class)) {
            return getName(clsMethod);
        }
        if (CeylonClassDecorator.is(clsClass, Ceylon.class)
                || clsClass.getName().endsWith("$impl")) {
            return CeylonClassDecorator.getName(clsClass)
                    + '.' + getName(clsMethod);
        }
        return null;
    }

    private String getName(ClsMethodImpl clsMethod) {
        String name = CeylonClassDecorator.getName(clsMethod, clsMethod);
        return isGetter(clsMethod) || isSetter(clsMethod) ?
                valueName(name) :
                name + '(' + parameters(clsMethod) + ')';
    }

    private static boolean isSetter(ClsMethodImpl clsMethod) {
        String name = clsMethod.getName();
        return name.length() > 3
                && name.startsWith("set")
                && clsMethod.getParameterList().getParametersCount() == 1;
    }

    private static boolean isGetter(ClsMethodImpl clsMethod) {
        String name = clsMethod.getName();
        return name.length() > 3
                && name.startsWith("get")
                && clsMethod.getParameterList().getParametersCount() == 0;
    }

    @NotNull
    private String valueName(String name) {
        char[] chars = Character.toChars(Character.toLowerCase(name.codePointAt(3)));
        return String.valueOf(chars) + name.substring(3 + chars.length);
    }

    @NotNull
    static StringBuilder parameters(PsiMethod clsMethod) {
        StringBuilder params = new StringBuilder();
        for (PsiParameter param: clsMethod.getParameterList().getParameters()) {
            if (!param.getName().startsWith("$reified$")) {
                if (params.length() > 0) {
                    params.append(", ");
                }
                PsiAnnotation tann = typeInfoAnnotation(param);
                if (tann != null) {
                    String type
                            = nameValue(tann)
                            .replaceAll("[a-z]\\w*(\\.[a-z]\\w*)*::", "");
                    params.append(type).append(' ');
                }
                PsiAnnotation pann = nameAnnotation(param);
                if (pann != null) {
                    params.append(nameValue(pann));
                } else {
                    params.append(param.getName());
                }
            }
        }
        return params;
    }

    static PsiAnnotation typeInfoAnnotation(PsiModifierListOwner owner) {
        return owner.getModifierList().findAnnotation(TypeInfo.class.getName());
    }

    @Override
    public ItemPresentation getPresentation(@NotNull final ClsMethodImpl item) {
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
                        return icons_.get_().forMethod(item);
                    }
                };
            }
        }

        // we can't return null
        return new MethodPresentationProvider().getPresentation(item);
    }
}
