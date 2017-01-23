import com.intellij.codeInsight.editorActions.enter {
    EnterHandlerDelegate {
        Result { cont=Continue }
    }
}
import com.intellij.openapi.actionSystem {
    DataContext
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.editor.actionSystem {
    EditorActionHandler
}
import com.intellij.openapi.util {
    Ref
}
import com.intellij.psi {
    PsiFile
}
import com.intellij.psi.util {
    PsiUtilCore
}

import java.lang {
    JString=String,
    JInteger=Integer
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonTokens
}
import com.intellij.psi.tree {
    IElementType
}

shared class EnterInStringHandler() satisfies EnterHandlerDelegate {

    function isString(IElementType type)
            => type == CeylonTokens.verbatimString
            || type == CeylonTokens.averbatimString
            || type == CeylonTokens.stringLiteral
            || type == CeylonTokens.astringLiteral;

    function isVerbatim(IElementType type)
            => type == CeylonTokens.verbatimString
            || type == CeylonTokens.averbatimString;

    function correctlyTerminated(IElementType type, String text)
            => isVerbatim(type)
            then text.endsWith("\"\"\"") && text.longerThan(5)
            else text.endsWith("\"") && text.longerThan(1);

    shared actual Result preprocessEnter(PsiFile file, Editor editor,
            Ref<JInteger> caretOffset, Ref<JInteger> caretAdvance,
            DataContext dataContext, EditorActionHandler originalHandler) {
        value el = PsiUtilCore.getElementAtOffset(file, caretOffset.get().intValue());
        value document = editor.document;
        value offset = editor.caretModel.offset;
        value startOffset = el.textRange.startOffset;
        value onFirstLine
                = document.getLineNumber(startOffset)
                == document.getLineNumber(offset);
        value type = el.node.elementType;
        if (isString(type),
            onFirstLine,
            !correctlyTerminated(type, el.text)) { //doesn't really work properly (detect unmatched " in whole document instead?)
            value quotesToInsert = isVerbatim(type) then "\"\"\"" else "\"";
            document.insertString(offset, JString(quotesToInsert));
        }
        return cont;
    }

    shared actual Result postProcessEnter(PsiFile file, Editor editor, DataContext dataContext) {
        value el = PsiUtilCore.getElementAtOffset(file, editor.caretModel.offset);
        value document = editor.document;
        value offset = editor.caretModel.offset;
        value startOffset = el.textRange.startOffset;
        value parentLine = document.getLineNumber(startOffset);
        value caretLine = document.getLineNumber(offset);
        value onFirstLine = parentLine + 1 == caretLine;
        value type = el.node.elementType;
        if (onFirstLine, isString(type)) {
            value spacesToInsert = isVerbatim(type) then "   " else " ";
            value adjustment = " ".repeat(
                (startOffset - document.getLineStartOffset(parentLine))
                - (offset - document.getLineStartOffset(caretLine)));
            document.insertString(offset, JString(adjustment + spacesToInsert));
        }
        return cont;
    }
}
