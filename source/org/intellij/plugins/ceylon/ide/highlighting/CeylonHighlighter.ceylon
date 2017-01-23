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

import org.intellij.plugins.ceylon.ide.lang {
    ceylonFileType
}
import org.intellij.plugins.ceylon.ide.parser {
    CeylonAntlrToIntellijLexerAdapter
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonTokens,
    TokenTypes
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
        keys[kw] = ceylonHighlightingColors.keyword;
    }

    keys[TokenTypes.uidentifier.tokenType] = ceylonHighlightingColors.type;
    keys[TokenTypes.lidentifier.tokenType] = ceylonHighlightingColors.identifier;

    keys[TokenTypes.multiComment.tokenType] = ceylonHighlightingColors.blockComment;
    keys[TokenTypes.lineComment.tokenType] = ceylonHighlightingColors.lineComment;
    keys[TokenTypes.naturalLiteral.tokenType] = ceylonHighlightingColors.number;
    keys[TokenTypes.floatLiteral.tokenType] = ceylonHighlightingColors.number;
    keys[TokenTypes.charLiteral.tokenType] = ceylonHighlightingColors.char;

    keys[CeylonTokens.stringStart] = ceylonHighlightingColors.strings;
    keys[CeylonTokens.stringMid] = ceylonHighlightingColors.strings;
    keys[CeylonTokens.stringEnd] = ceylonHighlightingColors.strings;
    keys[CeylonTokens.stringLiteral] = ceylonHighlightingColors.strings;
    keys[CeylonTokens.verbatimString] = ceylonHighlightingColors.strings;

    keys[TokenTypes.lparen.tokenType] = ceylonHighlightingColors.paren;
    keys[TokenTypes.rparen.tokenType] = ceylonHighlightingColors.paren;
    keys[TokenTypes.lbrace.tokenType] = ceylonHighlightingColors.brace;
    keys[TokenTypes.rbrace.tokenType] = ceylonHighlightingColors.brace;
    keys[TokenTypes.lbracket.tokenType] = ceylonHighlightingColors.bracket;
    keys[TokenTypes.rbracket.tokenType] = ceylonHighlightingColors.bracket;

    keys[TokenTypes.semicolon.tokenType] = ceylonHighlightingColors.semi;
    keys[TokenTypes.comma.tokenType] = ceylonHighlightingColors.comma;

    keys[TokenTypes.specify.tokenType] = ceylonHighlightingColors.assignment;
    keys[TokenTypes.compute.tokenType] = ceylonHighlightingColors.assignment;

    keys[TokenTypes.memberOp.tokenType] = ceylonHighlightingColors.dot;
    keys[TokenTypes.safeMemberOp.tokenType] = ceylonHighlightingColors.dot;
    keys[TokenTypes.spreadOp.tokenType] = ceylonHighlightingColors.dot;

    keys[TokenType.badCharacter] = HighlighterColors.badCharacter;

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
