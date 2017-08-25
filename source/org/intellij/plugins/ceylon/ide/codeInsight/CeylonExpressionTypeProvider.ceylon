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
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}

import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}

shared alias Typed
        => CeylonPsi.TermPsi
         | CeylonPsi.LocalModifierPsi
         | CeylonPsi.TypedDeclarationPsi
         | CeylonPsi.InitializerParameterPsi;

shared class CeylonExpressionTypeProvider()
        extends ExpressionTypeProvider<Typed>() {

    function type(Typed psi)
            => switch (psi)
                 case (is CeylonPsi.TermPsi) psi.ceylonNode?.typeModel
            else case (is CeylonPsi.LocalModifierPsi) psi.ceylonNode?.typeModel
            else case (is CeylonPsi.TypedDeclarationPsi) psi.ceylonNode?.type?.typeModel
            else case (is CeylonPsi.InitializerParameterPsi) psi.ceylonNode?.parameterModel?.type;

    getInformationHint(Typed psi)
            => if (exists node = psi.ceylonNode, exists type = type(psi))
            //This highlighting doesn't work :-(
            //"<html>``highlighter.highlight(type.asString(node.unit), termPsi.project)``</html>"
            then StringUtil.escapeXml(type.asString(node.unit))
            else "";

    errorHint => "No expression found";

    shared actual List<Typed>
    getExpressionsAt(PsiElement psiElement) {
        value list = ArrayList<Typed>();
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
            if (exists mod = getParentOfType(psiElement, `CeylonPsi.InitializerParameterPsi`)) {
                list.add(mod);
            }
            else if (exists mod = getParentOfType(psiElement, `CeylonPsi.TypedDeclarationPsi`)) {
                if (!mod.ceylonNode?.type is Tree.VoidModifier) {
                    list.add(mod);
                }
            }
        }
        return list;
    }
}
