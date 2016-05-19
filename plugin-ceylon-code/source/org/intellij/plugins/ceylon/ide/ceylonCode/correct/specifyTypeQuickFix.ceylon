import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.redhat.ceylon.ide.common.correct {
    SpecifyTypeQuickFix
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode,
    IdeaLinkedModeSupport,
    ideaCompletionManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared object ideaSpecifyTypeQuickFix satisfies IdeaSpecifyTypeQuickFix { }

shared interface IdeaSpecifyTypeQuickFix
        satisfies SpecifyTypeQuickFix<Document,LookupElement,IdeaLinkedMode>
                & IdeaLinkedModeSupport {

    completionManager => ideaCompletionManager;
    
    shared actual Document getNativeDocument(CommonDocument doc) {
        assert(is IdeaDocument doc);
        return doc.nativeDocument;
    }
}

shared class IdeaSpecifyTypeIntention()
        extends AbstractIntention()
        satisfies IdeaSpecifyTypeQuickFix {

    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value declaration = nodes.findDeclaration(data.rootNode, data.node);
        addTypingProposals(data, declaration);
    }
    
    familyName => "Specify type";
}
