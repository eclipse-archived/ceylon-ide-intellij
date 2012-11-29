package org.intellij.plugins.ceylon.lang;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonClassBody;
import org.intellij.plugins.ceylon.psi.CeylonInterfaceBody;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] BRACE_PAIRS = new BracePair[]{
            new BracePair(CeylonTypes.OP_LPAREN, CeylonTypes.OP_RPAREN, false),
            new BracePair(CeylonTypes.OP_LBRACE, CeylonTypes.OP_RBRACE, true),
            new BracePair(CeylonTypes.OP_LBRACKET, CeylonTypes.OP_RBRACKET, false),
    };

    @Override
    public BracePair[] getPairs() {
        return BRACE_PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        PsiElement element = file.findElementAt(openingBraceOffset);
        if (element == null || element instanceof PsiFile) return openingBraceOffset;
        PsiElement parent = element.getParent();

        if (parent instanceof CeylonClassBody || parent instanceof CeylonInterfaceBody) {
            CeylonClass parentClass = (CeylonClass) parent.getParent();
            PsiElement nameIdentifier = parentClass.getNameIdentifier();
            return (nameIdentifier == null) ? parent.getTextOffset() : nameIdentifier.getTextOffset();
        }
        return openingBraceOffset;
    }
}
