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
    ConvertToDefaultConstructorQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ConvertToDefaultConstructorIntention()
        extends AbstractIntention()
        satisfies ConvertToDefaultConstructorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Convert to class with default constructor";
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, 
        TextChange change, DefaultRegion region)
            => makeAvailable(desc, change, region);
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value statement = nodes.findStatement(data.rootNode, data.node);
        
        addConvertToDefaultConstructorProposal(data, file, statement);
    }
}
