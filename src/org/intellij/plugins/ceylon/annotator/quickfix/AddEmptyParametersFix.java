package org.intellij.plugins.ceylon.annotator.quickfix;

import com.intellij.codeInsight.intention.AbstractIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.psi.CeylonClassDeclaration;
import org.intellij.plugins.ceylon.psi.CeylonTypeName;
import org.jetbrains.annotations.NotNull;

/**
 * Adds missing "()" to a class declaration.
 */
public class AddEmptyParametersFix extends AbstractIntentionAction {
    private final CeylonClassDeclaration aClass;

    public AddEmptyParametersFix(CeylonClassDeclaration aClass) {
        this.aClass = aClass;
    }

    @NotNull
    @Override
    public String getText() {
        return "Add empty parameters";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        CeylonTypeName typeName = aClass.getTypeName();

        if (typeName != null) {
            editor.getDocument().insertString(typeName.getTextOffset() + typeName.getTextLength(), "()");
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
