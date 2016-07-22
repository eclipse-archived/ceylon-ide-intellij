import com.intellij.ide.projectView {
    PresentationData,
    ProjectViewNode,
    ProjectViewNodeDecorator
}
import com.intellij.ide.projectView.impl.nodes {
    ClassTreeNode,
    NamedLibraryElementNode
}
import com.intellij.navigation {
    ItemPresentation,
    ItemPresentationProvider
}
import com.intellij.packageDependencies.ui {
    PackageDependenciesNode
}
import com.intellij.psi {
    PsiLiteralExpression
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl
}
import com.intellij.psi.presentation.java {
    ClassPresentationProvider
}
import com.intellij.ui {
    ColoredTreeCellRenderer
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    Annotations
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

shared class CeylonClassDecorator()
        satisfies ProjectViewNodeDecorator
                & ItemPresentationProvider<ClsClassImpl> {

    function has(ClsClassImpl clsClass, Annotations ann)
            => clsClass.modifierList.findAnnotation(ann.className) exists;

    function getName(ClsClassImpl clsClass) {
        if (exists ann
                = clsClass.modifierList
                    .findAnnotation(Annotations.name.className),
            exists att = ann.parameterList.attributes[0],
            is PsiLiteralExpression str = att.\ivalue) {
            return str.\ivalue?.string;
        }
        else {
            return clsClass.name.removeTerminal("_");
        }
    }

    function getPresentableText(ClsClassImpl clsClass) {
        if (has(clsClass, Annotations.ceylon)) {
            if (has(clsClass, Annotations.moduleDescriptor)) {
                return "module.ceylon";
            } else if (has(clsClass, Annotations.packageDescriptor)) {
                return "package.ceylon";
            } else if (has(clsClass, Annotations.method)) {
                String text = clsClass.name;
                return text.substring(0, text.size - 1);
            } else if (has(clsClass, Annotations.\iobject)) {
                return getName(clsClass);
            } else if (has(clsClass, Annotations.attribute)) {
                return getName(clsClass);
            } else {
                return getName(clsClass);
            }
        }
        else {
            return null;
        }
    }

    shared actual void decorate(PackageDependenciesNode node, ColoredTreeCellRenderer cellRenderer) {}

    shared actual void decorate(ProjectViewNode<out Object> node, PresentationData data) {
        if (is ClassTreeNode node) {
            if (is ClsClassImpl psiClass = node.psiClass,
                classFileDecompilerUtil.hasValidCeylonBinaryData(psiClass.containingFile.virtualFile)) {
                if (exists presentableText = getPresentableText(psiClass)) {
                    data.presentableText = presentableText;
                }
                if (exists icon = icons.forClass(psiClass)) {
                    data.setIcon(icon);
                }
            }
        } else if (is NamedLibraryElementNode pd = node.parentDescriptor,
            pd.name.startsWith("Ceylon: ")) {
            node.parentDescriptor.icon = icons.moduleArchives;
        }
    }

    shared actual ItemPresentation getPresentation(ClsClassImpl item) {
        if (classFileDecompilerUtil.hasValidCeylonBinaryData(item.containingFile.virtualFile),
            exists text = getPresentableText(item)) {
            return object satisfies ItemPresentation {
                presentableText => text;
                locationString
                        => if (exists container = item.container,
                               exists name = container.qualifiedName)
                        then "(``name``)"
                        else null;

                getIcon(Boolean unused) => icons.forClass(item);
            };
        }
        else {
            return ClassPresentationProvider().getPresentation(item);
        }
    }
}
