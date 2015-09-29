import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.psi {
    PsiFile
}
import com.intellij.codeInsight.lookup {
    LookupElement
}

shared class ImportTypeIntention(LookupElement lookupElement) extends BaseIntentionAction() {
    
    shared actual String familyName => "Ceylon Intentions";
    
    shared actual void invoke(Project? project, Editor editor, PsiFile? psiFile) {
        assert(is TextChange change = lookupElement.\iobject);
        change.apply();
    }
    
    shared actual Boolean isAvailable(Project? project, Editor? editor, PsiFile? psiFile) 
            => true;
    
    shared actual String text => lookupElement.lookupString;
}