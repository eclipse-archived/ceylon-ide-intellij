import com.intellij.ide {
    IconProvider
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.ui {
    RowIcon
}
import com.intellij.util {
    PlatformIcons
}
import org.eclipse.ceylon.model.typechecker.util {
    ModuleManager
}

import javax.swing {
    Icon
}

import org.eclipse.ceylon.ide.intellij.lightpsi {
    CeyLightClass,
    CeyLightMethod
}
import org.eclipse.ceylon.ide.intellij.psi {
    CeylonFile
}
import org.eclipse.ceylon.ide.intellij.util {
    icons
}

shared class CeylonIconProvider() extends IconProvider() {

    shared actual Icon? getIcon(PsiElement element, Integer flags) {
        switch (element)
        case (is CeylonFile) {
            String fileName = element.name;
            if (fileName==ModuleManager.packageFile) {
                return icons.packageDescriptors;
            } else if (fileName==ModuleManager.moduleFile) {
                return icons.moduleDescriptors;
            }
            else {
                return null;
            }
        }
        case (is CeyLightClass) {
            return element.\iinterface
                then PlatformIcons.interfaceIcon
                else PlatformIcons.classIcon;
        }
        case (is CeyLightMethod) {
            RowIcon icon = RowIcon(2, RowIcon.Alignment.center);
            icon.setIcon(PlatformIcons.methodIcon, 0);
            icon.setIcon(PlatformIcons.publicIcon, 1);
            return icon;
        }
        else {
            return null;
        }
    }
    
}
