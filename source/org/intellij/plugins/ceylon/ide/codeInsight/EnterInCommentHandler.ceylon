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
    Ref,
    TextRange
}
import com.intellij.psi {
    PsiFile
}
import com.intellij.psi.util {
    PsiUtilCore
}

import java.lang {
    JInteger=Integer,
    JString=String
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonTokens
}


shared class EnterInCommentHandler() satisfies EnterHandlerDelegate {

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
        if (el.node.elementType == CeylonTokens.multiComment, 
            onFirstLine,
            !el.text.endsWith("*/")) {
            value lineStartOffset = document.getLineStartOffset(document.getLineNumber(startOffset));
            value indent
                    = document.getText(TextRange(lineStartOffset, startOffset))
                        .map((ch) => !ch.whitespace then ' ' else ch);
            document.insertString(offset, JString("\n``String(indent)`` */"));
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
        if (onFirstLine,
            type == CeylonTokens.multiComment) {
            value adjustment = " ".repeat(
                (startOffset - document.getLineStartOffset(parentLine))
                - (offset - document.getLineStartOffset(caretLine)));
            document.insertString(offset, JString(adjustment + "   "));
        }
        return cont;
    }
}