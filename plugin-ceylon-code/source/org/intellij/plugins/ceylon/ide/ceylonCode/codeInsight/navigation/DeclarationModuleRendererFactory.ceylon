import com.intellij.ide.util {
    ModuleRendererFactory,
    PsiElementModuleRenderer
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
import com.intellij.util.ui {
    UIUtil
}
import com.redhat.ceylon.ide.common.model {
    SourceFile
}
import com.intellij.openapi.vfs {
    VirtualFileManager
}
import com.intellij.psi {
    PsiManager
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

            if (is SourceFile unit = val.decl.unit,
                exists vfile = VirtualFileManager.instance.findFileByUrl(
                    (unit.ceylonSourceFullPath.contains("!/") then "jar" else "file") + "://" + unit.ceylonSourceFullPath),
                exists file = PsiManager.getInstance(val.project).findFile(vfile)) {

                return delegate.getListCellRendererComponent(list, file, index, isSelected, cellHasFocus);
            } else {
                value mod = val.decl.unit.\ipackage.\imodule;
                text = mod.nameAsString + "/" + mod.version;
                icon = icons.modules;
            }

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