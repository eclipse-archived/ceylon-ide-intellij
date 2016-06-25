import ceylon.interop.java {
    javaString
}

import com.intellij.codeHighlighting {
    Pass
}
import com.intellij.codeInsight {
    TargetElementUtil {
        referencedElementAccepted,
        lookupItemAccepted
    }
}
import com.intellij.codeInsight.daemon {
    LineMarkerInfo,
    GutterIconNavigationHandler
}
import com.intellij.codeInsight.navigation {
    GotoImplementationHandler,
    ImplementationSearcher
}
import com.intellij.icons {
    AllIcons
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.editor.markup {
    GutterIconRenderer
}
import com.intellij.openapi.fileEditor {
    FileEditorManager,
    TextEditor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange,
    Computable
}
import com.intellij.pom {
    Navigatable
}
import com.intellij.psi {
    PsiElement,
    PsiFile
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
    JString=String,
    ObjectArray
}
import java.util {
    List,
    Collection,
    ArrayList,
    Collections
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    CeylonReference
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}
import com.intellij.openapi.application {
    ApplicationManager
}

shared class CeylonLineMarkerProvider() extends MyLineMarkerProvider() {

    class MarkerInfo(
        PsiElement element, TextRange range, Icon icon, Integer updatePass,
        String tooltip, GutterIconRenderer.Alignment alignment,
        GutterIconNavigationHandler<PsiElement> handler)
            extends LineMarkerInfo<PsiElement>(
                element, range, icon, updatePass,
                object satisfies Function<PsiElement,JString> {
                    fun(PsiElement? param) => javaString(tooltip);
                },
                handler, alignment) {}

    Declaration? getModel(CeylonPsi.DeclarationPsi|CeylonPsi.SpecifierStatementPsi decl)
            => if (is CeylonPsi.DeclarationPsi decl)
            then decl.ceylonNode.declarationModel
            else decl.ceylonNode.declaration;

    function findParentDeclaration(PsiElement el) {
        if (is CeylonPsi.DeclarationPsi decl = el.parent) {
            return decl;
        } else if (is CeylonPsi.SpecifierStatementPsi el) {
            return el;
        } else {
            return null;
        }
    }

    function findDeclaration(PsiElement element) {
        /*if (is CeylonFile file = element.containingFile) {
            if (!file.upToDatePhasedUnit exists) {
                platformUtils.log(Status._DEBUG,
                    "CeylonLineMarkerProvider returned no marker info because the file `` file `` is not typechecked and up-to-date");
                throw platformUtils.newOperationCanceledException();
            }
        }*/

        if (is CeylonPsi.IdentifierPsi|CeylonPsi.SpecifierStatementPsi element,
            exists decl = findParentDeclaration(element),
            exists model = getModel(decl)) {
            return model;
        }

        return null;
    }

    shared actual LineMarkerInfo<PsiElement>? getLineMarkerInfo(PsiElement element) {
        if (exists model = findDeclaration(element),
            model.actual,
            exists refined = types.getRefinedDeclaration(model)) {
            assert (is Declaration parent = refined.container);

            return MarkerInfo {
                element = element;
                range = element.textRange;
                icon = refined.formal then icons.refinement else icons.extendedType;
                updatePass = Pass.updateAll;
                tooltip = "Refines ``parent.getName(model.unit)``.``refined.name``";
                alignment = GutterIconRenderer.Alignment.left;
                object handler satisfies GutterIconNavigationHandler<PsiElement> {
                    shared actual void navigate(MouseEvent? mouseEvent, PsiElement element) {
                        assert(is CeylonFile ceylonFile = element.containingFile);
                        if (is Navigatable psi
                                = CeylonReference.resolveDeclaration(refined, element.project)) {
                            psi.navigate(true);
                        }
                    }
                }
            };
        }
        
        return null;
    }

    function editor(PsiElement element) {
        value file = element.containingFile;
        if (exists virtualFile = file.virtualFile) {
            value fileEditor
                    = FileEditorManager
                        .getInstance(file.project)
                        .getSelectedEditor(virtualFile);
            if (is TextEditor fileEditor) {
                return fileEditor.editor;
            }
        }
        return null;
    }

    class Searcher(Editor editor) extends ImplementationSearcher() {
        value accept = TargetElementUtil.instance.acceptImplementationForReference;

        shared actual ObjectArray<PsiElement> filterElements
                (PsiElement element, ObjectArray<PsiElement> targetElements, Integer offset) {
            value result = ArrayList<PsiElement>();
            for (targetElement in targetElements) {
                value reference = TargetElementUtil.findReference(editor, offset);
                if (accept(reference, targetElement)) {
                    result.add(targetElement);
                }
            }
            return result.toArray(ObjectArray<PsiElement>(0));
        }

        "Became package-private in intellij-community@7b037bf so we had to copy it here."
        shared ObjectArray<PsiElement> ourSearchImplementations(Editor editor, PsiElement? element, Integer offset) {
            TargetElementUtil targetElementUtil = TargetElementUtil.instance;

            value onRef = ApplicationManager.application.runReadAction(object satisfies Computable<Boolean> {
                shared actual Boolean compute() {
                    return !targetElementUtil.findTargetElement(editor,
                        flags.and(referencedElementAccepted.or(lookupItemAccepted)),
                        offset
                    ) exists;
                }
            });

            value onSelf = ApplicationManager.application.runReadAction(object satisfies Computable<Boolean> {
                shared actual Boolean compute() {
                    return !element exists||targetElementUtil.includeSelfInGotoImplementation(element);
                }
            });
            return searchImplementations(element, editor, offset, onRef && onSelf, onRef);
        }
    }

    shared actual void collectLineMarkers(List<PsiElement> elements,
            Collection<LineMarkerInfo<out PsiElement>> result) {
        for (element in elements) {
            if (exists model = findDeclaration(element),
                exists decl = findParentDeclaration(element),
                model.formal || model.default) {

                result.add(MarkerInfo {
                    alignment = GutterIconRenderer.Alignment.right;
                    tooltip = "Refinements of ``model.name``";
                    updatePass = Pass.updateOverridenMarkers;
                    icon = AllIcons.Gutter.implementedMethod;
                    range = element.textRange;
                    element = element;
                    object handler
                            extends GotoImplementationHandler()
                            satisfies GutterIconNavigationHandler<PsiElement> {

                        shared actual void invoke(Project project, Editor? editor, PsiFile file) {
                            if (exists editor) {
                                value document = editor.document;
                                value before = editor.caretModel.offset;
                                value lineStartOffset = document.getLineStartOffset(document.getLineNumber(decl.textOffset));
                                editor.caretModel.moveToOffset(lineStartOffset);
                                try {
                                    super.invoke(project, editor, file);
                                }
                                finally {
                                    editor.caretModel.moveToOffset(before);
                                }
                            }
                            else {
                                super.invoke(project, editor, file);
                            }
                        }

                        getSourceAndTargetElements(Editor editor, PsiFile file)
                                => GotoData(decl,
                                    Searcher(editor).ourSearchImplementations(editor, decl of PsiElement, decl.textOffset),
                                    Collections.emptyList<AdditionalAction>());

                        navigate(MouseEvent? mouseEvent, PsiElement id)
                                => invoke(element.project, editor(element), element.containingFile);
                    }
                });
            }
        }
    }

}
