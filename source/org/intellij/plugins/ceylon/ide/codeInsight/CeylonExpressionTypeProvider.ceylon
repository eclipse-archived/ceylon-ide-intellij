import com.intellij.lang {
    ExpressionTypeProvider
}
import com.intellij.openapi.util.text {
    StringUtil
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil {
        getParentOfType
    }
}

import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}

shared class CeylonExpressionTypeProvider()
        extends ExpressionTypeProvider<CeylonPsi.TermPsi|CeylonPsi.LocalModifierPsi>() {

    function type(CeylonPsi.TermPsi|CeylonPsi.LocalModifierPsi psi)
            => if (is CeylonPsi.TermPsi psi)
            then psi.ceylonNode?.typeModel
            else psi.ceylonNode?.typeModel;

    getInformationHint(CeylonPsi.TermPsi|CeylonPsi.LocalModifierPsi psi)
            => if (exists node = psi.ceylonNode, exists type = type(psi))
            //This highlighting doesn't work :-(
            //"<html>``highlighter.highlight(type.asString(node.unit), termPsi.project)``</html>"
            then StringUtil.escapeXml(type.asString(node.unit))
            else "";

    errorHint => "No expression found";

    shared actual List<CeylonPsi.TermPsi|CeylonPsi.LocalModifierPsi>
    getExpressionsAt(PsiElement psiElement) {
        value list = ArrayList<CeylonPsi.TermPsi|CeylonPsi.LocalModifierPsi>();
        //TODO: this doesn't work because there are never any
        //      CeylonPsi.LocalModifierPsi nodes in the tree!
        if (exists mod = getParentOfType(psiElement, `CeylonPsi.LocalModifierPsi`)) {
            list.add(mod);
        }
        else {
            variable value expression = psiElement;
            while (exists ex = getParentOfType(expression, `CeylonPsi.TermPsi`)) {
                if (!ex is CeylonPsi.ExpressionPsi, !ex in list) {
                    list.add(ex);
                }
                expression = ex;
            }
        }
        return list;
    }
}
