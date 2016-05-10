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
    IdeaLinkedMode,
    IdeaLinkedModeSupport
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

object ideaDeclareLocalQuickFix 
        satisfies DeclareLocalQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,IdeaLinkedMode,LookupElement,IdeaQuickFixData,TextRange>
                & IdeaDocumentChanges
                & IdeaQuickFix
                & IdeaLinkedModeSupport {

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
