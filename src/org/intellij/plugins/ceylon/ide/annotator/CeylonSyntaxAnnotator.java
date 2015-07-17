package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.ceylonHighlightingColors_;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsiVisitor;
import org.intellij.plugins.ceylon.ide.psi.CeylonTypes;
import org.jetbrains.annotations.NotNull;

public class CeylonSyntaxAnnotator extends CeylonPsiVisitor implements Annotator {
    private AnnotationHolder annotationHolder;
    private ceylonHighlightingColors_ ceylonHighlightingColors = ceylonHighlightingColors_.get_();

    public CeylonSyntaxAnnotator() {
        super(false);
    }

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
        anno.setTextAttributes(ceylonHighlightingColors.getAnnotation());
    }


    @Override
    public void visitCompilerAnnotationPsi(@NotNull CeylonPsi.CompilerAnnotationPsi element) {
        super.visitCompilerAnnotationPsi(element);

        Annotation anno = annotationHolder.createInfoAnnotation(element, null);
        anno.setTextAttributes(ceylonHighlightingColors.getAnnotation());
    }

    @Override
    public void visitMetaLiteralPsi(@NotNull CeylonPsi.MetaLiteralPsi element) {
        super.visitMetaLiteralPsi(element);

        Annotation anno = annotationHolder.createInfoAnnotation(element, null);
        anno.setTextAttributes(ceylonHighlightingColors.getInterp());
    }

    @Override
    public void visitStringTemplatePsi(@NotNull CeylonPsi.StringTemplatePsi element) {
        super.visitStringTemplatePsi(element);

        for (PsiElement child : element.getChildren()) {
            if (child instanceof CeylonPsi.ExpressionPsi) {
                Annotation anno = annotationHolder.createInfoAnnotation(child, null);
                anno.setTextAttributes(ceylonHighlightingColors.getInterp());
            }
        }
    }

    @Override
    public void visitIdentifierPsi(@NotNull CeylonPsi.IdentifierPsi element) {
        super.visitIdentifierPsi(element);

        String name = element.getName();
        PsiElement prevSibling = element.getPrevSibling();

        if (name != null && name.charAt(0) < 'z' && prevSibling != null && prevSibling.getNode().getElementType() == CeylonTypes.MEMBER_OP) {
            Annotation anno = annotationHolder.createInfoAnnotation(element, null);
            anno.setTextAttributes(ceylonHighlightingColors.getMember());
        }
    }
}
