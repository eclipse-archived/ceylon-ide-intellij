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
    AddSatisfiesQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    TypeDeclaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

object ideaAddSatisfiesQuickFix
        satisfies AddSatisfiesQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    shared actual void newProposal(IdeaQuickFixData data, TypeDeclaration typeParam,
        String description, String missingSatisfiedTypeText, TextChange change, 
        DefaultRegion? region) {
        
        value range = if (exists region) then TextRange.from(region.start, region.length) else null;
        
        data.registerFix(description, change, range, ideaIcons.addCorrection);
    }
}
