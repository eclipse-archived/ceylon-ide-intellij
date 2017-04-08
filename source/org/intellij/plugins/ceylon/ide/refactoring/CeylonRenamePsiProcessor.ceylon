import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.search {
    SearchScope
}
import com.intellij.refactoring.rename {
    RenameDialog,
    RenameProcessor,
    RenamePsiElementProcessor
}
import com.redhat.ceylon.ide.common.hierarchy {
    hierarchyManager
}

import java.lang {
    Types {
        nativeString
    },
    JString=String
}
import java.util {
    Map
}

import org.intellij.plugins.ceylon.ide.hierarchy {
    collectPhasedUnits
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.resolve {
    resolveDeclaration
}

"Applies special behavior when renaming Ceylon declarations."
shared class CeylonRenamePsiProcessor() extends RenamePsiElementProcessor() {

    canProcessElement(PsiElement element)
            => element.containingFile is CeylonFile;

    "Forces a reparse of the current file after the rename to update the global model."
    shared actual RenameDialog createRenameDialog(Project proj, PsiElement element,
            PsiElement nameSuggestionContext, Editor editor) {
        value file = nameSuggestionContext.containingFile;
        return object extends RenameDialog(proj, element, nameSuggestionContext, editor) {
            createRenameProcessor(String newName)
                    => object extends RenameProcessor(outer.project, element, newName,
                            outer.searchInComments, outer.searchInNonJavaFiles) {
                        shared actual void performPsiSpoilingRefactoring() {
                            super.performPsiSpoilingRefactoring();
                            if (is CeylonFile file,
                                !(element.containingFile?.equals(file) else true)) {
                                file.forceReparse();
                            }
                        }
                    };
        };
    }

    shared actual void prepareRenaming(PsiElement element, String newName,
        Map<PsiElement,JString> allRenames, SearchScope scope) {

        if (is CeylonPsi.MethodDeclarationPsi element,
            exists model = element.ceylonNode.declarationModel) {

            value pus = collectPhasedUnits(element.project, true);
            for (subtype in hierarchyManager.findSubtypes { model; *pus }) {
                if (exists psiElement = resolveDeclaration(subtype, element.project)) {
                    allRenames.put(psiElement, nativeString(newName));
                }
            }
        }
    }
}
