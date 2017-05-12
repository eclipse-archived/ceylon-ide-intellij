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
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    ModelUtil,
    ClassOrInterface,
    Value,
    Function
}

import java.lang {
    ObjectArray
}
import java.util {
    Collections,
    ArrayList
}

import org.intellij.plugins.ceylon.ide.lang {
    ceylonFileType
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi {
        ...
    }
}
import org.intellij.plugins.ceylon.ide.resolve {
    resolveDeclaration
}

shared class CeylonGotoSuperHandler()
        extends GotoTargetHandler()
        satisfies LanguageCodeInsightActionHandler
                & PresentableCodeInsightActionHandler {

    value noAdditionalActions = Collections.emptyList<AdditionalAction>();

    alias SourceTypes
        => AnyClassPsi
         | AnyInterfacePsi
         | ObjectDefinitionPsi
         | ObjectExpressionPsi
         | ObjectArgumentPsi;
    alias SourceMembers
        => AnyAttributePsi
         | AnyMethodPsi
         | SpecifierStatementPsi;
    alias SourceConstructors
        => ConstructorPsi
         | EnumeratedPsi;

    alias Source
        => SourceTypes | SourceMembers | SourceConstructors;

    featureUsedKey => GotoSuperAction.featureId;

    shared actual default GotoData? getSourceAndTargetElements(Editor editor, PsiFile file)
            => if (exists source = findSource(editor, file))
            then GotoData(source, findTargets(source), noAdditionalActions)
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
            variable value current = element;
            while (!current is CompilationUnitPsi) {
                if (is SpecifierStatementPsi psi = current) {
                    if (psi.ceylonNode.refinement) {
                        return psi;
                    }
                }
                else if (is Source psi = current) {
                    return psi;
                }
                current = current.parent;
            }
        }
        return null;
    }

    ObjectArray<PsiElement> findTargets(Source e) {
        value list = ArrayList<Declaration>();
        switch (e)
        case (is AnyMethodPsi) {
            if (exists fun = e.ceylonNode.declarationModel,
                fun.actual,
                is ClassOrInterface bottom = fun.container) {
                value signature = ModelUtil.getSignature(fun);
                value variadic = ModelUtil.isVariadic(fun);
                for (type in bottom.supertypeDeclarations) {
                    if (type!=bottom,
                        is Function dec
                            = type.getDirectMember(fun.name, signature, variadic, true),
                        dec.shared) {
                        list.add(dec);
                    }
                }
            }
        }
        else case (is AnyAttributePsi) {
            if (exists val = e.ceylonNode.declarationModel,
                val.actual,
                is ClassOrInterface bottom = val.container) {
                for (type in bottom.supertypeDeclarations) {
                    if (type!=bottom,
                        is Value dec
                            = type.getDirectMember(val.name, null, false, true),
                        dec.shared) {
                        list.add(dec);
                    }
                }
            }
        }
        else case (is AnyClassPsi) {
            if (exists cla = e.ceylonNode.declarationModel) {
                if (exists target = cla.extendedType?.declaration) {
                    list.add(target);
                }
                for (type in cla.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else case (is AnyInterfacePsi) {
            if (exists int = e.ceylonNode.declarationModel) {
                if (exists target = int.extendedType?.declaration) {
                    list.add(target);
                }
                for (type in int.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else case (is ObjectDefinitionPsi) {
            if (exists cla = e.ceylonNode.anonymousClass) {
                if (exists target = cla.extendedType?.declaration) {
                    list.add(target);
                }
                for (type in cla.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else case (is ObjectExpressionPsi) {
            if (exists cla = e.ceylonNode.anonymousClass) {
                if (exists target = cla.extendedType?.declaration) {
                    list.add(target);
                }
                for (type in cla.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else case (is ObjectArgumentPsi) {
            if (exists cla = e.ceylonNode.anonymousClass) {
                if (exists target = cla.extendedType?.declaration) {
                    list.add(target);
                }
                for (type in cla.satisfiedTypes) {
                    list.add(type.declaration);
                }
            }
        }
        else case (is ConstructorPsi) {
            Tree.DelegatedConstructor? delegatedConstructor
                    = e.ceylonNode.delegatedConstructor;
            if (exists target
                    = delegatedConstructor?.type?.typeModel?.declaration) {
                list.add(target);
            }
        }
        else case (is EnumeratedPsi) {
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
                for (dec in ModelUtil.getInterveningRefinements(ref,
                                root, bottom, top)) {
                    list.add(dec);
                }
            }
        }
        value result = ArrayList<PsiElement>();
        for (target in list) {
            if (exists targetNode 
                    = resolveDeclaration(target, e.project)) {
                result.add(targetNode);
            }
        }
        return result.toArray(ObjectArray<PsiElement>(0));
    }

    startInWriteAction() => false;

    isValidFor(Editor editor, PsiFile file)
            => file.fileType==ceylonFileType;

    shared actual void update(Editor editor, PsiFile file, Presentation presentation) {
        presentation.enabled = true;
        switch (source = findSource(editor, file))
        case (is SourceTypes) {
            presentation.setText("Supertype Declaration", false);
        }
        else case (is SourceMembers) {
            presentation.setText("Refined Declaration", false);
        }
        else case (is SourceConstructors) {
            presentation.setText("Delegated Constructor", false);
        }
        else {
            presentation.setText("Inherited Declaration", false);
            presentation.enabled = false;
        }
    }

}
