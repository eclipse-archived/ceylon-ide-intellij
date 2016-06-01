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
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared class GotoTargetRenderer() satisfies GotoTargetRendererProvider {
    
    shared actual PsiElementListCellRenderer<out PsiElement> getRenderer(
        PsiElement psiElement, GotoTargetHandler.GotoData gotoData) 
            => object extends PsiElementListCellRenderer<PsiElement>() {
        
        shared actual String? getContainerText(PsiElement t, String string) 
                => if (is NavigationItem t) 
                then t.presentation?.locationString 
                else null;
        
        getElementText(PsiElement t) 
                => if (is PsiNamedElement t, exists name = t.name)
                    then name
                else if (is CeylonPsi.ObjectExpressionPsi t)
                    then "anonymous in " + t.containingFile.name
                else t.containingFile.name;
        
        iconFlags => 0;
        
        getIcon(PsiElement psiElement) 
                => if (is CeylonPsi.ObjectExpressionPsi psiElement) 
                then ideaIcons.objects 
                else super.getIcon(psiElement);
    };
}