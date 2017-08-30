import com.intellij.openapi.application {
    QueryExecutorBase
}
import com.intellij.openapi.components {
    ServiceManager
}
import com.intellij.psi {
    PsiReference,
    PsiNameIdentifierOwner,
    PsiElement
}
import com.intellij.psi.search {
    PsiSearchHelper,
    UsageSearchContext,
    TextOccurenceProcessor
}
import com.intellij.psi.search.searches {
    ReferencesSearch
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.intellij.util {
    Processor
}

import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        needReadAccess
    }
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonCompositeElement,
    CeylonPsi,
    CeylonFile
}

"Finds references to import aliases. For example, when Find Usages is called on `String`,
 this will also look for `JString` in files that have imports like:
 
     import java.lang {
         JString=String
     }
 "
shared class ImportAliasReferencesSearch() extends
        QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>() {

    shared actual void processQuery(ReferencesSearch.SearchParameters params, Processor<PsiReference> consumer) {
        if (is PsiNameIdentifierOwner toSearch = params.elementToSearch,
            toSearch.containingFile is CeylonFile,
            exists name = toSearch.name) {

            value helper = ServiceManager.getService(params.project, `PsiSearchHelper`);
            value scope = needReadAccess(() => params.effectiveSearchScope);
            value processor = object satisfies TextOccurenceProcessor {
                shared actual Boolean execute(PsiElement element, Integer offsetInElement) {
                    if (is CeylonCompositeElement element,
                        exists im = PsiTreeUtil.getParentOfType(element, `CeylonPsi.ImportMemberPsi`),
                        exists al = im.ceylonNode.\ialias,
                        im.ceylonNode.identifier.text == name) {

                        params.optimizer.searchWord(al.identifier.text, scope, true, toSearch);
                    }
                    return true; // TODO what should we return?
                }
            };
            helper.processElementsWithWord(processor, scope, name, UsageSearchContext.inCode, true);
        }
    }
}
