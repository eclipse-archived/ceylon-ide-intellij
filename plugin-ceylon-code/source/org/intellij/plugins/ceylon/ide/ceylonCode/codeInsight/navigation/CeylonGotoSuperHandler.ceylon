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
    Declaration,
    Class,
    Value,
    Function,
    TypeDeclaration,
    TypedDeclaration,
    Interface
}

import java.lang {
    ObjectArray
}
import java.util {
    Collections,
    ArrayList
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
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}

shared class CeylonGotoSuperHandler()
        extends GotoTargetHandler()
        satisfies LanguageCodeInsightActionHandler {

    alias Source
        => CeylonPsi.SpecifierStatementPsi
         | CeylonPsi.AnyClassPsi
         | CeylonPsi.AnyInterfacePsi
         | CeylonPsi.AnyAttributePsi
         | CeylonPsi.AnyMethodPsi
         | CeylonPsi.ObjectDefinitionPsi
         | CeylonPsi.ConstructorPsi
         | CeylonPsi.EnumeratedPsi;

    featureUsedKey => GotoSuperAction.\iFEATURE_ID;

    shared actual GotoData? getSourceAndTargetElements(Editor editor, PsiFile file)
            => if (exists e = findSource(editor, file))
            then GotoData(e, findTargets(e), Collections.emptyList<AdditionalAction>())
            else null;

    getChooserTitle(PsiElement sourceElement, String name, Integer length)
            => "Choose supertype";

    getFindUsagesTitle(PsiElement sourceElement, String name, Integer length)
            => CodeInsightBundle.message("goto.super.method.findUsages.title", name);

    getNotFoundMessage(Project project, Editor editor, PsiFile file)
            => let (source = findSource(editor, file))
            if (is CeylonPsi.AnyClassPsi|CeylonPsi.AnyInterfacePsi|CeylonPsi.ObjectDefinitionPsi source)
            then "No supertypes found"
            else if (is CeylonPsi.ConstructorPsi|CeylonPsi.EnumeratedPsi source)
            then "No delegated constructor found"
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
                javaClass<CeylonPsi.AnyInterfacePsi>(),
                javaClass<CeylonPsi.AnyAttributePsi>(),
                javaClass<CeylonPsi.AnyMethodPsi>(),
                javaClass<CeylonPsi.ConstructorPsi>(),
                javaClass<CeylonPsi.ObjectDefinitionPsi>());
        }
        return null;
    }

    ObjectArray<PsiElement> findTargets(Source e) {
        value list = ArrayList<Declaration>();
        if (is CeylonPsi.AnyMethodPsi e) {
            Function? meth = e.ceylonNode.declarationModel;
            if (exists meth, exists target = types.getRefinedDeclaration(meth)) {
                list.add(target);
            }
        }
        else if (is CeylonPsi.AnyAttributePsi e) {
            TypedDeclaration? att = e.ceylonNode.declarationModel;
            if (exists att, exists target = types.getRefinedDeclaration(att)) {
                list.add(target);
            }
        }
        else if (is CeylonPsi.AnyClassPsi e) {
            Class? cla = e.ceylonNode.declarationModel;
            if (exists cla) {
                if (exists target = cla.extendedType ?. declaration) {
                    list.add(target);
                }
                for (type in cla.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else if (is CeylonPsi.AnyInterfacePsi e) {
            Interface? cla = e.ceylonNode.declarationModel;
            if (exists cla) {
                if (exists target = cla.extendedType ?. declaration) {
                    list.add(target);
                }
                for (type in cla.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else if (is CeylonPsi.ObjectDefinitionPsi e) {
            Class? cla = e.ceylonNode.anonymousClass;
            if (exists cla) {
                if (exists target = cla.extendedType ?. declaration) {
                    list.add(target);
                }
                for (type in cla.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else if (is CeylonPsi.ConstructorPsi e) {
            Tree.DelegatedConstructor? delegatedConstructor
                    = e.ceylonNode.delegatedConstructor;
            if (exists target
                    = delegatedConstructor?.type?.typeModel?.declaration) {
                list.add(target);
            }
        }
        else if (is CeylonPsi.EnumeratedPsi e) {
            Tree.DelegatedConstructor? delegatedConstructor
                = e.ceylonNode.delegatedConstructor;
            if (exists target
                    = delegatedConstructor?.type?.typeModel?.declaration) {
                list.add(target);
            }
        }
        else {
            if (exists target = e.ceylonNode.refined) {
                list.add(target);
            }
        }
        value result = ArrayList<PsiElement>();
        for (target in list) {
            if (exists targetNode
                    = CeylonReference.resolveDeclaration(target, e.project)) {
                result.add(targetNode);
            }
        }
        return result.toArray(ObjectArray<PsiElement>(0));
    }

    startInWriteAction() => false;

    isValidFor(Editor editor, PsiFile file)
            => file.fileType==CeylonFileType.instance;
}
