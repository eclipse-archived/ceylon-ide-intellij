import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Editor,
    Document
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
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.ide.common.correct {
    RefineFormalMembersQuickFix
}

import java.lang {
    Character
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared object ideaRefineFormalMembersQuickFix 
        satisfies RefineFormalMembersQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,TypeChecker,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges & AbstractIntention {
    
     shared actual Character getDocChar(Document doc, Integer offset)
            => Character(doc.getText(TextRange.from(offset, 1)).first else ' ');
    
    shared actual void newRefineFormalMembersProposal(IdeaQuickFixData data, String desc) {
        data.registerFix(desc, null, null, ideaIcons.refinement, false, void (Project project, Editor editor, PsiFile psiFile) {
            assert(is CeylonFile psiFile);
            
            if (exists change = refineFormalMembers(data, psiFile, editor.caretModel.offset)) {
                change.apply();
            }
        });
    }
}
