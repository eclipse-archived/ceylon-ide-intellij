package org.intellij.plugins.ceylon.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.intellij.plugins.ceylon.annotator.quickfix.AddEmptyParametersFix;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonClassDeclaration;
import org.intellij.plugins.ceylon.psi.CeylonTypeName;
import org.intellij.plugins.ceylon.psi.CeylonVisitor;
import org.jetbrains.annotations.NotNull;

public class CeylonAnnotator extends CeylonVisitor implements Annotator {
    private AnnotationHolder annotationHolder;

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        this.annotationHolder = holder;
        element.accept(this);
        this.annotationHolder = null;
    }

    @Override
    public void visitClassDeclaration(@NotNull final CeylonClassDeclaration o) {
        CeylonTypeName typeName = o.getTypeName();

        if (typeName != null && !((CeylonClass) o).isInterface() && o.getParameters() == null) {
            annotationHolder.createErrorAnnotation(typeName, "Missing parameters list").registerFix(new AddEmptyParametersFix(o));
        }
    }

}
