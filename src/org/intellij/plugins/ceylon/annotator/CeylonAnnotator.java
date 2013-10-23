package org.intellij.plugins.ceylon.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.intellij.plugins.ceylon.psi.*;
import org.jetbrains.annotations.NotNull;

public class CeylonAnnotator extends CeylonPsiVisitor implements Annotator {
    private AnnotationHolder annotationHolder;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        this.annotationHolder = holder;

        element.accept(this);

        if (element instanceof PsiFile) {
            new CeylonTypeCheckerVisitor(annotationHolder).accept((PsiFile) element);
        }

        this.annotationHolder = null;
    }

    @Override
    public void visitAnnotationPsi(@NotNull CeylonPsi.AnnotationPsi element) {
        super.visitAnnotationPsi(element);

        Annotation anno = annotationHolder.createInfoAnnotation(element, null);
        anno.setTextAttributes(CodeInsightColors.ANNOTATION_NAME_ATTRIBUTES);
    }


    @Override
    public void visitCompilerAnnotationPsi(@NotNull CeylonPsi.CompilerAnnotationPsi element) {
        super.visitCompilerAnnotationPsi(element);

        Annotation anno = annotationHolder.createInfoAnnotation(element, null);
        anno.setTextAttributes(CodeInsightColors.ANNOTATION_NAME_ATTRIBUTES);
    }
}
