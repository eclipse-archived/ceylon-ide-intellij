package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.codeInsight.editorActions.moveLeftRight.MoveElementLeftRightHandler;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;

/**
 * Created by gavin on 7/3/16.
 */
public class CeylonMoveLeftRightHandler extends MoveElementLeftRightHandler {
    @NotNull
    @Override
    public PsiElement[] getMovableSubElements(@NotNull PsiElement element) {
        if (element instanceof CeylonPsi.PositionalArgumentListPsi) {
            return element.getChildren();
        }
        if (element instanceof CeylonPsi.SequencedArgumentPsi) {
            return element.getChildren();
        }
        if (element instanceof CeylonPsi.ParameterListPsi) {
            return element.getChildren();
        }
        if (element instanceof CeylonPsi.ImportMemberOrTypeListPsi) {
            return element.getChildren();
        }
        return PsiElement.EMPTY_ARRAY;
    }
}
