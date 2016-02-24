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
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.correct {
    ConvertToClassQuickFix,
    AbstractConvertToClassProposal
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
import com.intellij.openapi.project {
    Project
}

shared class ConvertToClassIntention()
        extends AbstractIntention()
        satisfies ConvertToClassQuickFix<Module,IdeaQuickFixData>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    familyName => "Convert object to class";
    
    shared actual void newProposal(IdeaQuickFixData data, String desc,
        Tree.ObjectDefinition def) {
        
        makeAvailable { 
            desc = desc;
            callback = (p, e, f) {
                ConvertToClassProposal(p).applyChanges(f.viewProvider.document, def);
            };
        };
    }
    
    shared actual void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset) {
        value decl = nodes.findDeclaration(data.rootNode, data.node);
        
        addConvertToClassProposal(data, decl);
    }
}

class ConvertToClassProposal(Project project)
        satisfies AbstractConvertToClassProposal<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement,IdeaLinkedMode> 
                & IdeaDocumentChanges
                & IdeaQuickFix
                & IdeaLinkedModeSupport {
    
    shared actual void performChange(TextChange change) {
        change.apply(project);
    }
}
