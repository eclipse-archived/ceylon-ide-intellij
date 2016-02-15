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
    CreateParameterQuickFix
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Type
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared object ideaCreateParameterQuickFix
        satisfies CreateParameterQuickFix<CeylonFile,Module,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges & AbstractIntention {
    
    shared actual void newCreateParameterProposal(IdeaQuickFixData data, String desc, Declaration dec, Type? type,
        TextRange selection, Icons image, TextChange change, Integer exitPos) {
        
        data.registerFix(desc, change, selection, ideaIcons.addCorrection);
    }

}
