import com.intellij.lang.refactoring {
    RefactoringSupportProvider
}
import com.intellij.psi {
    PsiElement
}

import org.intellij.plugins.ceylon.ide.ceylonCode.refactoring {
    CeylonChangeSignatureHandler,
    ExtractFunctionHandler,
    ExtractValueHandler
}

shared class CeylonRefactoringSupportProvider() extends RefactoringSupportProvider() {

    isInplaceRenameAvailable(PsiElement element, PsiElement context) => false; //TODO: huh?

    changeSignatureHandler => CeylonChangeSignatureHandler();

    introduceVariableHandler => ExtractValueHandler();

    extractMethodHandler => ExtractFunctionHandler();
}
