import ceylon.interop.java {
    CeylonList
}

import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.lang.annotation {
    Annotation
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
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.ide.common.correct {
    CreateEnumQuickFix
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.ide.common.util {
    Indents
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}

shared class CreateEnumIntention(Document doc, Annotation annotation)
        extends CreateEnumQuickFix<TypeChecker, Document, InsertEdit, TextEdit, TextChange>()
        satisfies IdeaDocumentChanges {
    
    shared actual Integer getDocLength(Document doc) => doc.textLength;
    
    shared actual List<PhasedUnit> getUnits(TypeChecker typeChecker) {
        return CeylonList(typeChecker.phasedUnits.phasedUnits);
    }
    
    shared actual Indents<Document> indents => ideaIndents;
    
    shared actual TextChange newTextChange(PhasedUnit unit) => TextChange(doc);

    shared actual void consumeNewQuickFix(String def, String desc, Icons image, Integer offset, TextChange change) {
        annotation.registerFix(object extends BaseIntentionAction() {
            shared actual String familyName => "Ceylon Intentions";
            
            shared actual void invoke(Project? project, Editor? editor, PsiFile? psiFile) {
                change.apply();
            }
            
            shared actual Boolean isAvailable(Project? project, Editor? editor, PsiFile? psiFile) => true;
            
            shared actual String text => desc;
        });
    }
}
