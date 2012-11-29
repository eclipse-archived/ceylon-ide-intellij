package org.intellij.plugins.ceylon.highlighting.quickfix;

import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.psi.*;
import org.jetbrains.annotations.NotNull;

public class AddSharedAnnotationFix extends AbstractIntentionAction {

    private final CeylonClass myClass;

    public AddSharedAnnotationFix(CeylonClass myClass) {
        this.myClass = myClass;
    }

    @NotNull
    @Override
    public String getText() {
        return "Make " + myClass.getName() + " shared";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        CeylonAnnotations annotations = ((CeylonDeclaration) myClass.getParent()).getAnnotations();

        // TODO CeylonElementFactory
        CeylonFile ceylonFile = (CeylonFile) PsiFileFactory.getInstance(myClass.getProject()).createFileFromText("dummy.ceylon", CeylonLanguage.INSTANCE, "shared class Foo(){}");
        CeylonAnnotation annotation = (ceylonFile.findChildByClass(CeylonDeclaration.class)).getAnnotations().getAnnotationList().get(0);
        annotations.add(annotation);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
