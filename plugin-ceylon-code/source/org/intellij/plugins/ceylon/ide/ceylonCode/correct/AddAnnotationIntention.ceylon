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
    AddAnnotationQuickFix
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable
}

import org.intellij.plugins.ceylon.ide.ceylonCode.imports {
    ideaModuleImportUtils
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class AddAnnotationIntention() 
        extends AbstractIntention()
        satisfies AddAnnotationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {

    familyName => "Add annotation";
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);
        
        // TODO this method can potentially generate more than one proposal, which
        // means we should have more than one intention :/
        addContextualAnnotationProposals(data, decl, data.nativeDoc, offset);
    }
    
    moduleImportUtil => ideaModuleImportUtils;
    
    shared actual void newAddAnnotationQuickFix(Referenceable dec, String text, 
        String desc, Integer offset, TextChange change, TextRange? selection,
        IdeaQuickFixData data) {
        
        makeAvailable(desc, change);
    }
    
    shared actual void newCorrectionQuickFix(String desc, TextChange change, TextRange? selection) {}
    
}