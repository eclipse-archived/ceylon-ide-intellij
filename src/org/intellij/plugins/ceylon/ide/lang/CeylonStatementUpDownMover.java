package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.codeInsight.editorActions.moveUpDown.StatementUpDownMover;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;

/**
 * Created by gavin on 7/3/16.
 */
public class CeylonStatementUpDownMover extends StatementUpDownMover {

    private Condition<PsiElement> condition = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement element) {
            return element instanceof CeylonPsi.StatementOrArgumentPsi
                && !(element instanceof CeylonPsi.VariablePsi)
                && !(element instanceof CeylonPsi.TypeParameterDeclarationPsi)
                && !(element instanceof CeylonPsi.ForIteratorPsi);
        }
    };

    @Override
    public boolean checkAvailable(@NotNull Editor editor, @NotNull PsiFile psiFile, @NotNull MoveInfo moveInfo, boolean down) {

        int selectionStart = editor.getSelectionModel().getSelectionStart();
        int selectionEnd = editor.getSelectionModel().getSelectionEnd();
        PsiElement start = psiFile.findElementAt(selectionStart);
        PsiElement end = psiFile.findElementAt(selectionEnd);
        if (start instanceof PsiWhiteSpace) {
            start = PsiTreeUtil.getNextSiblingOfType(start,
                    CeylonCompositeElement.class);
        }
        if (end instanceof PsiWhiteSpace) {
            end = PsiTreeUtil.getPrevSiblingOfType(end,
                    CeylonCompositeElement.class);
        }

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
            PsiElement next =
                    PsiTreeUtil.getNextSiblingOfType(last,
                        CeylonPsi.StatementOrArgumentPsi.class);
            if (next==null) {
                return false;
            }
            moveInfo.toMove2 = new LineRange(next);
        }
        else {
            PsiElement prev =
                    PsiTreeUtil.getPrevSiblingOfType(first,
                        CeylonPsi.StatementOrArgumentPsi.class);
            if (prev==null) {
                return false;
            }
            moveInfo.toMove2 = new LineRange(prev);
        }

        return true;
    }
}
