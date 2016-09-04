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
import com.intellij.psi.presentation.java.ClassPresentationProvider;
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
import java.util.Arrays;
import java.util.Comparator;

/**
 * Decorates results in Navigate > Class for compiled Ceylon classes.
 * Decorates nodes in the Project View that represent compiled Ceylon classes.
 */
public class CeylonClassDecorator
        implements ProjectViewNodeDecorator, ItemPresentationProvider<ClsClassImpl> {

    private static final classFileDecompilerUtil_ decompilerUtil = classFileDecompilerUtil_.get_();

    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        if (node instanceof ClassTreeNode) {
            PsiClass psiClass = ((ClassTreeNode) node).getPsiClass();
            if (psiClass instanceof ClsClassImpl
                    && decompilerUtil.hasValidCeylonBinaryData(psiClass.getContainingFile().getVirtualFile())) {
                ClsClassImpl clsClass = (ClsClassImpl) psiClass;

                String presentableText = getPresentableText(clsClass);
                if (presentableText != null) {
                    data.setPresentableText(presentableText);
                }

                if (psiClass.isDeprecated()) {
                    data.setAttributesKey(CodeInsightColors.DEPRECATED_ATTRIBUTES);
                }

                Icon icon = icons_.get_().forClass(clsClass);
                if (icon != null) {
                    data.setIcon(icon);
                }
            }
        } else if (node.getParentDescriptor() instanceof NamedLibraryElementNode
                && ((NamedLibraryElementNode) node.getParentDescriptor()).getName().startsWith("Ceylon: ")) {
            node.getParentDescriptor().setIcon(icons_.get_().getModuleArchives());
        }
    }

    @Nullable
    private String getPresentableText(ClsClassImpl clsClass) {
        if (is(clsClass, Ceylon.class)
                || clsClass.getName().endsWith("$impl")) {
            if (is(clsClass, Module.class)) {
                return "module.ceylon";
            } else if (is(clsClass, Package.class)) {
                return "package.ceylon";
            } else if (is(clsClass, Method.class)) {
                String text = clsClass.getName();
                String name = text.substring(0, text.length() - 1);
                PsiMethod[] methods = clsClass.findMethodsByName(name, false);
                Arrays.sort(methods,
                        new Comparator<PsiMethod>() {
                            @Override
                            public int compare(PsiMethod x, PsiMethod y) {
                                return Integer.compare(
                                        y.getParameterList().getParametersCount(),
                                        x.getParameterList().getParametersCount());
                            }
                        });
                PsiMethod clsMethod = methods[0];
                return name + '(' + CeylonMethodDecorator.parameters(clsMethod) + ')';
            } else if (is(clsClass, Object.class)) {
                return getName(clsClass);
            } else if (is(clsClass, Attribute.class)) {
                return getName(clsClass);
            } else {
                return getName(clsClass);
            }
        }
        return null;
    }

    static boolean is(PsiClass clsClass, Class<?> ann) {
        return clsClass.getModifierList().findAnnotation(ann.getName()) != null;
    }

    static String getName(PsiClass clsClass) {

        PsiAnnotation ann = nameAnnotation(clsClass);

        String name = clsClass.getName();

        if (ann != null) {
            name = nameValue(ann);
        } else if (name.endsWith("_")) {
            name = name.substring(0, name.length() - 1);
        } else if (name.endsWith("$impl")) {
            name = name.substring(0, name.length() - 5);
        }

        PsiClass outer = clsClass.getContainingClass();
        return outer != null ? getName(outer) + '.' + name : name;
    }

    static PsiAnnotation nameAnnotation(PsiModifierListOwner owner) {
        return owner.getModifierList().findAnnotation(Name.class.getName());
    }

    static String nameValue(PsiAnnotation nameAnnotation) {
        PsiLiteralExpression value =
                (PsiLiteralExpression)
                        nameAnnotation.findAttributeValue(
                                PsiAnnotation.DEFAULT_REFERENCED_METHOD_NAME);
        return value.getValue().toString();
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {}

    @Override
    public ItemPresentation getPresentation(@NotNull final ClsClassImpl item) {
        if (decompilerUtil.hasValidCeylonBinaryData(item.getContainingFile().getVirtualFile())
                || item.getName().endsWith("$impl")
                || item.getContainingClass()!=null
                && item.getContainingClass().getName().endsWith("$impl")) {
            final String presentableText = getPresentableText(item);
            if (presentableText != null) {
                return new ColoredItemPresentation() {
                    @Nullable
                    @Override
                    public TextAttributesKey getTextAttributesKey() {
                        if (item.isDeprecated()) {
                            return CodeInsightColors.DEPRECATED_ATTRIBUTES;
                        }
                        String name = item.getName();
                        if (name.endsWith("_")) { //TODO: better to check for the Ceylon annotations?
                            for (PsiMethod method : item.findMethodsByName(name.substring(0, name.length()-1), false)) {
                                if (method.isDeprecated()) {
                                    return CodeInsightColors.DEPRECATED_ATTRIBUTES;
                                }
                            }
                            for (PsiMethod method : item.findMethodsByName("get_", false)) {
                                if (method.isDeprecated()) {
                                    return CodeInsightColors.DEPRECATED_ATTRIBUTES;
                                }
                            }
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
                        return icons_.get_().forClass(item);
                    }
                };
            }
        }

        // we can't return null
        return new ClassPresentationProvider().getPresentation(item);
    }
}
