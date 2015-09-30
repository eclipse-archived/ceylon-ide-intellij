import com.redhat.ceylon.ide.common.correct {
    AddAnnotationQuickFix,
    RemoveAnnotationQuickFix
}
import com.intellij.openapi.editor {
    Document,
    Editor
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable,
    Declaration
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.lang.annotation {
    Annotation
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}
import ceylon.interop.java {
    CeylonList
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

shared class AddRemoveAnnotationIntention(Document doc, Annotation annotation) 
        satisfies AddAnnotationQuickFix<Document, InsertEdit, TextEdit, TextChange, TextRange, TypeChecker>
                & RemoveAnnotationQuickFix<Document, InsertEdit, TextEdit, TextChange, TextRange, TypeChecker>
                & IdeaDocumentChanges {
    
    shared actual void newAddAnnotationQuickFix(Referenceable dec, String text, String desc, 
        Integer offset, TextChange change, TextRange? selection) {
        
        annotation.registerFix(object extends BaseIntentionAction() {
            shared actual String familyName => "Ceylon Intentions";
            
            shared actual void invoke(Project? project, Editor editor, PsiFile? psiFile) {
                change.apply();
                if (exists selection) {
                    editor.selectionModel.setSelection(selection.startOffset, selection.endOffset);
                    editor.caretModel.moveToOffset(selection.endOffset);
                }
            }
            
            shared actual Boolean isAvailable(Project? project, Editor? editor, PsiFile? psiFile)
                    => true;
            
            shared actual String text => desc;
        });
    }
    
    shared actual void newRemoveAnnotationQuickFix(Declaration dec, String ann,
        String desc, Integer offset, TextChange change, TextRange selection) {

        annotation.registerFix(object extends BaseIntentionAction() {
            shared actual String familyName => "Ceylon Intentions";
            
            shared actual void invoke(Project? project, Editor editor, PsiFile? psiFile) {
                change.apply();
                editor.selectionModel.setSelection(selection.startOffset, selection.endOffset);
                editor.caretModel.moveToOffset(selection.endOffset);
            }
            
            shared actual Boolean isAvailable(Project? project, Editor? editor, PsiFile? psiFile)
                    => true;
            
            shared actual String text => desc;
        });        
    }

    
    shared actual Integer getTextEditOffset(TextEdit change) => change.start;
    
    shared actual List<PhasedUnit> getUnits(TypeChecker tc) 
            => CeylonList(tc.phasedUnits.phasedUnits);
    
    shared actual Indents<Document> indents => ideaIndents;
    
    shared actual TextRange newRegion(Integer start, Integer length) => TextRange.from(start, length);
    
    // TODO this will break if we try to add an annotation in an other compilation unit!!!
    shared actual TextChange newTextChange(PhasedUnit u) => TextChange(doc);
    
    
}