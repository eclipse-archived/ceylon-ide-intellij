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

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

shared class GotoTargetRenderer() satisfies GotoTargetRendererProvider {
    
    shared actual PsiElementListCellRenderer<out PsiElement> getRenderer(
        PsiElement psiElement, GotoTargetHandler.GotoData gotoData) 
            => object extends PsiElementListCellRenderer<PsiElement>() {
        
        shared actual String? getContainerText(PsiElement t, String string) 
                => if (is NavigationItem t) 
                then t.presentation?.locationString 
                else null;
        
        shared actual String? getElementText(PsiElement t) 
                => if (is NavigationItem t)
                    then t.presentation?.presentableText
                /*else if (is CeylonPsi.ObjectExpressionPsi t)
                    then "anonymous in " + t.containingFile.name*/
                else t.containingFile.name;
        
        iconFlags => 0;
        
        shared actual Icon? getIcon(PsiElement psiElement) 
                => if (is CeylonPsi.ObjectExpressionPsi psiElement) 
                then icons.objects
                else super.getIcon(psiElement);
    };
}