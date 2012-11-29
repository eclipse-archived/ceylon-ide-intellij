package org.intellij.plugins.ceylon.highlighting;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.codeInsight.daemon.impl.quickfix.QuickFixAction;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.ceylon.highlighting.quickfix.AddSharedAnnotationFix;
import org.intellij.plugins.ceylon.psi.*;
import org.jetbrains.annotations.NotNull;

public class CeylonHighlightVisitor extends CeylonVisitor implements HighlightVisitor {
    private HighlightInfoHolder myHolder;

    @Override
    public boolean suitableForFile(@NotNull PsiFile file) {
        return file instanceof CeylonFile;
    }

    @Override
    public void visit(@NotNull PsiElement element) {
        element.accept(this);
    }

    @Override
    public boolean analyze(@NotNull PsiFile file, boolean updateWholeFile, @NotNull HighlightInfoHolder holder, @NotNull Runnable action) {
        myHolder = holder;

        try {
            action.run();
        } finally {
            myHolder = null;
        }
        return true;
    }

    @Override
    public int order() {
        return 0;
    }

    @NotNull
    @Override
    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException", "CloneDoesntCallSuperClone"})
    public HighlightVisitor clone() {
        return new CeylonHighlightVisitor();
    }

    @Override
    public void visitAnnotationName(@NotNull CeylonAnnotationName o) {
        super.visitAnnotationName(o);
        HighlightInfo info = HighlightInfo.createHighlightInfo(HighlightInfoType.ANNOTATION_NAME, o, null);
        myHolder.add(info);
    }

    @Override
    public void visitTypeName(@NotNull CeylonTypeName name) {
        super.visitTypeName(name);

        if (PsiTreeUtil.getParentOfType(name, CeylonTypeParameter.class) != null) {
            HighlightInfo info = HighlightInfo.createHighlightInfo(HighlightInfoType.TYPE_PARAMETER_NAME, name, null);
            myHolder.add(info);
        } else {
            // TODO ugly, perhaps create an interface named TypedDeclaration with getTypeParameters()
            CeylonVoidOrInferredMethodDeclaration decl = PsiTreeUtil.getParentOfType(name, CeylonVoidOrInferredMethodDeclaration.class);
            CeylonTypeParameters typeParameters = (decl == null) ? null : decl.getTypeParameters();

            if (typeParameters != null) {

                for (CeylonTypeParameter typeParameter : typeParameters.getTypeParameterList()) {
                    CeylonTypeName typeNameDeclaration = typeParameter.getTypeName();
                    if (name.getText().equals(typeNameDeclaration.getText())) {
                        HighlightInfo info = HighlightInfo.createHighlightInfo(HighlightInfoType.TYPE_PARAMETER_NAME, name, null);
                        myHolder.add(info);
                    }
                }
            }
        }

        if (!(name.getParent() instanceof CeylonNamedDeclaration)) {
            PsiReference reference = name.getReference();

            if (reference != null) {
                PsiElement target = reference.resolve();
                if (target == null) {
                    HighlightInfo error = HighlightInfo.createHighlightInfo(HighlightInfoType.WRONG_REF, name, "Unresolved symbol " + name.getText());
                    myHolder.add(error);
                } else if (target.getParent() instanceof CeylonClass && name.getParent() instanceof CeylonImportName) {
                    CeylonClass myClass = (CeylonClass) target.getParent();
                    String myPackage = myClass.getPackage();
                    PsiFile containingFile = name.getContainingFile();
                    if (containingFile instanceof CeylonFile) {
                        if (!StringUtil.equals(((CeylonFile) containingFile).getPackageName(), myPackage) && !myClass.isShared()) {
                            HighlightInfo highlightInfo = HighlightInfo.createHighlightInfo(HighlightInfoType.ERROR, name, "Imported declaration is not shared");
                            QuickFixAction.registerQuickFixAction(highlightInfo, new AddSharedAnnotationFix(myClass));
                            myHolder.add(highlightInfo);
                        }
                    }
                }
            }
        }
    }

}
