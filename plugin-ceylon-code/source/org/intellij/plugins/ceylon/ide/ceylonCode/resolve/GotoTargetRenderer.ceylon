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
    PsiElement,
    PsiNamedElement
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}
import javax.swing {
    Icon
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared class GotoTargetRenderer() satisfies GotoTargetRendererProvider {
    
    shared actual PsiElementListCellRenderer<out PsiElement> getRenderer(
        PsiElement psiElement, GotoTargetHandler.GotoData gotoData) {

        return object extends PsiElementListCellRenderer<PsiElement>() {

            shared actual String? getContainerText(PsiElement t, String string) {
                if (is NavigationItem t) {
                    return t.presentation?.locationString;
                }
                return null;
            }
            
            shared actual String getElementText(PsiElement t) {
                if (is PsiNamedElement t, exists name = t.name) {
                    return name;
                }
                if (is CeylonPsi.ObjectExpressionPsi t) {
                    return "anonymous in " + t.containingFile.name;
                }
                return t.containingFile.name;
            }
            
            shared actual Integer iconFlags => 0;
            
            shared actual Icon getIcon(PsiElement psiElement) { 
                if (is CeylonPsi.ObjectExpressionPsi psiElement) {
                    return ideaIcons.objects;
                }
                return super.getIcon(psiElement);
            }
        };
    }
}