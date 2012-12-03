package org.intellij.plugins.ceylon.annotator.quickfix;

import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.psi.*;
import org.jetbrains.annotations.NotNull;

public class AddAnnotationFix extends AbstractIntentionAction {

    private final CeylonDeclaration declaration;
    private final String declarationName;
    private final String annotationName;

    public AddAnnotationFix(CeylonDeclaration declaration, String declarationName, String annotationName) {
        this.declaration = declaration;
        this.declarationName = declarationName;
        this.annotationName = annotationName;
    }

    @NotNull
    @Override
    public String getText() {
        return String.format("Make %s %s", declarationName, annotationName);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        CeylonAnnotations annotations = declaration.getAnnotations();

        // TODO CeylonElementFactory
        CeylonFile ceylonFile = (CeylonFile) PsiFileFactory.getInstance(declaration.getProject()).createFileFromText("dummy.ceylon", CeylonLanguage.INSTANCE, annotationName + " class Foo(){}");
        CeylonAnnotation annotation = (ceylonFile.findChildByClass(CeylonDeclaration.class)).getAnnotations().getAnnotationList().get(0);
        annotations.add(annotation);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
