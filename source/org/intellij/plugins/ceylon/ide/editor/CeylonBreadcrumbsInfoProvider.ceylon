import com.intellij.psi {
    PsiElement
}
import com.intellij.xml.breadcrumbs {
    BreadcrumbsInfoProvider
}
import com.redhat.ceylon.compiler.typechecker.tree {
    TreeUtil {
        formatPath
    }
}
import com.redhat.ceylon.model.typechecker.model {
    Function
}

import java.lang {
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.psi {
    descriptions,
    CeylonCompositeElement,
    CeylonPsi
}

shared class CeylonBreadcrumbsInfoProvider()
        extends BreadcrumbsInfoProvider() {

    function packageName(CeylonPsi.CompilationUnitPsi e)
            //TODO: get the package name from the CeylonFile
            //      (CeylonFile.packageName currently returns "")
            => e.ceylonNode?.unit?.\ipackage?.nameAsString
            else "";

    acceptElement(PsiElement e)
            => if (is CeylonPsi.SpecifierStatementPsi e)
            then e.ceylonNode.refinement
            else if (is CeylonPsi.CompilationUnitPsi e)
            then !packageName(e).empty
            else e is CeylonPsi.DeclarationPsi
                    | CeylonPsi.TypedArgumentPsi
                    | CeylonPsi.ObjectExpressionPsi
                    | CeylonPsi.FunctionArgumentPsi
                    | CeylonPsi.CompilationUnitPsi
                    | CeylonPsi.ImportPsi
                    | CeylonPsi.ImportModulePsi
                    | CeylonPsi.ModuleDescriptorPsi
                    | CeylonPsi.PackageDescriptorPsi;

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
            else if (is CeylonPsi.PackageDescriptorPsi e,
                exists ids = e.ceylonNode?.importPath?.identifiers)
                then formatPath(ids)
            else if (is CeylonPsi.ModuleDescriptorPsi e,
                exists ids = e.ceylonNode?.importPath?.identifiers)
                then formatPath(ids)
            else if (is CeylonPsi.ImportPsi e,
                exists ids = e.ceylonNode?.importPath?.identifiers)
                then formatPath(ids)
            else if (is CeylonPsi.ImportModulePsi e,
                exists ids = e.ceylonNode?.importPath?.identifiers)
                then formatPath(ids)
            else if (is CeylonPsi.CompilationUnitPsi e)
                then packageName(e) + "::"
            else "";

    getElementTooltip(PsiElement e)
            => if (is CeylonCompositeElement e)
//            then "<html>``highlighter.highlight(descriptions.descriptionForPsi(e) else "", e.project)``</html>"
            then descriptions.descriptionForPsi(e)
            else null;

    languages = ObjectArray(1, CeylonLanguage.instance);
}
