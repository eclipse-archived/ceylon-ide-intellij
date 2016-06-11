import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight {
    CodeInsightBundle
}
import com.intellij.codeInsight.navigation {
    GotoTargetHandler
}
import com.intellij.codeInsight.navigation.actions {
    GotoSuperAction
}
import com.intellij.lang {
    LanguageCodeInsightActionHandler
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiElement,
    PsiFile
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.lang {
    ObjectArray
}
import java.util {
    Collections
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    IdeaNavigation,
    CeylonReference
}
import com.redhat.ceylon.ide.common.util {
    types
}

shared class CeylonGotoSuperHandler()
        extends GotoTargetHandler()
        satisfies LanguageCodeInsightActionHandler {

    alias Source
        => CeylonPsi.SpecifierStatementPsi
         | CeylonPsi.AnyClassPsi
         | CeylonPsi.AnyAttributePsi
         | CeylonPsi.AnyMethodPsi
         | CeylonPsi.ObjectDefinitionPsi
         | CeylonPsi.ConstructorPsi;

    featureUsedKey => GotoSuperAction.\iFEATURE_ID;

    shared actual GotoData? getSourceAndTargetElements(Editor editor, PsiFile file)
            => if (exists e = findSource(editor, file))
            then GotoData(e, findTargets(e), Collections.emptyList<AdditionalAction>())
            else null;

    getChooserTitle(PsiElement sourceElement, String name, Integer length)
            => CodeInsightBundle.message("goto.super.method.chooser.title");

    getFindUsagesTitle(PsiElement sourceElement, String name, Integer length)
            => CodeInsightBundle.message("goto.super.method.findUsages.title", name);

    getNotFoundMessage(Project project, Editor editor, PsiFile file)
            => if (is CeylonPsi.AnyClassPsi source = findSource(editor, file))
            then "No super classes found"
            else "No super implementation found";

    Source? findSource(Editor editor, PsiFile file) {
        if (exists element = file.findElementAt(editor.caretModel.offset)) {
            if (exists psi
                = PsiTreeUtil.getParentOfType(element,
                    javaClass<CeylonPsi.SpecifierStatementPsi>()),
                psi.ceylonNode.refinement) {
                return psi;
            }
            return PsiTreeUtil.getParentOfType(element,
                javaClass<CeylonPsi.AnyClassPsi>(),
                javaClass<CeylonPsi.AnyAttributePsi>(),
                javaClass<CeylonPsi.AnyMethodPsi>(),
                javaClass<CeylonPsi.ConstructorPsi>(),
                javaClass<CeylonPsi.ObjectDefinitionPsi>());
        }
        return null;
    }

    ObjectArray<PsiElement> findTargets(Source e) {
        Declaration? target;
        if (is CeylonPsi.AnyMethodPsi e) {
            target = types.getRefinedDeclaration(e.ceylonNode.declarationModel);
        }
        else if (is CeylonPsi.AnyAttributePsi e) {
            target = types.getRefinedDeclaration(e.ceylonNode.declarationModel);
        }
        else if (is CeylonPsi.AnyClassPsi e) {
            target = e.ceylonNode.declarationModel.extendedType?.declaration;
        }
        else if (is CeylonPsi.ObjectDefinitionPsi e) {
            target = e.ceylonNode.anonymousClass.extendedType?.declaration;
        }
        else if (is CeylonPsi.ConstructorPsi e) {
            target = e.ceylonNode.delegatedConstructor?.type?.typeModel?.declaration;
        }
        else {
            target = e.ceylonNode.refined;
        }
        if (exists target,
            exists targetNode
                = CeylonReference.resolveDeclaration(target,e.project)) {
            value array = ObjectArray<PsiElement>(1);
            array.set(0, targetNode);
            return array;
        }
        else {
            return ObjectArray<PsiElement>(0);
        }
    }

    startInWriteAction() => false;

    isValidFor(Editor editor, PsiFile file)
            => file.fileType==CeylonFileType.instance;
}
