import com.intellij.ide.structureView.impl.common {
    PsiTreeElementBase
}
import com.intellij.ide.util.treeView.smartTree {
    ActionPresentationData,
    Filter,
    TreeElement
}
import com.intellij.openapi.actionSystem {
    KeyboardShortcut,
    Shortcut
}
import com.intellij.openapi.util {
    SystemInfo
}
import com.intellij.util {
    PlatformIcons {
        publicIcon
    }
}

import java.lang {
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}

"Filters out declarations that are not shared in the tool window."
shared class UnsharedDeclarationsFilter()
        satisfies /* FileStructureFilter*/ Filter {

    shared actual Boolean isVisible(TreeElement treeNode) {
        if (is PsiTreeElementBase<out Anything> treeNode,
            is CeylonPsi.DeclarationPsi element = treeNode.element) {

            return element.ceylonNode.declarationModel.shared;
        }
        return true;
    }

    reverted => false;

    presentation
            => ActionPresentationData("Hide unshared declarations", null, publicIcon);

    shared actual String name {
        return "CEYLON_HIDE_UNSHARED";
    }

    shared /*actual*/ String checkBoxText {
        return "Hide unshared declarations";
    }

    shared /*actual*/ ObjectArray<Shortcut> shortcut {
        return ObjectArray<Shortcut>.with {
            KeyboardShortcut.fromString(if (SystemInfo.isMac) then "meta U" else "control U")
        };
    }
}
