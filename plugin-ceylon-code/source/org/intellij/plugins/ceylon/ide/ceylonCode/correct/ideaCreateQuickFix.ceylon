import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.ide.common.correct {
    CreateQuickFix,
    CreateParameterQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Type,
    Scope
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import javax.swing {
    Icon
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}
import com.intellij.icons {
    AllIcons
}
import com.intellij.codeInsight.lookup {
    LookupElement
}

object ideaCreateQuickFix
        satisfies CreateQuickFix<CeylonFile,TypeChecker,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement>
                & AbstractIntention & IdeaDocumentChanges {
    
    shared actual Integer getLineOfOffset(Document doc, Integer offset) => doc.getLineNumber(offset);
    
    shared actual void newCreateQuickFix(IdeaQuickFixData data, String desc, Scope scope, Unit unit,
        Type? returnType, Icons icons, TextChange change, Integer exitPos, TextRange selection) {
        
        Icon icon = switch(icons)
        case (Icons.localClass) ideaIcons.classes
        case (Icons.localAttribute) ideaIcons.local
        else AllIcons.General.\iAdd;
        
        data.registerFix(desc, change, selection, icon);
    }
    
    shared actual CreateParameterQuickFix<CeylonFile,TypeChecker,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> createParameterQuickFix
            => ideaCreateParameterQuickFix;
    
}