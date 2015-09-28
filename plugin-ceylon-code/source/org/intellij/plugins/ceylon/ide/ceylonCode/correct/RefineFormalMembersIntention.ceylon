import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.openapi.editor {
    Editor,
    Document
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.correct {
    RefineFormalMembersQuickFix,
    ImportProposals,
    getRefineFormalMembersScope
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import java.lang {
    Character
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}
import com.intellij.openapi.util {
    TextRange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    ideaCompletionManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class RefineFormalMembersIntention(Node node, Boolean ambiguousError) 
        extends BaseIntentionAction()
        satisfies RefineFormalMembersQuickFix<Document,InsertEdit,TextEdit,TextChange>
                & IdeaDocumentChanges {
    
    shared actual String familyName => "Ceylon intentions";
    shared actual String text => getName(node, ambiguousError) else "<error>";
    
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        assert(is CeylonFile psiFile);
        value change = TextChange(editor.document);
        refineFormalMembers(editor.document, change, psiFile.compilationUnit, node, editor.caretModel.offset);
        change.apply();
    }
    
    shared actual Boolean isAvailable(Project project, Editor editor, PsiFile psiFile)
        => getRefineFormalMembersScope(node) exists;
    
    shared actual IdeCompletionManager<out Object,out Object,out Object,Document> completionManager
            => ideaCompletionManager;
    
    shared actual Character getDocChar(Document doc, Integer offset)
            => Character(doc.getText(TextRange.from(offset, 1)).first else ' ');
    
    shared actual ImportProposals<out Object,out Object,Document,InsertEdit,TextEdit,TextChange> importProposals
            => ideaImportProposals;
    
    shared actual Indents<Document> indents => ideaIndents;
}
