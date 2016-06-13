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
    Map,
    HashMap
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType
}
import org.intellij.plugins.ceylon.ide.ceylonCode.parser {
    CeylonAntlrToIntellijLexerAdapter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonTokens,
    TokenTypes
}

shared class CeylonHighlighter() extends SyntaxHighlighterBase() {

    Map<IElementType,TextAttributesKey> keys = HashMap<IElementType,TextAttributesKey>();

    TokenSet keywords = TokenSet.create(
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

    keys.put(TokenTypes.\iMULTI_COMMENT.tokenType, ceylonHighlightingColors.comment);
    keys.put(TokenTypes.\iLINE_COMMENT.tokenType, ceylonHighlightingColors.comment);
    keys.put(TokenTypes.\iNATURAL_LITERAL.tokenType, ceylonHighlightingColors.number);
    keys.put(TokenTypes.\iFLOAT_LITERAL.tokenType, ceylonHighlightingColors.number);
    keys.put(TokenTypes.\iCHAR_LITERAL.tokenType, ceylonHighlightingColors.char);
    keys.put(CeylonTokens.\iSTRING_START, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.\iSTRING_MID, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.\iSTRING_END, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.\iSTRING_LITERAL, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.\iSTRING_INTERP, ceylonHighlightingColors.strings);
    keys.put(CeylonTokens.\iVERBATIM_STRING, ceylonHighlightingColors.strings);
    keys.put(TokenTypes.\iLPAREN.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.\iRPAREN.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.\iLBRACE.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.\iRBRACE.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.\iLBRACKET.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.\iRBRACKET.tokenType, ceylonHighlightingColors.brace);
    keys.put(TokenTypes.\iSEMICOLON.tokenType, ceylonHighlightingColors.semi);
    keys.put(TokenTypes.\iUIDENTIFIER.tokenType, ceylonHighlightingColors.type);
    keys.put(TokenTypes.\iLIDENTIFIER.tokenType, ceylonHighlightingColors.identifier);
    keys.put(TokenType.\iBAD_CHARACTER, HighlighterColors.\iBAD_CHARACTER);

    highlightingLexer => CeylonAntlrToIntellijLexerAdapter();

    getTokenHighlights(IElementType tokenType)
            => if (exists key = keys[tokenType]) then pack(key) else empty;
}

shared class CeylonFileHighlightFilter() satisfies Condition<VirtualFile> {
    \ivalue(VirtualFile virtualFile) => virtualFile.fileType is CeylonFileType;
}

shared class CeylonHighlighterFactory() extends SyntaxHighlighterFactory() {
    getSyntaxHighlighter(Project project, VirtualFile virtualFile) => CeylonHighlighter();
}

shared class CeylonColorSettingsPage() extends AbstractCeylonColorSettingsPage() {
    highlighter => CeylonHighlighter();
}
