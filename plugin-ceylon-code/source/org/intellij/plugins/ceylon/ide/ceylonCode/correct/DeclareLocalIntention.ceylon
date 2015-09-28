import com.redhat.ceylon.ide.common.correct {
    DeclareLocalQuickFix,
    getDeclareLocalTerm
}
import com.intellij.openapi.editor {
    Document,
    Editor
}
import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiFile
}
import com.intellij.openapi.application {
    ApplicationManager,
    ModalityState
}
import java.lang {
    Runnable
}
import com.intellij.openapi.fileEditor {
    FileEditorManager
}

shared class DeclareLocalIntention(Tree.CompilationUnit rootNode, Node node, Project project)
        extends BaseIntentionAction()
        satisfies DeclareLocalQuickFix<Document, InsertEdit, TextEdit, TextChange, IdeaLinkedMode, LookupElement>
                & IdeaDocumentChanges {
    
    shared actual void addEditableRegion(IdeaLinkedMode lm, Document doc, Integer start,
        Integer len, Integer exitSeqNumber, LookupElement[] proposals) {
        
        lm.addEditableRegion(start, len, proposals);
    }
    
    shared actual void installLinkedMode(Document doc, IdeaLinkedMode lm, Object owner,
        Integer exitSeqNumber, Integer exitPosition) {
        
        ApplicationManager.application.invokeAndWait(object satisfies Runnable {
            shared actual void run() {
                value editor = FileEditorManager.getInstance(project).selectedTextEditor;
                editor.caretModel.moveToOffset(node.startIndex.intValue());
                editor.document.deleteString(node.startIndex.intValue(), node.startIndex.intValue() + 5);
                lm.buildTemplate(editor, true);
            }
        }, ModalityState.any());
    }
    
    shared actual IdeaLinkedMode newLinkedMode() => IdeaLinkedMode("value", node.startIndex.intValue());

    shared actual String familyName => "Ceylon Intentions";
    shared actual String text => getName(node);
    
    shared actual void invoke(Project project, Editor editor, PsiFile? psiFile) {
        value change = TextChange(editor.document);
        addDeclareLocalProposal(rootNode, node, editor.document, change);
    }
    
    shared actual Boolean isAvailable(Project? project, Editor? editor, PsiFile? psiFile)
            => getDeclareLocalTerm(rootNode, node) exists;
    
    shared actual void applyChange(Document doc, TextChange change) => change.apply();
}
