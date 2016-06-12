import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.psi {
    PsiElement,
    PsiFile,
    PsiNamedElement
}
import com.intellij.refactoring.rename.inplace {
    VariableInplaceRenameHandler,
    VariableInplaceRenamer
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    DeclarationPsiNameIdOwner,
    ParameterPsiIdOwner
}
import java.util {
    List
}
import com.intellij.openapi.util {
    TextRange,
    Pair
}

shared class CeylonVariableRenameHandler(Boolean forceInplace = false)
        extends VariableInplaceRenameHandler() {

    shared actual VariableInplaceRenamer createRenamer(PsiElement elementToRename, Editor editor) {
        assert (is PsiNamedElement elementToRename);

        return object extends VariableInplaceRenamer(elementToRename, editor) {
            
            shared actual void finish(Boolean success) {
                super.finish(success);

                if (success, is CeylonFile file = elementToRename.containingFile) {
                    object extends WriteCommandAction<Nothing>(myProject, file) {
                        shared actual void run(Result<Nothing> result) {
                            file.forceReparse();
                        }
                    }.execute();
                }
            }

            collectAdditionalElementsToRename(List<Pair<PsiElement,TextRange>>? stringUsages)
                    => noop();

            shared actual PsiElement? checkLocalScope() {
                if (forceInplace) {
                    return elementToRename.containingFile;
                }
                return super.checkLocalScope();
            }
        };
    }

    shared actual Boolean isAvailable(PsiElement? element, Editor editor, PsiFile file) {
        if (forceInplace) {
            return true;
        }
        if (exists element,
            exists context = file.findElementAt(editor.caretModel.offset),
            context.containingFile != element.containingFile) {
            return false;
        }

        if (is CeylonFile file) {
            file.ensureTypechecked();
        }
        return element is DeclarationPsiNameIdOwner|ParameterPsiIdOwner;
    }
}
