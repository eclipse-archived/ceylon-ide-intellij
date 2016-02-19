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
    CreateEnumQuickFix
}
import com.redhat.ceylon.ide.common.doc {
    Icons
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

shared object ideaCreateEnumQuickFix
        satisfies CreateEnumQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    shared actual Integer getDocLength(Document doc) => doc.textLength;
    
    shared actual void consumeNewQuickFix(String desc, Icons image, Integer offset, TextChange change, IdeaQuickFixData data) {
        Icon? icon = switch(image)
        case (Icons.classes) ideaIcons.classes
        case (Icons.attributes) ideaIcons.attributes
        case (Icons.interfaces) ideaIcons.interfaces
        else null;
        
        data.registerFix(desc, change, TextRange.from(offset, 0), icon);
    }
}
