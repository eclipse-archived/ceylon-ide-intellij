package org.intellij.plugins.ceylon.ide.ceylonCode.refactoring;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.inline.InlineOptionsDialog;
import com.redhat.ceylon.model.typechecker.model.Declaration;

public class InlineDialog extends InlineOptionsDialog {

    private Object refactoring;

    protected InlineDialog(Project project, PsiElement element, Object refactoring) {
        super(project, true, element);
        this.refactoring = refactoring;

        IdeaInlineRefactoring ref = (IdeaInlineRefactoring) refactoring;
        long occurrences = ref.countDeclarationOccurrences();
        myInvokedOnReference = occurrences > 1 && ((IdeaInlineRefactoring) refactoring).getIsReference();
        init();
    }

    @Override
    protected String getNameLabelText() {
        IdeaInlineRefactoring ref = (IdeaInlineRefactoring) refactoring;
        long occurrences = ref.countDeclarationOccurrences();
        Declaration declaration = ref.getEditorData().getDeclaration();
        String occ = " occurrence" + (occurrences > 1 ? "s" : "");
        return "Inline " + occurrences + occ + " of declaration '" + declaration.getName() + "'";
    }

    @Override
    protected String getBorderTitle() {
        return "Inline";
    }

    @Override
    protected String getInlineAllText() {
        return "Inline all references and delete declaration";
    }

    @Override
    protected String getInlineThisText() {
        return "Inline just this reference";
    }

    @Override
    protected boolean isInlineThis() {
        return false;
    }

    @Override
    protected void doAction() {
        if (isInlineThisOnly()) {
            IdeaInlineRefactoring ref = (IdeaInlineRefactoring) refactoring;
            ((IdeaInlineRefactoring.IdeaInlineData)ref.getEditorData()).setJustOne(true);
            ((IdeaInlineRefactoring.IdeaInlineData)ref.getEditorData()).setDelete(false);
        }
        close(OK_EXIT_CODE);
    }
}
