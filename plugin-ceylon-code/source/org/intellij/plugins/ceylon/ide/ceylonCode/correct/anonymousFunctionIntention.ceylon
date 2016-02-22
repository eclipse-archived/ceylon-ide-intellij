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
    ConvertToSpecifierQuickFix,
    AnonymousFunctionQuickFix,
    ConvertToBlockQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class AnonymousFunctionIntention()
        extends AbstractIntention()
        satisfies AnonymousFunctionQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => addAnonymousFunctionProposals(data, file);
    
    convertToBlockQuickFix => object satisfies ConvertToBlockQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
            & IdeaDocumentChanges
            & IdeaQuickFix {

        shared actual void newProposal(IdeaQuickFixData data, String desc, 
            TextChange change, DefaultRegion region) {
            makeAvailable(desc, change, region);
        }
    };
    
    convertToSpecifierQuickFix => object satisfies ConvertToSpecifierQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
            & IdeaDocumentChanges
            & IdeaQuickFix {

        shared actual void newProposal(IdeaQuickFixData data, String desc, 
            TextChange change, DefaultRegion region) {
            makeAvailable(desc, change, region);
        }
    };
    
    familyName => "Convert anonymous function block";
}