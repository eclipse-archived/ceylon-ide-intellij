import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.intellij.openapi.\imodule {
    Module
}
import com.redhat.ceylon.ide.common.correct {
    AssertExistsDeclarationQuickFix
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

shared class AssertExistsDeclarationIntention()
        extends AbstractIntention()
        satisfies AssertExistsDeclarationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Assert value exists or is non-empty";
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        TextChange change, DefaultRegion region)
            => makeAvailable(desc, change, region);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);
        
        addAssertExistsDeclarationProposals(data, file, decl);
    }
}
