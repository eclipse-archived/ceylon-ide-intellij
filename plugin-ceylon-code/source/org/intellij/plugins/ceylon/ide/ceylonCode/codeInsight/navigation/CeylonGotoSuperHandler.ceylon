import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.generation.actions {
    PresentableCodeInsightActionHandler
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
import com.intellij.openapi.actionSystem {
    Presentation
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
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    ModelUtil,
    ClassOrInterface
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
    CeylonReference
}

shared class CeylonGotoSuperHandler()
        extends GotoTargetHandler()
        satisfies LanguageCodeInsightActionHandler
                & PresentableCodeInsightActionHandler {

    alias SourceTypes
        => CeylonPsi.AnyClassPsi
         | CeylonPsi.AnyInterfacePsi
         | CeylonPsi.ObjectDefinitionPsi
         | CeylonPsi.ObjectExpressionPsi;
    alias SourceMembers
        => CeylonPsi.AnyAttributePsi
         | CeylonPsi.AnyMethodPsi
         | CeylonPsi.SpecifierStatementPsi;
    alias SourceConstructors
        => CeylonPsi.ConstructorPsi
        | CeylonPsi.EnumeratedPsi;

    alias Source
        => SourceTypes | SourceMembers | SourceConstructors;

    featureUsedKey => GotoSuperAction.featureId;

    getSourceAndTargetElements(Editor editor, PsiFile file)
            => if (exists source = findSource(editor, file))
            then GotoData(source, findTargets(source), Collections.emptyList<AdditionalAction>())
            else null;

    getChooserTitle(PsiElement sourceElement, String name, Integer length)
            => if (is SourceTypes sourceElement)
            then "Choose supertype declaration"
            else "Choose refined declaration";

//    getFindUsagesTitle(PsiElement sourceElement, String name, Integer length)
//            => CodeInsightBundle.message("goto.super.method.findUsages.title", name);

    getNotFoundMessage(Project project, Editor editor, PsiFile file)
            => let (source = findSource(editor, file))
            if (is SourceTypes source)
            then "No supertype declaration found"
            else if (is SourceConstructors source)
            then "No delegated constructor found"
            else "No refined declaration found";

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
                javaClass<CeylonPsi.ObjectDefinitionPsi>(),
                javaClass<CeylonPsi.ObjectExpressionPsi>());
        }
        return null;
    }

    ObjectArray<PsiElement> findTargets(Source e) {
        value list = ArrayList<Declaration>();
        if (is CeylonPsi.AnyMethodPsi e) {
            if (exists fun = e.ceylonNode.declarationModel,
                exists root = fun.refinedDeclaration,
                is ClassOrInterface bottom = fun.container,
                is ClassOrInterface top = root.container) {
                for (dec in ModelUtil.getInterveningRefinements(fun.name,
                                ModelUtil.getSignature(fun), root, bottom, top)) {
                    list.add(dec);
                }
            }
        }
        else if (is CeylonPsi.AnyAttributePsi e) {
            if (exists val = e.ceylonNode.declarationModel,
                exists root = val.refinedDeclaration,
                is ClassOrInterface bottom = val.container,
                is ClassOrInterface top = root.container) {
                for (dec in ModelUtil.getInterveningRefinements(val.name,
                                null, root, bottom, top)) {
                    list.add(dec);
                }
            }
        }
        else if (is CeylonPsi.AnyClassPsi e) {
            if (exists cla = e.ceylonNode.declarationModel) {
                if (exists target = cla.extendedType ?. declaration) {
                    list.add(target);
                }
                for (type in cla.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else if (is CeylonPsi.AnyInterfacePsi e) {
            if (exists int = e.ceylonNode.declarationModel) {
                if (exists target = int.extendedType ?. declaration) {
                    list.add(target);
                }
                for (type in int.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else if (is CeylonPsi.ObjectDefinitionPsi e) {
            if (exists cla = e.ceylonNode.anonymousClass) {
                if (exists target = cla.extendedType ?. declaration) {
                    list.add(target);
                }
                for (type in cla.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else if (is CeylonPsi.ObjectExpressionPsi e) {
            if (exists cla = e.ceylonNode.anonymousClass) {
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
            value node = e.ceylonNode;
            if (node.refinement,
                exists ref = node.declaration,
                exists root = ref.refinedDeclaration,
                is ClassOrInterface bottom = ref.container,
                is ClassOrInterface top = root.container) {
                for (dec in ModelUtil.getInterveningRefinements(ref.name,
                                ModelUtil.getSignature(ref), root, bottom, top)) {
                    list.add(dec);
                }
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

    shared actual void update(Editor editor, PsiFile file, Presentation presentation) {
        presentation.enabled = true;
        value source = findSource(editor, file);
        if (is SourceTypes source) {
            presentation.setText("Supertype Declaration", false);
        }
        else if (is SourceMembers source) {
            presentation.setText("Refined Declaration", false);
        }
        else if (is SourceConstructors source) {
            presentation.setText("Delegated Constructor", false);
        }
        else {
            presentation.setText("Inherited Declaration", false);
            presentation.enabled = false;
        }
    }

}
