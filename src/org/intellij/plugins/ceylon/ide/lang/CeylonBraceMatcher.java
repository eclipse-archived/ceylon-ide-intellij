package org.intellij.plugins.ceylon.ide.lang;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.psi.TokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] BRACE_PAIRS = new BracePair[]{
            new BracePair(TokenTypes.LPAREN.getTokenType(), TokenTypes.RPAREN.getTokenType(), false),
            new BracePair(TokenTypes.LBRACE.getTokenType(), TokenTypes.RBRACE.getTokenType(), true),
            new BracePair(TokenTypes.LBRACKET.getTokenType(), TokenTypes.RBRACKET.getTokenType(), false),
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

        if (parent instanceof CeylonPsi.BlockPsi && parent.getParent() instanceof PsiNameIdentifierOwner) {
            PsiNameIdentifierOwner namedParent = (PsiNameIdentifierOwner) parent.getParent();
            PsiElement nameIdentifier = namedParent.getNameIdentifier();
            return (nameIdentifier == null) ? parent.getTextOffset() : nameIdentifier.getTextOffset();
        }
        return openingBraceOffset;
    }
}
