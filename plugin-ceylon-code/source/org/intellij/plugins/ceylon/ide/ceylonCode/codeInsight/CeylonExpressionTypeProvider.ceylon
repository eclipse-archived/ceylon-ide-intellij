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
    PsiTreeUtil {
        getParentOfType
    }
}

import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}

shared class CeylonExpressionTypeProvider()
        extends ExpressionTypeProvider<CeylonPsi.TermPsi>() {

    getInformationHint(CeylonPsi.TermPsi termPsi)
            => if (exists node = termPsi.ceylonNode,
                   exists type = node.typeModel)
            //This highlighting doesn't work :-(
            //"<html>``highlighter.highlight(type.asString(node.unit), termPsi.project)``</html>"
            then StringUtil.escapeXml(type.asString(node.unit))
            else "";

    errorHint => "No expression found";

    value termType = javaClass<CeylonPsi.TermPsi>();

    shared actual List<CeylonPsi.TermPsi> getExpressionsAt(PsiElement psiElement) {
        value list = ArrayList<CeylonPsi.TermPsi>();
        variable value expression = getParentOfType(psiElement, termType);
        while (exists ex = expression) {
            if (!ex is CeylonPsi.ExpressionPsi, !ex in list) {
                list.add(expression);
            }
            expression = getParentOfType(expression, termType);
        }
        return list;
    }
}
