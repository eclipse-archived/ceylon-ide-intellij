import com.intellij.lang {
    ExpressionTypeProvider
}
import com.intellij.openapi.util.text {
    StringUtil
}
import com.intellij.psi {
    PsiClass,
    PsiElement,
    PsiElementVisitor,
    PsiType
}
import com.intellij.psi.util {
    PsiTreeUtil,
    PsiUtil
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}
import org.jetbrains.annotations {
    NotNull
}
import java.util {
    ArrayList,
    List
}
import ceylon.interop.java {
    javaClass
}

shared class CeylonExpressionTypeProvider() extends ExpressionTypeProvider<CeylonPsi.TermPsi>() {

    shared actual String? getInformationHint(CeylonPsi.TermPsi termPsi) {
        if (exists node = termPsi.ceylonNode,
            exists type = node.typeModel) {
            return StringUtil.escapeXml(type.asString(node.unit));
        }
        else {
            return null;
        }
    }

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
