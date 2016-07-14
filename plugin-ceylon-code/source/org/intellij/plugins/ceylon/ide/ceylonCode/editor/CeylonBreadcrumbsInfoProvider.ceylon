import ceylon.interop.java {
    createJavaObjectArray
}

import com.intellij.lang.java {
    JavaBreadcrumbsInfoProvider
}
import com.intellij.psi {
    PsiElement
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    descriptions,
    CeylonCompositeElement,
    CeylonPsi
}
import com.redhat.ceylon.model.typechecker.model {
    Function
}
shared class CeylonBreadcrumbsInfoProvider()
        extends JavaBreadcrumbsInfoProvider() {

    acceptElement(PsiElement e)
            => if (is CeylonPsi.SpecifierStatementPsi e)
            then e.ceylonNode.refinement
            else e is CeylonPsi.DeclarationPsi
                    | CeylonPsi.TypedArgumentPsi
                    | CeylonPsi.ObjectExpressionPsi
                    | CeylonPsi.FunctionArgumentPsi;

    getElementInfo(PsiElement e)
            => if (is CeylonPsi.SpecifierStatementPsi e,
                   exists dec = e.ceylonNode?.declaration)
                then dec.name + (dec is Function then "()" else "")
            else if (is CeylonPsi.FunctionArgumentPsi e)
                then "anonymous function"
            else if (is CeylonPsi.ObjectExpressionPsi e)
                then "object expression"
            else if (is CeylonPsi.AnyMethodPsi e,
                    exists name = e.name)
                then name + "()"
            else if (is CeylonPsi.DeclarationPsi e)
                then (e.name else "")
            else if (is CeylonPsi.TypedArgumentPsi e)
                then (e.name else "")
            else "";

    getElementTooltip(PsiElement e)
            => if (is CeylonCompositeElement e)
//            then "<html>``highlighter.highlight(descriptions.descriptionForPsi(e) else "", e.project)``</html>"
            then descriptions.descriptionForPsi(e)
            else null;

    languages = createJavaObjectArray { CeylonLanguage.instance };

}