import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.refactoring.inline {
    InlineOptionsDialog
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

class InlineDialog(Project project, PsiElement element, IdeaInlineRefactoring refactoring)
        extends InlineOptionsDialog(project, true, element) {

    value occurrences = refactoring.countDeclarationOccurrences();
    myInvokedOnReference = occurrences>1 && refactoring.isReference;

    init();

    shared actual String nameLabelText {
        Integer occurrences = refactoring.countDeclarationOccurrences();
        Declaration declaration = refactoring.editorData.declaration;
        String occ = " occurrence" + (if (occurrences>1) then "s" else "");
        return "Inline ``occurrences````occ`` of declaration '``declaration.name``'";
    }

    hasPreviewButton() => false;

    borderTitle => "Inline";

    inlineAllText => "Inline all references and delete declaration";

    inlineThisText => "Inline just this reference";

    inlineThis => false;

    shared actual void doAction() {
        if (inlineThisOnly) {
            refactoring.editorData.justOne = true;
            refactoring.editorData.delete = false;
        }
        close(okExitCode);
    }

}
