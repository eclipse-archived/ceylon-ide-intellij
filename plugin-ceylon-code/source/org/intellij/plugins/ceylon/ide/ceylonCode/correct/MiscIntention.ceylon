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
    MiscQuickFixes,
    ConvertToBlockQuickFix,
    ConvertToGetterQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

shared abstract class MiscIntention()
        extends AbstractIntention()
        satisfies MiscQuickFixes<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
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
    
    convertToGetterQuickFix => object satisfies ConvertToGetterQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
            & IdeaDocumentChanges
            & IdeaQuickFix {

        shared actual void newProposal(IdeaQuickFixData data, String desc, 
            TextChange change, DefaultRegion region) {
            makeAvailable(desc, change, region);
        }
    };   
}

shared class AnonymousFunctionIntention() extends MiscIntention() {
    familyName => "Convert anonymous function block";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset)
            => addAnonymousFunctionProposals(data, file);
}

shared class DeclarationIntention() extends MiscIntention() {
    familyName => "Convert declaration";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);
        addDeclarationProposals(data, file, decl, offset);
    }
}
