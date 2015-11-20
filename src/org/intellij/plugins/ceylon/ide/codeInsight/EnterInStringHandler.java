package org.intellij.plugins.ceylon.ide.codeInsight;

import com.google.common.base.Strings;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
import org.jetbrains.annotations.NotNull;

/**
 * Aligns multiline strings to the right of their opening quote.
 */
public class EnterInStringHandler implements EnterHandlerDelegate {

    @Override
    public Result preprocessEnter(@NotNull PsiFile file, @NotNull Editor editor,
                                  @NotNull Ref<Integer> caretOffset, @NotNull Ref<Integer> caretAdvance,
                                  @NotNull DataContext dataContext, EditorActionHandler originalHandler) {

        PsiElement el = PsiUtilCore.getElementAtOffset(file, caretOffset.get());

        boolean onFirstLine = editor.getDocument().getLineNumber(el.getTextRange().getStartOffset())
                == editor.getDocument().getLineNumber(editor.getCaretModel().getOffset());

        if (el.getNode().getElementType() == CeylonTokens.ASTRING_LITERAL && onFirstLine) {
            editor.getDocument().insertString(editor.getCaretModel().getOffset(), " ");
        }

        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        PsiElement el = PsiUtilCore.getElementAtOffset(file, editor.getCaretModel().getOffset());
        boolean onFirstLine = editor.getDocument().getLineNumber(el.getTextRange().getStartOffset()) + 1
                == editor.getDocument().getLineNumber(editor.getCaretModel().getOffset());

        if ((el.getNode().getElementType() == CeylonTokens.VERBATIM_STRING
                || el.getNode().getElementType() == CeylonTokens.AVERBATIM_STRING) && onFirstLine) {
            int parentLine = editor.getDocument().getLineNumber(el.getTextRange().getStartOffset());
            int parentOffsetInLine = el.getTextRange().getStartOffset() - editor.getDocument().getLineStartOffset(parentLine);
            int line = editor.getDocument().getLineNumber(editor.getCaretModel().getOffset());
            int offsetInLine = editor.getCaretModel().getOffset() - editor.getDocument().getLineStartOffset(line);
            int nbSpacesToInsert = parentOffsetInLine - offsetInLine + 3;
            editor.getDocument().insertString(editor.getCaretModel().getOffset(), Strings.padEnd("", nbSpacesToInsert, ' '));
        }
        return Result.Continue;
    }
}
