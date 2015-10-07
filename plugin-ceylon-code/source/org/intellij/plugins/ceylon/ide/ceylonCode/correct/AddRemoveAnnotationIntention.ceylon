import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.ide.common.correct {
    AddAnnotationQuickFix,
    RemoveAnnotationQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable,
    Declaration
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared object addRemoveAnnotationIntention
        satisfies AddAnnotationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,TypeChecker,IdeaQuickFixData,LookupElement>
                & RemoveAnnotationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,TypeChecker,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges & AbstractIntention {
    
    shared actual void newAddAnnotationQuickFix(Referenceable dec, String text, String desc,
        Integer offset, TextChange change, TextRange? selection, IdeaQuickFixData data) {
        
        data.registerFix(desc, change, selection, ideaIcons.correction);
    }
    
    shared actual void newRemoveAnnotationQuickFix(Declaration dec, String ann,
        String desc, Integer offset, TextChange change, TextRange selection, IdeaQuickFixData data) {
        
        data.registerFix(desc, change, selection, ideaIcons.correction);
    }
    
}
