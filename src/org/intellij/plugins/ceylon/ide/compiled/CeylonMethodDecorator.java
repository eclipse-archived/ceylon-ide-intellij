package org.intellij.plugins.ceylon.ide.compiled;

import ceylon.language.Callable;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsMethodImpl;
import com.intellij.psi.presentation.java.MethodPresentationProvider;
import com.redhat.ceylon.compiler.java.metadata.*;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
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
        for (PsiParameter param :
                clsMethod.getParameterList()
                        .getParameters()) {
            if (!isTypeDescriptor(param)
                    && !isSelfParameter(clsMethod, param)) {
                if (params.length() > 0) {
                    params.append(", ");
                }
                PsiAnnotation tann = typeInfoAnnotation(param);
                if (tann != null) {
                    params.append(getAnnotatedType(param, tann));
                } else {
                    params.append(getCeylonType(param));
                }
                params.append(' ');
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

    @NotNull
    private static String getAnnotatedType(PsiParameter param, PsiAnnotation tann) {
        String type
                = nameValue(tann)
                .replaceAll("[a-z]\\w*(\\.[a-z]\\w*)*::", "");
        if (type.endsWith("[]") &&
                param.getModifierList()
                        .findAnnotation(Sequenced.class.getCanonicalName()) != null) {
            type = type.substring(0, type.length() - 2) + '*';
        }
        return type;
    }

    private static boolean isSelfParameter(PsiMethod clsMethod, PsiParameter param) {
        if (clsMethod.getModifierList()
                .hasExplicitModifier(PsiModifier.STATIC)) {
            PsiType type = param.getType();
            switch (clsMethod.getContainingClass().getQualifiedName()) {
                case "ceylon.language.String":
                    return java.lang.String.class.getCanonicalName()
                            .equals(type.getCanonicalText());
                case "ceylon.language.Integer":
                    return PsiType.LONG.equals(type);
                case "ceylon.language.Float":
                    return PsiType.DOUBLE.equals(type);
                case "ceylon.language.Byte":
                    return PsiType.BYTE.equals(type);
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    private static boolean isTypeDescriptor(PsiParameter param) {
        return param.getName().startsWith("$reified$") ||
                param.getType().getCanonicalText()
                        .equals(TypeDescriptor.class.getCanonicalName());
    }

    @NotNull
    private static String getCeylonType(PsiParameter param) {
        PsiType pt = param.getType();
        if (PsiType.BOOLEAN.equals(pt)) {
            return "Boolean";
        } else if (PsiType.LONG.equals(pt) || PsiType.INT.equals(pt)) {
            return "Integer";
        } else if (PsiType.INT.equals(pt)) {
            return "Character";
        } else if (PsiType.BYTE.equals(pt)) {
            return "Byte";
        } else if (PsiType.DOUBLE.equals(pt) || PsiType.FLOAT.equals(pt)) {
            return "Float";
        } else if (pt.getCanonicalText()
                .startsWith(Callable.class.getCanonicalName())) {
            return "Callable"; //no useful type arguments
        } else {
            return pt.getPresentableText()
                    .replace("? extends ", "")
                    .replace("? super ", "");
        }
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
