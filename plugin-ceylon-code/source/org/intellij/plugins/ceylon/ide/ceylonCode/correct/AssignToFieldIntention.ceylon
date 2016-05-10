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
    AssignToFieldQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class AssignToFieldIntention()
        extends GenericIntention()
        satisfies AssignToFieldQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> {
    
    familyName => "Assign to field";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        value decl = nodes.findDeclaration(data.rootNode, data.node);
        
        addAssignToFieldProposal(data, file, statement, decl);
    }
}