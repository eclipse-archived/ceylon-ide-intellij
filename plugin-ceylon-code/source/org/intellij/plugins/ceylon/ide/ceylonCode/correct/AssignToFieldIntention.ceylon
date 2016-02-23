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
    AssignToFieldQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class AssignToFieldIntention()
        extends AbstractIntention()
        satisfies AssignToFieldQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Assign to field";
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, TextChange change)
            => makeAvailable(desc, change);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        value decl = nodes.findDeclaration(data.rootNode, data.node);
        
        addAssignToFieldProposal(data, file, statement, decl);
    }
}