import ceylon.interop.java {
    javaString
}

import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.codeStyle {
    SuggestedNameInfo
}
import com.intellij.refactoring.rename {
    NameSuggestionProvider
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import java.lang {
    JString=String,
    ObjectArray
}
import java.util {
    Set
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    CeylonFile
}

class CeylonNameSuggestionProvider() satisfies NameSuggestionProvider {

    shared actual SuggestedNameInfo? getSuggestedNames(PsiElement element, PsiElement nameSuggestionContext, Set<JString> result) {
        if (element.language.isKindOf(CeylonLanguage.instance),
            is CeylonFile file = element.containingFile,
            is CeylonPsi.DeclarationPsi element) {
            nodes.renameProposals(element.ceylonNode, file.compilationUnit).map(javaString).each(result.add);
            return object extends SuggestedNameInfo(result.toArray(ObjectArray<JString>(0))) {};
        }
        return null;
    }

}