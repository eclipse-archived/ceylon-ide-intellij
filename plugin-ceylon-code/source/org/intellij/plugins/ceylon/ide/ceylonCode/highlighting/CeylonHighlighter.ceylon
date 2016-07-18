import com.intellij.openapi.editor {
    HighlighterColors
}
import com.intellij.openapi.editor.colors {
    TextAttributesKey
}
import com.intellij.openapi.fileTypes {
    SyntaxHighlighterBase,
    SyntaxHighlighterFactory
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    Condition
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    TokenType
}
import com.intellij.psi.tree {
    IElementType,
    TokenSet
}

import java.util {
    HashMap
}

import org.intellij.plugins.ceylon.ide.ceylonCode.parser {
    CeylonAntlrToIntellijLexerAdapter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonTokens,
    TokenTypes
}
import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    ceylonFileType
}

shared object ceylonHighlighter extends SyntaxHighlighterBase() {

    value keys = HashMap<IElementType,TextAttributesKey>();

    shared TokenSet keywords = TokenSet.create(
        TokenTypes.\iASSEMBLY.tokenType,
        TokenTypes.\iMODULE.tokenType,
        TokenTypes.\iPACKAGE.tokenType,
        TokenTypes.\iIMPORT.tokenType,
        TokenTypes.\iALIAS.tokenType,
        TokenTypes.\iCLASS_DEFINITION.tokenType,
        TokenTypes.\iINTERFACE_DEFINITION.tokenType,
        TokenTypes.\iOBJECT_DEFINITION.tokenType,
        TokenTypes.\iTYPE_CONSTRAINT.tokenType,
        TokenTypes.\iVALUE_MODIFIER.tokenType,
        TokenTypes.\iASSIGN.tokenType,
        TokenTypes.\iVOID_MODIFIER.tokenType,
        TokenTypes.\iFUNCTION_MODIFIER.tokenType,
        TokenTypes.\iCASE_TYPES.tokenType,
        TokenTypes.\iEXTENDS.tokenType,
        TokenTypes.\iSATISFIES.tokenType,
        TokenTypes.\iABSTRACTED_TYPE.tokenType,
        TokenTypes.\iIN_OP.tokenType,
        TokenTypes.\iOUT.tokenType,
        TokenTypes.\iRETURN.tokenType,
        TokenTypes.\iBREAK.tokenType,
        TokenTypes.\iCONTINUE.tokenType,
        TokenTypes.\iTHROW.tokenType,
        TokenTypes.\iASSERT.tokenType,
        TokenTypes.\iDYNAMIC.tokenType,
        TokenTypes.\iIF_CLAUSE.tokenType,
        TokenTypes.\iELSE_CLAUSE.tokenType,
        TokenTypes.\iSWITCH_CLAUSE.tokenType,
        TokenTypes.\iCASE_CLAUSE.tokenType,
        TokenTypes.\iFOR_CLAUSE.tokenType,
        TokenTypes.\iWHILE_CLAUSE.tokenType,
        TokenTypes.\iTRY_CLAUSE.tokenType,
        TokenTypes.\iCATCH_CLAUSE.tokenType,
        TokenTypes.\iFINALLY_CLAUSE.tokenType,
        TokenTypes.\iTHEN_CLAUSE.tokenType,
        TokenTypes.\iTHIS.tokenType,
        TokenTypes.\iOUTER.tokenType,
        TokenTypes.\iSUPER.tokenType,
        TokenTypes.\iIS_OP.tokenType,
        TokenTypes.\iEXISTS.tokenType,
        TokenTypes.\iNONEMPTY.tokenType,
        TokenTypes.\iLET.tokenType,
        TokenTypes.\iNEW.tokenType);

    for (kw in keywords.types) {
        keys.put(kw, ceylonHighlightingColors.keyword);
    }

    keys.put(TokenTypes.multiComment.tokenType, ceylonHighlightingColors.comment);
    keys.put(TokenTypes.lineComment.tokenType, ceylonHighlightingColors.comment);
    keys.put(TokenTypes.naturalLiteral.tokenType, ceylonHighlightingColors.number);
    keys.put(TokenTypes.floatLiteral.tokenType, ceylonHighlightingColors.number);
    keys.put(TokenTypes.charLiteral.tokenType, ceylonHighlightingColors.char);
    keys.put(CeylonTokens.stringStart, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.stringMid, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.stringEnd, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.stringLiteral, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.stringInterp, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.verbatimString, ceylonHighlightingColors.strings);
    keys.put(TokenTypes.lparen.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.rparen.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.lbrace.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.rbrace.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.lbracket.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.rbracket.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.semicolon.tokenType, ceylonHighlightingColors.semi);
    keys.put(TokenTypes.uidentifier.tokenType, ceylonHighlightingColors.type);
    keys.put(TokenTypes.lidentifier.tokenType, ceylonHighlightingColors.identifier);
    keys.put(TokenType.badCharacter, HighlighterColors.badCharacter);

    highlightingLexer => CeylonAntlrToIntellijLexerAdapter();

    getTokenHighlights(IElementType tokenType)
            => if (exists key = keys[tokenType]) then pack(key) else empty;
}

shared class CeylonFileHighlightFilter() satisfies Condition<VirtualFile> {
    \ivalue(VirtualFile virtualFile) => virtualFile.fileType == ceylonFileType;
}

shared class CeylonHighlighterFactory() extends SyntaxHighlighterFactory() {
    getSyntaxHighlighter(Project project, VirtualFile virtualFile) => ceylonHighlighter;
}

shared class CeylonColorSettingsPage() extends AbstractCeylonColorSettingsPage() {
    highlighter => ceylonHighlighter;
}
