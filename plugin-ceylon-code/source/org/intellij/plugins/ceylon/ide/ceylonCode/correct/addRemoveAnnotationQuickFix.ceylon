import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.correct {
    AddAnnotationQuickFix,
    RemoveAnnotationQuickFix
}
import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable,
    Declaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.imports {
    ideaModuleImportUtils
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared object ideaAddRemoveAnnotationQuickFix
        satisfies AddAnnotationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & RemoveAnnotationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges & IdeaQuickFix {
    
    shared actual void newAddAnnotationQuickFix(Referenceable dec, String text, String desc,
        Integer offset, TextChange change, TextRange? selection, IdeaQuickFixData data) {
        
        data.registerFix(desc, change, selection, ideaIcons.correction);
    }
    
    shared actual void newRemoveAnnotationQuickFix(Declaration dec, String ann,
        String desc, Integer offset, TextChange change, TextRange selection, IdeaQuickFixData data) {
        
        data.registerFix(desc, change, selection, ideaIcons.correction);
    }
    
    shared actual AbstractModuleImportUtil<CeylonFile,Module,Document,InsertEdit,TextEdit,TextChange> moduleImportUtil
            => ideaModuleImportUtils;
    
    shared actual void newCorrectionQuickFix(String desc, TextChange change, TextRange? selection) {}
    
    
}
