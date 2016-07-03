package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;

/**
 * Created by gavin on 7/3/16.
 */
public class CeylonStatementUpDownMover extends StatementUpDownMover {
    @Override
    public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile psiFile, @NotNull MoveInfo moveInfo, boolean down) {

        int selectionStart = editor.getSelectionModel().getSelectionStart();
        int selectionEnd = editor.getSelectionModel().getSelectionEnd();
        PsiElement start = psiFile.findElementAt(selectionStart);
        PsiElement end = psiFile.findElementAt(selectionEnd);
        while (start instanceof PsiWhiteSpace) {
            start = start.getNextSibling();
            if (start==null) {
                return false;
            }
        }
        while (end instanceof PsiWhiteSpace) {
            end = end.getPrevSibling();
            if (end==null) {
                return false;
            }
        }

        Condition<PsiElement> condition = new Condition<PsiElement>() {
            @Override
            public boolean value(PsiElement psiElement) {
                return psiElement instanceof CeylonPsi.StatementOrArgumentPsi;
            }
        };

        CeylonPsi.StatementOrArgumentPsi first
                = (CeylonPsi.StatementOrArgumentPsi)
                    PsiTreeUtil.findFirstParent(start, condition);
        CeylonPsi.StatementOrArgumentPsi last
                = (CeylonPsi.StatementOrArgumentPsi)
                    PsiTreeUtil.findFirstParent(end, condition);
        if (first==null || last==null) {
            return false;
        }

        moveInfo.toMove = new LineRange(first, last);

        if (down) {
            PsiElement next = last.getNextSibling();
            while (!(next instanceof CeylonPsi.StatementOrArgumentPsi)) {
                if (next==null) {
                    return false;
                }
                next = next.getNextSibling();
            }
            moveInfo.toMove2 = new LineRange(next);
        }
        else {
            PsiElement prev = first.getPrevSibling();
            while (!(prev instanceof CeylonPsi.StatementOrArgumentPsi)) {
                if (prev==null) {
                    return false;
                }
                prev = prev.getPrevSibling();
            }
            moveInfo.toMove2 = new LineRange(prev);
        }

        return true;
    }
}
