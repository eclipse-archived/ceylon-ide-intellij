package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.ceylonHighlightingColors_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsiVisitor;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
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
    public void visitElement(PsiElement element) {
        super.visitElement(element);

        if (element.getNode().getElementType() == CeylonTokens.ASTRING_LITERAL) {
            Annotation anno = annotationHolder.createInfoAnnotation(element, null);
            anno.setTextAttributes(ceylonHighlightingColors.getAnnotationString());
        }
    }

    @Override
    public void visitIdentifierPsi(@NotNull CeylonPsi.IdentifierPsi element) {
        super.visitIdentifierPsi(element);

        String name = element.getName();
        PsiElement prevSibling = element.getPrevSibling();

        if (!(element.getParent() instanceof CeylonPsi.ImportPathPsi)
                && StringUtil.isNotEmpty(name) && name.charAt(0) < 'z' && prevSibling != null
                && prevSibling.getNode().getElementType() == CeylonTokens.MEMBER_OP) {
            Annotation anno = annotationHolder.createInfoAnnotation(element, null);
            anno.setTextAttributes(ceylonHighlightingColors.getMember());
        }
        ASTNode firstChildNode = element.getNode().getFirstChildNode();
        if (firstChildNode != null && firstChildNode.getElementType() == CeylonTokens.AIDENTIFIER) {
            Annotation anno = annotationHolder.createInfoAnnotation(element, null);
            anno.setTextAttributes(ceylonHighlightingColors.getAnnotation());
        }
    }

    @Override
    public void visitImportPathPsi(@NotNull CeylonPsi.ImportPathPsi element) {
        super.visitImportPathPsi(element);

        Annotation anno = annotationHolder.createInfoAnnotation(element, null);
        anno.setTextAttributes(ceylonHighlightingColors.getPackages());
    }
}
