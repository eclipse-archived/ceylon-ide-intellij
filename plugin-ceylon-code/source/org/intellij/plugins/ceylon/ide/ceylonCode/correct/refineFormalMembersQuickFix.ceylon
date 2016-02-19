import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Editor,
    Document
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
import com.redhat.ceylon.ide.common.correct {
    RefineFormalMembersQuickFix
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared object ideaRefineFormalMembersQuickFix 
        satisfies RefineFormalMembersQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges & IdeaQuickFix {
    
     shared actual Character getDocChar(Document doc, Integer offset)
            => doc.getText(TextRange.from(offset, 1)).first else ' ';
    
    shared actual void newRefineFormalMembersProposal(IdeaQuickFixData data, String desc) {
        data.registerFix(desc, null, null, ideaIcons.refinement, false, void (Project project, Editor editor, PsiFile psiFile) {
            assert(is CeylonFile psiFile);
            
            if (exists change = refineFormalMembers(data, editor.document, editor.caretModel.offset)) {
                change.apply();
            }
        });
    }
}
