package org.intellij.plugins.ceylon.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.annotator.quickfix.AddEmptyParametersFix;
import org.intellij.plugins.ceylon.highlighting.quickfix.AddSharedAnnotationFix;
import org.intellij.plugins.ceylon.psi.*;
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
    public void visitAnnotationName(@NotNull CeylonAnnotationName o) {
        super.visitAnnotationName(o);

        Annotation anno = annotationHolder.createInfoAnnotation(o, null);
        anno.setTextAttributes(CodeInsightColors.ANNOTATION_NAME_ATTRIBUTES);
    }

    @Override
    public void visitCompilerAnnotation(@NotNull CeylonCompilerAnnotation o) {
        super.visitCompilerAnnotation(o);

        @SuppressWarnings("ConstantConditions")
        Annotation anno = annotationHolder.createInfoAnnotation(o.getNode().findChildByType(CeylonTypes.OP_ANNOTATION), null);
        anno.setTextAttributes(CodeInsightColors.ANNOTATION_NAME_ATTRIBUTES);
    }

    @Override
    public void visitClassDeclaration(@NotNull final CeylonClassDeclaration o) {
        CeylonTypeName typeName = o.getTypeName();

        if (typeName != null && !((CeylonClass) o).isInterface() && o.getParameters() == null) {
            annotationHolder.createErrorAnnotation(typeName, "Missing parameters list").registerFix(new AddEmptyParametersFix(o));
        }
    }

    @Override
    public void visitPackageDescriptor(@NotNull CeylonPackageDescriptor o) {
        if (!o.getContainingFile().getName().equals("package.ceylon")) {
            annotationHolder.createErrorAnnotation(o, "Package descriptor is not valid outside of package.ceylon");
        }
    }

    @Override
    public void visitTypeName(@NotNull CeylonTypeName name) {
        super.visitTypeName(name);
        boolean isTypeParameter = false;

        if (PsiTreeUtil.getParentOfType(name, CeylonTypeParameter.class) != null) {
            Annotation anno = annotationHolder.createInfoAnnotation(name, null);
            anno.setTextAttributes(CodeInsightColors.TYPE_PARAMETER_NAME_ATTRIBUTES);
            isTypeParameter = true;
        } else {
            PsiElement element = name;
            CeylonTypedDeclaration decl;

            // TODO refactor in function
            while ((decl = PsiTreeUtil.getParentOfType(element, CeylonTypedDeclaration.class)) != null) {
                CeylonTypeParameters typeParameters = decl.getTypeParameters();
                if (typeParameters != null) {
                    for (CeylonTypeParameter typeParameter : typeParameters.getTypeParameterList()) {
                        CeylonTypeName typeNameDeclaration = typeParameter.getTypeName();
                        if (name.getText().equals(typeNameDeclaration.getText())) {
                            Annotation anno = annotationHolder.createInfoAnnotation(name, null);
                            anno.setTextAttributes(CodeInsightColors.TYPE_PARAMETER_NAME_ATTRIBUTES);
                            isTypeParameter = true;
                            decl = null;
                            break;
                        }
                    }
                }

                element = decl;
            }
        }

        if (!isTypeParameter && !(name.getParent() instanceof CeylonNamedDeclaration)) {
            PsiReference reference = name.getReference();

            if (reference != null) {
                PsiElement target = reference.resolve();
                if (target == null) {
                    Annotation anno = annotationHolder.createErrorAnnotation(name, "Unresolved symbol " + name.getText());
                    anno.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                } else if (target.getParent() instanceof CeylonClass && name.getParent() instanceof CeylonImportName) {
                    CeylonClass myClass = (CeylonClass) target.getParent();
                    String myPackage = myClass.getPackage();
                    PsiFile containingFile = name.getContainingFile();
                    if (containingFile instanceof CeylonFile) {
                        if (!StringUtil.equals(((CeylonFile) containingFile).getPackageName(), myPackage) && !myClass.isShared()) {
                            Annotation anno = annotationHolder.createErrorAnnotation(name, "Imported declaration is not shared");
                            anno.setHighlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                            anno.registerFix(new AddSharedAnnotationFix(myClass));
                        }
                    }
                }
            }
        }
    }
}
