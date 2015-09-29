import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document,
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.correct {
    DeclareLocalQuickFix,
    getDeclareLocalTerm
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode,
    ideaCompletionManager
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}

shared class DeclareLocalIntention(Tree.CompilationUnit rootNode, Node node, Project project)
        extends BaseIntentionAction()
        satisfies DeclareLocalQuickFix<Document, InsertEdit, TextEdit, TextChange, IdeaLinkedMode, LookupElement>
                & IdeaDocumentChanges {
    
    late Editor editor;
    
    shared actual void addEditableRegion(IdeaLinkedMode lm, Document doc, Integer start,
        Integer len, Integer exitSeqNumber, LookupElement[] proposals) {
        
        lm.addEditableRegion(start, len, proposals);
    }
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner,
        Integer exitSeqNumber, Integer exitPosition) {
        
        lm.buildTemplate(editor);
    }
    
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode(editor);

    shared actual String familyName => "Ceylon Intentions";
    shared actual String text => getName(node);
    
    shared actual void invoke(Project project, Editor editor, PsiFile? psiFile) {
        this.editor = editor;
        value change = TextChange(editor.document);
        addDeclareLocalProposal(rootNode, node, editor.document, change);
    }
    
    shared actual Boolean isAvailable(Project? project, Editor? editor, PsiFile? psiFile)
            => getDeclareLocalTerm(rootNode, node) exists;
    
    shared actual void applyChange(Document doc, TextChange change) => change.apply();

    shared actual IdeCompletionManager<out Object,out Object,LookupElement,Document> completionManager
            => ideaCompletionManager;
    
}
