package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting.ceylonHighlightingColors_;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsiVisitor;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTypes;
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
                PsiElement prev = child;
                do {
                    prev = prev.getPrevSibling();
                } while (prev != null &&
                        prev.getNode().getElementType()
                                == CeylonTokens.WS);
                PsiElement next = child;
                do {
                    next = next.getNextSibling();
                } while (next != null &&
                        next.getNode().getElementType()
                                == CeylonTokens.WS);

                int startOffset
                        = prev != null
                        && prev.getText().endsWith("``") ?
                        prev.getTextRange().getEndOffset() - 2 :
                        child.getTextRange().getStartOffset();
                int endOffset
                        = next != null
                        && next.getText().startsWith("``") ?
                        next.getTextRange().getStartOffset() + 2 :
                        child.getTextRange().getEndOffset();
                TextRange range = new TextRange(startOffset, endOffset);
                Annotation anno = annotationHolder.createInfoAnnotation(range, null);
                anno.setTextAttributes(ceylonHighlightingColors.getInterp());
            }
        }
    }

    @Override
    public void visitElement(PsiElement element) {
        super.visitElement(element);

        IElementType elementType = element.getNode().getElementType();
        if (elementType == CeylonTokens.ASTRING_LITERAL
         || elementType == CeylonTokens.AVERBATIM_STRING) {
            Annotation anno = annotationHolder.createInfoAnnotation(element, null);
            anno.setTextAttributes(ceylonHighlightingColors.getAnnotationString());
        }

        //workaround for bug in CeylonPsiVisitor
        if (element instanceof CeylonPsi.StringTemplatePsi) {
            visitStringTemplatePsi((CeylonPsi.StringTemplatePsi) element);
        }
    }

    @Override
    public void visitIdentifierPsi(@NotNull CeylonPsi.IdentifierPsi element) {
        super.visitIdentifierPsi(element);

        PsiElement prevSibling = element.getPrevSibling();

        ASTNode firstChildNode = element.getNode().getFirstChildNode();

        if (!(element.getParent() instanceof CeylonPsi.ImportPathPsi)
                && firstChildNode != null
                && firstChildNode.getElementType() == CeylonTokens.LIDENTIFIER
                && prevSibling != null
                && prevSibling.getNode().getElementType() == CeylonTokens.MEMBER_OP) {
            Annotation anno = annotationHolder.createInfoAnnotation(element, null);
            anno.setTextAttributes(ceylonHighlightingColors.getMember());
        }
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
