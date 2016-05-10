import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.correct {
    AddInitializerQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    TypedDeclaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

object ideaAddInitializerQuickFix
        satisfies AddInitializerQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        TypedDeclaration dec, Integer offset, Integer length, TextChange change) {

        data.registerFix(desc, change, TextRange.from(offset, length), 
            ideaIcons.correction, false, (project, editor, file) {
                IdeaInitializer().addInitializer(
                    editor.document, 
                    DefaultRegion(offset, length),
                    dec.type,
                    dec.unit,
                    dec.scope
                );
            }
        );
    }
}
