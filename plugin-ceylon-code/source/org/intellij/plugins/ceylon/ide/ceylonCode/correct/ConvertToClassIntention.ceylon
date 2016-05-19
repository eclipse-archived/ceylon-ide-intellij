import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.project {
    Project
}
import com.redhat.ceylon.ide.common.correct {
    convertToClassQuickFix,
    AbstractConvertToClassProposal
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode,
    IdeaLinkedModeSupport
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class ConvertToClassIntention() extends AbstractIntention() {
    
    familyName => "Convert object to class";
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);

        convertToClassQuickFix.addConvertToClassProposal(data, decl);
    }
}

class ConvertToClassProposal(Project project)
        satisfies AbstractConvertToClassProposal<LookupElement,Document,IdeaLinkedMode>
                & IdeaLinkedModeSupport {

    shared actual Document getNativeDocument(CommonDocument doc) {
        assert(is IdeaDocument doc);
        return doc.nativeDocument;
    }
}
