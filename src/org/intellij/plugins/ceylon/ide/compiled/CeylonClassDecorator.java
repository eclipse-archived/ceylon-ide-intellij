package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode;
import com.intellij.ide.projectView.impl.nodes.NamedLibraryElementNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
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

/**
 * Decorates nodes in the Project View and other declarations that represent a compiled Ceylon class.
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
        if (is(clsClass, Ceylon.class)) {
            if (is(clsClass, Module.class)) {
                return "module.ceylon";
            } else if (is(clsClass, Package.class)) {
                return "package.ceylon";
            } else if (is(clsClass, Method.class)) {
                String text = clsClass.getName();
                return text.substring(0, text.length() - 1);
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

    private boolean is(ClsClassImpl clsClass, Class<?> ann) {
        return clsClass.getModifierList().findAnnotation(ann.getName()) != null;
    }

    private String getName(ClsClassImpl clsClass) {
        PsiAnnotation ann = clsClass.getModifierList().findAnnotation(Name.class.getName());

        if (ann != null) {
            PsiNameValuePair[] attributes = ann.getParameterList().getAttributes();
            if (attributes.length == 1) {
                PsiAnnotationMemberValue value = attributes[0].getValue();
                if (value instanceof PsiLiteralExpression) {
                    return ((PsiLiteralExpression) value).getValue().toString();
                }
            }
        } else if (clsClass.getName().endsWith("_")) {
            return clsClass.getName().substring(0, clsClass.getName().length() - 1);
        }

        return clsClass.getName();
    }

    @Override
    public void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {
    }

    @Override
    public ItemPresentation getPresentation(@NotNull final ClsClassImpl item) {
        if (decompilerUtil.hasValidCeylonBinaryData(item.getContainingFile().getVirtualFile())) {
            final String presentableText = getPresentableText(item);
            if (presentableText != null) {
                return new ItemPresentation() {
                    @Nullable
                    @Override
                    public String getPresentableText() {
                        return presentableText;
                    }

                    @Nullable
                    @Override
                    public String getLocationString() {
                        PsiQualifiedNamedElement container = item.getContainer();
                        if (container != null) {
                            return "(" + container.getQualifiedName() + ")";
                        }
                        return null;
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
