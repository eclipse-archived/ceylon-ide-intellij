import com.intellij.codeHighlighting {
    Pass
}
import com.intellij.codeInsight.daemon {
    LineMarkerInfo,
    GutterIconNavigationHandler
}
import com.intellij.codeInsight.navigation {
    GotoImplementationHandler,
    GotoTargetHandler
}
import com.intellij.icons {
    AllIcons
}
import com.intellij.openapi.application {
    ApplicationInfo
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
    TextRange
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
    Declaration,
    ClassOrInterface
}

import java.awt.event {
    MouseEvent
}
import java.lang {
    Types {
        nativeString
    },
    JString=String
}
import java.util {
    List,
    Collection
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.codeInsight.navigation {
    CeylonGotoSuperHandler
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared class CeylonLineMarkerProvider() extends MyLineMarkerProvider() {

    class MarkerInfo(
        PsiElement element, TextRange range, Icon icon, Integer updatePass,
        String tooltip, GutterIconRenderer.Alignment alignment,
        GutterIconNavigationHandler<PsiElement> handler)
            extends LineMarkerInfo<PsiElement>(
                element, range, icon, updatePass,
                object satisfies Function<PsiElement,JString> {
                    fun(PsiElement? param) => nativeString(tooltip);
                },
                handler, alignment) {}

    Declaration? getModel(CeylonPsi.DeclarationPsi|CeylonPsi.SpecifierStatementPsi decl)
            => if (is CeylonPsi.DeclarationPsi decl)
            then decl.ceylonNode?.declarationModel
            else decl.ceylonNode?.declaration;

    function findParentDeclaration(PsiElement el)
            => if (is CeylonPsi.DeclarationPsi decl = el.parent) then decl
            else if (is CeylonPsi.SpecifierStatementPsi el) then el
            else null;

    function findDeclaration(PsiElement element) {
        /*if (is CeylonFile file = element.containingFile) {
            if (!file.upToDatePhasedUnit exists) {
                platformUtils.log(Status._DEBUG,
                    "CeylonLineMarkerProvider returned no marker info because the file `` file `` is not typechecked and up-to-date");
                throw platformUtils.newOperationCanceledException();
            }
        }*/

        if (is CeylonPsi.IdentifierPsi|CeylonPsi.SpecifierStatementPsi element,
            if (is CeylonPsi.SpecifierStatementPsi element)
                then element.ceylonNode.refinement else true,
            exists decl = findParentDeclaration(element),
            exists model = getModel(decl)) {
            return model;
        }

        return null;
    }

    function editor(PsiElement element) {
        value file = element.containingFile;
        if (exists virtualFile = file.virtualFile) {
            value fileEditor
                    = FileEditorManager.getInstance(file.project)
                        .getSelectedEditor(virtualFile);
            if (is TextEditor fileEditor) {
                return fileEditor.editor;
            }
        }
        return null;
    }

    shared actual LineMarkerInfo<PsiElement>? getLineMarkerInfo(PsiElement element) {
        if (exists model = findDeclaration(element),
            exists offset = findParentDeclaration(element)?.textOffset,
            model.actual,
            //only needed for the icon!
            exists refined = types.getRefinedDeclaration(model),
            is ClassOrInterface parent = refined.container) {

            return MarkerInfo {
                element = element;
                range = element.textRange;
                icon = refined.formal then icons.refinement else icons.extendedType;
                updatePass = Pass.updateAll;
                tooltip = "Refines ``parent.getName(model.unit)``.``refined.name``";
                alignment = GutterIconRenderer.Alignment.left;
                object handler
                        extends CeylonGotoSuperHandler()
                        satisfies GutterIconNavigationHandler<PsiElement> {

                    shared actual void invoke(Project project, Editor editor, PsiFile file) {
                        //move the caret to the start of the line (near the icon) temporarily
                        value document = editor.document;
                        value before = editor.caretModel.offset;
                        value lineStartOffset
                                = document.getLineStartOffset(document.getLineNumber(offset));
                        editor.caretModel.moveToOffset(lineStartOffset);
                        try {
                            super.invoke(project, editor, file);
                        }
                        finally {
                            //if we didn't navigate to a different position in the same file, move it back
                            if (editor.caretModel.offset==lineStartOffset) {
                                editor.caretModel.moveToOffset(before);
                            }
                        }
                    }

                    //use fully-qualified type here b/c of compiler bug!
                    shared actual GotoTargetHandler.GotoData? getSourceAndTargetElements(Editor editor, PsiFile file) {
                        //move the caret to the source declaration temporarily
                        value before = editor.caretModel.offset;
                        editor.caretModel.moveToOffset(offset);
                        try {
                            return super.getSourceAndTargetElements(editor, file);
                        }
                        finally {
                            editor.caretModel.moveToOffset(before);
                        }
                    }

                    shared actual void navigate(MouseEvent? mouseEvent, PsiElement element) {
                        if (exists ed = editor(element)) {
                            invoke(element.project, ed, element.containingFile);
                        }
                    }
                }
            };
        }

        return null;
    }

    shared actual void collectLineMarkers(List<PsiElement> elements,
            Collection<LineMarkerInfo<out PsiElement>> result) {
        for (element in elements) {
            if (exists model = findDeclaration(element),
                exists offset = findParentDeclaration(element)?.textOffset,
                model.formal || model.default) {

                result.add(MarkerInfo {
                    alignment = GutterIconRenderer.Alignment.right;
                    tooltip = "Refinements of ``model.name``";
                    updatePass
                        = ApplicationInfo.instance.build.baselineVersion >= 163
                        then Pass.lineMarkers
                        else 6; //Pass.UPDATE_OVERRIDEN_MARKERS was renamed, then deprecated in platform 163.x
                    icon
                        = model.formal
                        then AllIcons.Gutter.implementedMethod
                        else AllIcons.Gutter.overridenMethod;
                    range = element.textRange;
                    element = element;
                    object handler
                            extends GotoImplementationHandler()
                            satisfies GutterIconNavigationHandler<PsiElement> {

                        shared actual void invoke(Project project, Editor editor, PsiFile file) {
                            //move the caret to the start of the line (near the icon) temporarily
                            value document = editor.document;
                            value before = editor.caretModel.offset;
                            value lineStartOffset
                                    = document.getLineStartOffset(document.getLineNumber(offset));
                            editor.caretModel.moveToOffset(lineStartOffset);
                            try {
                                super.invoke(project, editor, file);
                            }
                            finally {
                                //if we didn't navigate to a different position in the same file, move it back
                                if (editor.caretModel.offset==lineStartOffset) {
                                    editor.caretModel.moveToOffset(before);
                                }
                            }
                        }

                        shared actual GotoData? getSourceAndTargetElements(Editor editor, PsiFile file) {
                            //move the caret to the source declaration temporarily
                            value before = editor.caretModel.offset;
                            editor.caretModel.moveToOffset(offset);
                            try {
                                return super.getSourceAndTargetElements(editor, file);
                            }
                            finally {
                                editor.caretModel.moveToOffset(before);
                            }
                        }

                        shared actual void navigate(MouseEvent? mouseEvent, PsiElement id) {
                            if (exists ed = editor(element)) {
                                invoke(element.project, ed, element.containingFile);
                            }
                        }
                    }
                });
            }
        }
    }

}
