package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsiVisitor;
import org.intellij.plugins.ceylon.ide.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;

public class CeylonSyntaxAnnotator extends CeylonPsiVisitor implements Annotator {
    private AnnotationHolder annotationHolder;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        this.annotationHolder = holder;
        element.accept(this);
        this.annotationHolder = null;
    }

    @Override
    public void visitAnnotationPsi(@NotNull CeylonPsi.AnnotationPsi element) {
        super.visitAnnotationPsi(element);

        final ASTNode ident = element.getNode().findChildByType(CeylonTypes.IDENTIFIER);
        final TextRange range = ident == null ? element.getTextRange() : ident.getTextRange();
        Annotation anno = annotationHolder.createInfoAnnotation(range, null);
        anno.setTextAttributes(CodeInsightColors.ANNOTATION_NAME_ATTRIBUTES);
    }


    @Override
    public void visitCompilerAnnotationPsi(@NotNull CeylonPsi.CompilerAnnotationPsi element) {
        super.visitCompilerAnnotationPsi(element);

        Annotation anno = annotationHolder.createInfoAnnotation(element, null);
        anno.setTextAttributes(CodeInsightColors.ANNOTATION_NAME_ATTRIBUTES);
    }
}
