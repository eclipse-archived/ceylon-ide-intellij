import ceylon.interop.java {
    javaString,
    javaClass
}

import com.intellij.codeHighlighting {
    Pass
}
import com.intellij.codeInsight.daemon {
    LineMarkerInfo,
    GutterIconNavigationHandler
}
import com.intellij.openapi.editor.markup {
    GutterIconRenderer
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.util {
    Function
}
import com.redhat.ceylon.ide.common.util {
    types
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.awt.event {
    MouseEvent
}
import java.lang {
    JString=String
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerProvider
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    CeylonReference
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}
import com.intellij.openapi.\imodule {
    ModuleUtil,
    Module
}

shared class CeylonLineMarkerProvider() extends MyLineMarkerProvider() {
    
    Declaration? getModel(CeylonPsi.DeclarationPsi|CeylonPsi.SpecifierStatementPsi decl) {
        if (is CeylonPsi.DeclarationPsi decl) {
            return decl.ceylonNode.declarationModel;
        }
        return decl.ceylonNode.declaration;
    }
    
    shared actual LineMarkerInfo<PsiElement>?
    getLineMarkerInfo(PsiElement element) {
        
        if (is CeylonFile file = element.containingFile) {
            file.ensureTypechecked();
        }
        if (is CeylonPsi.IdentifierPsi element,
            is CeylonPsi.DeclarationPsi|CeylonPsi.SpecifierStatementPsi decl = element.parent,
            exists model = getModel(decl),
            model.actual,
            exists refined = types.getRefinedDeclaration(model)) {
            
            value unit = decl.ceylonNode.unit;
            assert(is Declaration parent = refined.container);
            value text = "Refines " + parent.getName(unit) + "."
                    + refined.getName(unit);
            value tooltip = object satisfies Function<PsiElement, JString> {
                shared actual JString fun(PsiElement? param) => javaString(text);
            };
            
            return LineMarkerInfo(element, element.textRange, ideaIcons.refinement,
                Pass.\iUPDATE_ALL, tooltip, NavigationHandler(refined),
                GutterIconRenderer.Alignment.\iLEFT);
        }
        
        return null;
    }
    
    class NavigationHandler(Declaration target)
             satisfies GutterIconNavigationHandler<PsiElement> {
        
        shared actual void navigate(MouseEvent? mouseEvent, PsiElement t) {
            assert(is CeylonFile ceylonFile = t.containingFile);
            
            Module mod = ModuleUtil.findModuleForFile(ceylonFile.virtualFile, 
                ceylonFile.project);
            value provider = mod.getComponent(javaClass<ITypeCheckerProvider>());
            value tc = provider.typeChecker;

            if (exists psi = CeylonReference
                    .resolveDeclaration(target, tc, t.project)) {
                psi.navigate(true);
            }
        }
    }
}
