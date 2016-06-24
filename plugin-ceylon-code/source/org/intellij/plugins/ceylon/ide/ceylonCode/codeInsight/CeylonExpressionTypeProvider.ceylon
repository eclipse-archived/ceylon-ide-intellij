import ceylon.interop.java {
    javaClass
}

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
    PsiTreeUtil
}

import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}

shared class CeylonExpressionTypeProvider() extends ExpressionTypeProvider<CeylonPsi.TermPsi>() {

    getInformationHint(CeylonPsi.TermPsi termPsi)
            => if (exists node = termPsi.ceylonNode,
                   exists type = node.typeModel)
            then StringUtil.escapeXml(type.asString(node.unit))
            else null;

    errorHint => "No expression found";

    shared actual List<CeylonPsi.TermPsi> getExpressionsAt(PsiElement psiElement) {
        value list = ArrayList<CeylonPsi.TermPsi>();
        variable CeylonPsi.TermPsi? expression 
            = PsiTreeUtil.getParentOfType(psiElement, javaClass<CeylonPsi.TermPsi>());
        while (exists ex = expression) {
            if (!ex is CeylonPsi.ExpressionPsi, !ex in list) {
                list.add(expression);
            }
            expression 
                = PsiTreeUtil.getParentOfType(expression, javaClass<CeylonPsi.TermPsi>());
        }
        return list;
    }
}
