import com.intellij.ide.util {
    ModuleRendererFactory,
    PsiElementModuleRenderer
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    JarFileSystem {
        jarFs=instance
    }
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl
}
import com.intellij.util.ui {
    UIUtil
}
import com.redhat.ceylon.ide.common.model {
    AnyProjectSourceFile
}

import java.awt {
    Component
}

import javax.swing {
    DefaultListCellRenderer,
    JList,
    SwingConstants,
    BorderFactory
}

import org.intellij.plugins.ceylon.ide.compiled {
    classFileDecompilerUtil
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared class DeclarationModuleRendererFactory() extends ModuleRendererFactory() {

    moduleRenderer => declarationListCellRenderer;

    handles(Object? element) => element is DeclarationNavigationItem|ClsClassImpl;
}

"Renders a Ceylon declaration, for example in the results of Go To Class."
shared object declarationListCellRenderer extends DefaultListCellRenderer() {

    value delegate = PsiElementModuleRenderer();

    shared actual Component getListCellRendererComponent(JList<out Object>? list, Object? val,
        Integer index, Boolean isSelected, Boolean cellHasFocus) {

        function superComponent() {
            value oldText = text;
            value oldIcon = icon;

            value cmp = super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);

            text = oldText;
            icon = oldIcon;

            border = BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.listCellHPadding);
            horizontalTextPosition = SwingConstants.left;
            background = isSelected then UIUtil.listSelectionBackground else UIUtil.listBackground;
            foreground = isSelected then UIUtil.listSelectionForeground else UIUtil.inactiveTextColor;

            return cmp;
        }
        if (is DeclarationNavigationItem val) {
            value mod = val.declaration.unit.\ipackage.\imodule;
            value text = StringBuilder();

            if (is AnyProjectSourceFile psf = val.declaration.unit,
                is Module proj = psf.resourceProject) {
                text.append(proj.name);
                icon = icons.project;
            } else {
                text.append(mod.nameAsString).append("/").append(mod.version);
                icon = icons.moduleArchives;
            }
            this.text = text.string;

            return superComponent();
        } else if (is ClsClassImpl val,
            exists vfile = val.containingFile?.virtualFile,
            classFileDecompilerUtil.hasValidCeylonBinaryData(vfile)) {

            if (exists jar = jarFs.getVirtualFileForJar(vfile)) {
                if (exists pos = jar.nameWithoutExtension.firstOccurrence('-')) {
                    text = jar.nameWithoutExtension.replaceFirst("-", "/");
                } else {
                    text = jar.name;
                }
            } else {
                text = vfile.name;
            }

            icon = icons.moduleArchives;

            return superComponent();
        } else {
            return delegate.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
        }
    }
}