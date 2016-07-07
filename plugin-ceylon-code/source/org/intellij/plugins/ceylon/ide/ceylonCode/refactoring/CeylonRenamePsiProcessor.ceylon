import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.refactoring.rename {
    RenameDialog,
    RenameProcessor,
    RenamePsiElementProcessor
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class CeylonRenamePsiProcessor() extends RenamePsiElementProcessor() {

    canProcessElement(PsiElement element) => true;

    shared actual RenameDialog createRenameDialog(Project proj, PsiElement element,
            PsiElement nameSuggestionContext, Editor editor) {
        value file = nameSuggestionContext.containingFile;
        return object extends RenameDialog(proj, element, nameSuggestionContext, editor) {
            createRenameProcessor(String newName)
                    => object extends RenameProcessor(outer.project, element, newName,
                            outer.searchInComments, outer.searchInNonJavaFiles) {
                        shared actual void performPsiSpoilingRefactoring() {
                            super.performPsiSpoilingRefactoring();
                            if (is CeylonFile file, file != element.containingFile) {
                                file.forceReparse();
                            }
                        }
                    };
        };
    }
}
