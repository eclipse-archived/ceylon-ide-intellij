package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode;
import com.intellij.ide.projectView.impl.nodes.NamedLibraryElementNode;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.compiled.ClsMethodImpl;
import com.intellij.psi.presentation.java.ClassPresentationProvider;
import com.intellij.psi.presentation.java.MethodPresentationProvider;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.redhat.ceylon.compiler.java.metadata.*;
import com.redhat.ceylon.compiler.java.metadata.Object;
import com.redhat.ceylon.compiler.java.metadata.Package;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.classFileDecompilerUtil_;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.lang.Class;

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
        PsiAnnotation ann
                = clsMethod.getModifierList()
                    .findAnnotation(Name.class.getName());

        String name = clsMethod.getName();
        if (ann != null) {
            PsiNameValuePair[] attributes
                    = ann.getParameterList().getAttributes();
            if (attributes.length == 1) {
                PsiAnnotationMemberValue value = attributes[0].getValue();
                if (value instanceof PsiLiteralExpression) {
                    return ((PsiLiteralExpression) value).getValue().toString();
                }
            }
            return name;
        } else if (name.endsWith("_")) {
            return name.substring(0, name.length() - 1);
        } else if (name.length()>3
                && name.startsWith("get")
                && Character.isUpperCase(name.codePointAt(3))
                && clsMethod.getParameterList().getParametersCount()==0) {
            char[] chars = Character.toChars(Character.toLowerCase(name.codePointAt(3)));
            return String.valueOf(chars) + name.substring(3 + chars.length);
        }
        else {
            return name;
        }
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
