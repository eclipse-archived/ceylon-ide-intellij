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
    AddParameterQuickFix
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
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

object ideaAddParameterQuickFix
        satisfies AddParameterQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
        & IdeaDocumentChanges
        & AbstractIntention {
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        Declaration dec, Type? type, Integer offset, Integer length, 
        TextChange change, Integer exitPos) {
        
        data.registerFix(desc, change, TextRange.from(offset, length), 
            ideaIcons.addCorrection, false, (project, editor, file) {
                IdeaInitializer().addInitializer(
                    editor.document, 
                    DefaultRegion(offset, length),
                    type, 
                    dec.unit,
                    dec.scope
                );
            });
    }
}