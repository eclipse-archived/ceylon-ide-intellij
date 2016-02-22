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
    VerboseRefinementQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class VerboseRefinementIntention()
        extends AbstractIntention()
        satisfies VerboseRefinementQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Convert refinement to verbose form";
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, TextChange change)
            => makeAvailable(desc, change);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        addVerboseRefinementProposal(data, file, statement);
    }
}

shared class ShortcutRefinementIntention()
        extends AbstractIntention()
        satisfies VerboseRefinementQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Convert refinement to shortcut form";
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, TextChange change)
            => makeAvailable(desc, change);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        addShortcutRefinementProposal(data, file, statement);
    }
}
