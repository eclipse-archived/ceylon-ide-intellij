import ceylon.interop.java {
    CeylonList
}

import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.fileEditor {
    FileDocumentManager
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.openapi.vfs {
    VirtualFileManager
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

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents,
    ideaIcons
}

shared object ideaCreateEnumIntention
        satisfies CreateEnumQuickFix<TypeChecker,Document,InsertEdit,TextEdit,TextChange,IdeaQuickFixData>
                & IdeaDocumentChanges {
    
    shared actual Integer getDocLength(Document doc) => doc.textLength;
    
    shared actual List<PhasedUnit> getUnits(TypeChecker typeChecker) {
        return CeylonList(typeChecker.phasedUnits.phasedUnits);
    }
    
    shared actual Indents<Document> indents => ideaIndents;
    
    shared actual TextChange newTextChange(PhasedUnit u) {
        assert (exists vfile = VirtualFileManager.instance.findFileByUrl("file://" + u.unit.fullPath));
        
        return TextChange(FileDocumentManager.instance.getDocument(vfile));
    }
    
    shared actual void consumeNewQuickFix(String desc, Icons image, Integer offset, TextChange change, IdeaQuickFixData data) {
        Icon? icon = switch(image)
        case (Icons.classes) ideaIcons.classes
        case (Icons.attributes) ideaIcons.attributes
        case (Icons.interfaces) ideaIcons.interfaces
        else null;
        
        data.registerFix(desc, change, TextRange.from(offset, 0), icon);
    }
}
