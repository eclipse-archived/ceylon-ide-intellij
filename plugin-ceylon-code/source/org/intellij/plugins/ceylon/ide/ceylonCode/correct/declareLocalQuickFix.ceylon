import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document,
    EditorFactory,
    Editor
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.correct {
    DeclareLocalQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

object ideaDeclareLocalQuickFix 
        satisfies DeclareLocalQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,IdeaLinkedMode,LookupElement,Module,IdeaQuickFixData,TextRange>
                & IdeaDocumentChanges & IdeaQuickFix {
    
    shared actual void addEditableRegion(IdeaLinkedMode lm, Document doc, Integer start,
        Integer len, Integer exitSeqNumber, LookupElement[] proposals) {
        
        lm.addEditableRegion(start, len, proposals);
    }
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner,
        Integer exitSeqNumber, Integer exitPosition) {
        
        // TODO this is lame, not guaranteed to be the active editor
        value editors = EditorFactory.instance.getEditors(doc);
        if (editors.size > 0) {
            lm.buildTemplate(editors.get(0));
        }
    }
    
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode();
    
    shared actual void newDeclareLocalQuickFix(IdeaQuickFixData data, String desc, TextChange change,
        Tree.Term term, Tree.BaseMemberExpression bme) {
        
        value callback = void (Project project, Editor editor, PsiFile psiFile) {
            enableLinkedMode(data, term, change);
        };
        
        data.registerFix { 
            desc = desc; 
            change = change; 
            callback = callback; 
            image = ideaIcons.correction; 
        };
    }
}
