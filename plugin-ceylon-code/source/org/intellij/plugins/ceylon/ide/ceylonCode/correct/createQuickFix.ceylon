import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.ide.common.correct {
    CreateQuickFix,
    CreateParameterQuickFix
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Type,
    Scope
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

object ideaCreateQuickFix
        satisfies CreateQuickFix<CeylonFile,Module,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement>
                & IdeaQuickFix & IdeaDocumentChanges {
    
    shared actual void newCreateQuickFix(IdeaQuickFixData data, String desc, Scope scope, Unit unit,
        Type? returnType, Icons icons, TextChange change, Integer exitPos, TextRange selection) {
        
        Icon icon = switch(icons)
        case (Icons.localClass) ideaIcons.classes
        case (Icons.localAttribute) ideaIcons.local
        else ideaIcons.addCorrection;
        
        data.registerFix(desc, change, selection, icon);
    }
    
    shared actual CreateParameterQuickFix<CeylonFile,Module,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> createParameterQuickFix
            => ideaCreateParameterQuickFix;
}
