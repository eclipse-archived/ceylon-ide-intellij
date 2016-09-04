package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.nodes.ClassTreeNode;
import com.intellij.ide.projectView.impl.nodes.NamedLibraryElementNode;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.ItemPresentationProvider;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.vfs.VirtualFile;
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

import static com.intellij.openapi.editor.colors.CodeInsightColors.DEPRECATED_ATTRIBUTES;
import static org.intellij.plugins.ceylon.ide.compiled.CeylonMethodDecorator.parameters;

/**
 * Decorates results in Navigate > Class for compiled Ceylon classes.
 * Decorates nodes in the Project View that represent compiled Ceylon classes.
 */
public class CeylonClassDecorator
        implements ProjectViewNodeDecorator,
        ItemPresentationProvider<ClsClassImpl> {

    @Override
    public void decorate(ProjectViewNode node, PresentationData data) {
        if (node instanceof ClassTreeNode) {
            ClassTreeNode classTreeNode = (ClassTreeNode) node;
            PsiClass psiClass = classTreeNode.getPsiClass();
            if (psiClass instanceof ClsClassImpl) {
                ClsClassImpl clsClass = (ClsClassImpl) psiClass;
                VirtualFile virtualFile
                        = psiClass.getContainingFile()
                        .getVirtualFile();
                if (classFileDecompilerUtil_.get_()
                        .hasValidCeylonBinaryData(virtualFile)) {

                    String presentableText = getPresentableText(clsClass);
                    if (presentableText != null) {
                        data.setPresentableText(presentableText);
                    }

                    if (psiClass.isDeprecated()) {
                        data.setAttributesKey(DEPRECATED_ATTRIBUTES);
                    }

                    Icon icon = icons_.get_().forClass(clsClass);
                    if (icon != null) {
                        data.setIcon(icon);
                    }
                }
            }
            return;
        }

        NodeDescriptor parentDescriptor = node.getParentDescriptor();
        if (parentDescriptor instanceof NamedLibraryElementNode) {
            NamedLibraryElementNode descriptor
                    = (NamedLibraryElementNode) parentDescriptor;
            if (descriptor.getName().startsWith("Ceylon: ")) {
                parentDescriptor.setIcon(icons_.get_().getModuleArchives());
            }
        }
    }

    @Nullable
    private String getPresentableText(ClsClassImpl clsClass) {
        if (is(clsClass, Ceylon.class)
                || clsClass.getName().endsWith("$impl")
                || clsClass.getName().endsWith("$annotation$")) {
            if (is(clsClass, Module.class)) {
                return "module.ceylon";
            } else if (is(clsClass, Package.class)) {
                return "package.ceylon";
            } else if (is(clsClass, Method.class)) {
                String text = clsClass.getName();
                String name = text.substring(0, text.length() - 1);
                PsiMethod[] methods = clsClass.findMethodsByName(name, false);
                if (methods.length==0) {
                    methods = clsClass.findMethodsByName('$' + name, false);
                }
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
                return name + '(' + parameters(clsMethod) + ')';
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

    static String getName(PsiNameIdentifierOwner named,
                          PsiModifierListOwner annotated) {
        PsiAnnotation ann = nameAnnotation(annotated);
        if (ann != null) {
            return nameValue(ann);
        } else {
            String name = named.getName();
            return name.endsWith("_") ?
                    name.substring(0, name.length() - 1) :
                    name;
        }
    }

    static String getName(PsiClass clsClass) {

        String name = getName(clsClass, clsClass);
        if (name.endsWith("$impl")) {
            name = name.substring(0, name.length() - 5);
        } else if (name.endsWith("$annotation$")) {
            name = name.substring(0, name.length() - 12);
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
        VirtualFile virtualFile = item.getContainingFile().getVirtualFile();
        if (classFileDecompilerUtil_.get_()
                .hasValidCeylonBinaryData(virtualFile)
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
                            return DEPRECATED_ATTRIBUTES;
                        }
                        String name = item.getName();
                        if (name.endsWith("_")) { //TODO: better to check for the Ceylon annotations?
                            String realName = name.substring(0, name.length() - 1);
                            for (PsiMethod method : item.findMethodsByName(realName, false)) {
                                if (method.isDeprecated()) {
                                    return DEPRECATED_ATTRIBUTES;
                                }
                            }
                            for (PsiMethod method : item.findMethodsByName("get_", false)) {
                                if (method.isDeprecated()) {
                                    return DEPRECATED_ATTRIBUTES;
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
                        return icons_.get_().forClass(item);
                    }
                };
            }
        }

        // we can't return null
        return new ClassPresentationProvider().getPresentation(item);
    }
}
