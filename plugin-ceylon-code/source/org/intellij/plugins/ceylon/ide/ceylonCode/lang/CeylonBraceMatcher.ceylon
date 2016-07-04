import ceylon.interop.java {
    createJavaObjectArray
}

import com.intellij.lang {
    BracePair,
    PairedBraceMatcher
}
import com.intellij.psi {
    PsiFile,
    PsiNameIdentifierOwner
}
import com.intellij.psi.tree {
    IElementType
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    TokenTypes
}

shared class CeylonBraceMatcher() satisfies PairedBraceMatcher {

    pairs = createJavaObjectArray<BracePair> {
        BracePair(TokenTypes.lparen.tokenType, TokenTypes.rparen.tokenType, false),
        BracePair(TokenTypes.lbrace.tokenType, TokenTypes.rbrace.tokenType, true),
        BracePair(TokenTypes.lbracket.tokenType, TokenTypes.rbracket.tokenType, false)
    };

    isPairedBracesAllowedBeforeType(IElementType lbraceType, IElementType contextType) => true;

    shared actual Integer getCodeConstructStart(PsiFile file, Integer openingBraceOffset) {
        if (exists element = file.findElementAt(openingBraceOffset),
            is CeylonPsi.BlockPsi parent = element.parent,
            is PsiNameIdentifierOwner parentParent = parent.parent) {
            return if (exists nameIdentifier = parentParent.nameIdentifier)
                then nameIdentifier.textOffset
                else parent.textOffset;
        }
        return openingBraceOffset;
    }
}
