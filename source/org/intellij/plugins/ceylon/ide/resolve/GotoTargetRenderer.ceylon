import com.intellij.codeInsight.navigation {
    GotoTargetRendererProvider,
    GotoTargetHandler
}
import com.intellij.ide.util {
    PsiElementListCellRenderer
}
import com.intellij.navigation {
    NavigationItem
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared class GotoTargetRenderer()
        extends PsiElementListCellRenderer<PsiElement>()
        satisfies GotoTargetRendererProvider {

    getRenderer(PsiElement psiElement, GotoTargetHandler.GotoData gotoData)
            => this;

    iconFlags => 0; //TODO?

    shared actual String getContainerText(PsiElement element, String string) {
        if (is NavigationItem element,
            exists p = element.presentation,
            exists loc = p.locationString) {
            return loc;
        }
        else if (exists parent = PsiTreeUtil.getParentOfType(element, `CeylonPsi.DeclarationPsi`)) {
            return getContainerText(parent, string);
        }
        else {
            return element.containingFile.containingDirectory.name;
        }
    }

    shared actual String getElementText(PsiElement element) {
        if (is NavigationItem element,
            exists p = element.presentation,
            exists text = p.presentableText) {
            return text;
        }
        else if (exists parent = PsiTreeUtil.getParentOfType(element, `CeylonPsi.DeclarationPsi`)) {
            return getElementText(parent);
        }
        else {
            return element.containingFile.name;
        }
    }

    shared actual Icon? getIcon(PsiElement psiElement) {
        if (is CeylonPsi.ObjectExpressionPsi psiElement) {
            return icons.objects;
        }
        else {
            return super.getIcon(psiElement);
        }
    }

}