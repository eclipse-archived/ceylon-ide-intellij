import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.refactoring.listeners {
    RefactoringElementListener,
    RefactoringElementListenerProvider
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    DeclarationPsiNameIdOwner
}

shared class CeylonRefactoringListener() satisfies RefactoringElementListenerProvider {

    shared actual RefactoringElementListener? getListener(PsiElement element) {
        value file = element.containingFile;
        if (is DeclarationPsiNameIdOwner element) {
            return object satisfies RefactoringElementListener {
                shared actual void elementMoved(PsiElement newElement) {}
                shared actual void elementRenamed(PsiElement newElement) {
                    if (is CeylonFile file) {
                        object extends WriteCommandAction<Nothing>(file.project) {
                            run(Result<Nothing> result) => file.forceReparse();
                        }
                        .execute();
                    }
                }
            };
        }
        return null;
    }
}
