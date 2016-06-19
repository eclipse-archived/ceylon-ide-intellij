import com.intellij.ide.util {
    ModuleRendererFactory,
    PsiElementModuleRenderer
}
import com.intellij.openapi.\imodule {
    Module
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

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

shared class DeclarationModuleRendererFactory() extends ModuleRendererFactory() {

    moduleRenderer => declarationListCellRenderer;

    handles(Object? element) => element is DeclarationNavigationItem;
}

"Renders a Ceylon declaration, for example in the results of Go To Class."
shared object declarationListCellRenderer extends DefaultListCellRenderer() {

    value delegate = PsiElementModuleRenderer();

    shared actual Component getListCellRendererComponent(JList<out Object>? list, Object? val,
        Integer index, Boolean isSelected, Boolean cellHasFocus) {

        if (is DeclarationNavigationItem val) {

            value cmp = super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);

            value mod = val.decl.unit.\ipackage.\imodule;

            value text = StringBuilder();
            if (is AnyProjectSourceFile psf = val.decl.unit,
                is Module proj = psf.resourceProject) {
                text.append(proj.name);
                icon = icons.file;
            }
            else {
                text.append(mod.nameAsString).append("/").append(mod.version);
                icon = icons.modules;
            }
            this.text = text.string;

            border = BorderFactory.createEmptyBorder(0, 0, 0, UIUtil.listCellHPadding);
            horizontalTextPosition = SwingConstants.left;
            background = isSelected then UIUtil.listSelectionBackground else UIUtil.listBackground;
            foreground = isSelected then UIUtil.listSelectionForeground else UIUtil.inactiveTextColor;

            return cmp;
        } else {
            return delegate.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
        }
    }
}