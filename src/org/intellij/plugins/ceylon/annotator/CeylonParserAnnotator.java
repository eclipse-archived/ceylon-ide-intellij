package org.intellij.plugins.ceylon.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import org.antlr.runtime.CommonToken;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonPsiVisitor;
import org.jetbrains.annotations.NotNull;

public class CeylonParserAnnotator extends CeylonPsiVisitor implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        element.accept(this);

        if (element instanceof CeylonFile) {
            final CeylonFile file = (CeylonFile) element;
            // Idea doesn't mark errors at EOF (even though it does mark the file as containing errors), so we mark errors before EOF.
            final int lastOffset = file.getTextLength() - 1;
            for (ParseError parseError : file.getMyTree().getErrors()) {
                final CommonToken token = (CommonToken) parseError.getRecognitionException().token;
                final int startOffset = Math.min(token.getStartIndex(), lastOffset);
                final int endOffset = Math.min(token.getStopIndex() + 1, lastOffset);
                holder.createErrorAnnotation(new TextRange(startOffset, endOffset), parseError.getMessage());
            }
        }
    }
}
