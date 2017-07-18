import com.intellij.psi {
    PsiElement
}
import com.intellij.ui.breadcrumbs {
    BreadcrumbsProvider
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
        satisfies BreadcrumbsProvider {

    function packageName(CeylonPsi.CompilationUnitPsi e)
            //TODO: get the package name from the CeylonFile
            //      (CeylonFile.packageName currently returns "")
            => e.ceylonNode?.unit?.\ipackage?.nameAsString
            else "";

    acceptElement(PsiElement e)
            => switch (e)
            case (is CeylonPsi.SpecifierStatementPsi)
                e.ceylonNode.refinement
            else case (is CeylonPsi.CompilationUnitPsi)
                !packageName(e).empty
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
            => switch (e)
            case (is CeylonPsi.SpecifierStatementPsi)
               if (exists dec = e.ceylonNode?.declaration)
               then dec.name + (dec is Function then "()" else "")
               else ""
            else case (is CeylonPsi.FunctionArgumentPsi)
                "anonymous function"
            else case (is CeylonPsi.ObjectExpressionPsi)
                "object expression"
            else case (is CeylonPsi.AnyMethodPsi)
                if (exists name = e.name)
                then name + "()"
                else ""
            else case (is CeylonPsi.DeclarationPsi)
                (e.name else "")
            else case (is CeylonPsi.TypedArgumentPsi)
                (e.name else "")
            else case (is CeylonPsi.PackageDescriptorPsi)
                if (exists ids = e.ceylonNode?.importPath?.identifiers)
                then formatPath(ids)
                else ""
            else case (is CeylonPsi.ModuleDescriptorPsi)
                if (exists ids = e.ceylonNode?.importPath?.identifiers)
                then formatPath(ids)
                else ""
            else case (is CeylonPsi.ImportPsi)
                if (exists ids = e.ceylonNode?.importPath?.identifiers)
                then formatPath(ids)
                else ""
            else case (is CeylonPsi.ImportModulePsi)
                if (exists ids = e.ceylonNode?.importPath?.identifiers)
                then formatPath(ids)
                else ""
            else case (is CeylonPsi.CompilationUnitPsi)
                packageName(e) + "::"
            else "";

    getElementTooltip(PsiElement e)
            => if (is CeylonCompositeElement e)
//            then "<html>``highlighter.highlight(descriptions.descriptionForPsi(e) else "", e.project)``</html>"
            then descriptions.descriptionForPsi(e)
            else null;

    languages = ObjectArray(1, CeylonLanguage.instance);
}
