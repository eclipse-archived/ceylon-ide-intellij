import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.editorActions.moveUpDown {
    LineRange,
    StatementUpDownMover
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.util {
    Condition
}
import com.intellij.psi {
    PsiElement,
    PsiFile,
    PsiWhiteSpace
}
import com.intellij.psi.util {
    PsiTreeUtil
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonCompositeElement,
    CeylonPsi
}

shared class CeylonStatementUpDownMover() extends StatementUpDownMover() {

    object condition satisfies Condition<PsiElement> {
        \ivalue(PsiElement element)
                => element is CeylonPsi.StatementOrArgumentPsi
                && !element is CeylonPsi.VariablePsi
                             | CeylonPsi.TypeParameterDeclarationPsi
                             | CeylonPsi.ForIteratorPsi;
    }

    shared actual Boolean checkAvailable(Editor editor, PsiFile psiFile, MoveInfo moveInfo, Boolean down) {

        Integer selectionStart = editor.selectionModel.selectionStart;
        Integer selectionEnd = editor.selectionModel.selectionEnd;

        value startElement = psiFile.findElementAt(selectionStart);
        value endElement = psiFile.findElementAt(selectionEnd);
        value start
                = if (is PsiWhiteSpace startElement)
                then PsiTreeUtil.getNextSiblingOfType(startElement,
                        javaClass<CeylonCompositeElement>())
                else startElement;
        value end
                = if (is PsiWhiteSpace endElement)
                then PsiTreeUtil.getPrevSiblingOfType(endElement,
                        javaClass<CeylonCompositeElement>())
                else endElement;

        value first = PsiTreeUtil.findFirstParent(start, condition);
        value last = PsiTreeUtil.findFirstParent(end, condition);
        if (!is CeylonPsi.StatementOrArgumentPsi first) {
            return false;
        }
        if (!is CeylonPsi.StatementOrArgumentPsi last) {
            return false;
        }

        moveInfo.toMove = LineRange(first, last);
        if (down) {
            if (exists next
                    = PsiTreeUtil.getNextSiblingOfType(last,
                        javaClass<CeylonPsi.StatementOrArgumentPsi>())) {
                moveInfo.toMove2 = LineRange(next);
                return true;
            }
        } else {
            if (exists prev
                = PsiTreeUtil.getPrevSiblingOfType(first,
                    javaClass<CeylonPsi.StatementOrArgumentPsi>())) {
                moveInfo.toMove2 = LineRange(prev);
                return true;
            }
        }
        return false;
    }
}
