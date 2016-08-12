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
    ObjectArray,
    Thread
}
import java.util {
    Set
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    CeylonFile
}

class CeylonNameSuggestionProvider() satisfies NameSuggestionProvider {

    value noStrings = ObjectArray<JString>(0);

    shared actual SuggestedNameInfo? getSuggestedNames(PsiElement element, PsiElement nameSuggestionContext, Set<JString> result) {
        Thread.sleep(300); //TODO: this is awful, how can we fix it?
        if (element.language.isKindOf(ceylonLanguage)) {
            nodes.renameProposals {
                node = if (is CeylonPsi.DeclarationPsi element)
                    then element.ceylonNode else null;
                rootNode = if (is CeylonFile file = element.containingFile)
                    then file.compilationUnit else null;
            }
            .map(javaString)
            .each(result.add);
            return object extends SuggestedNameInfo(result.toArray(noStrings)) {};
        }
        else {
            return null;
        }
    }

}