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
    ConvertThenElseToIfElse
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

shared class ConvertThenElseToIfElseIntention()
        extends AbstractIntention()
        satisfies ConvertThenElseToIfElse<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        TextChange change, DefaultRegion region)
            => makeAvailable(desc, change, region);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        addConvertToIfElseProposal(data, file, file.viewProvider.document, statement);
    }
}
