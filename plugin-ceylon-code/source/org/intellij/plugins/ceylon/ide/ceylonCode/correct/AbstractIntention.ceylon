import com.redhat.ceylon.ide.common.correct {
    AbstractQuickFix,
    ImportProposals
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
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
import ceylon.interop.java {
    CeylonList
}
import com.intellij.openapi.vfs {
    VirtualFileManager,
    VirtualFile
}
import com.intellij.openapi.fileEditor {
    FileDocumentManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    ideaCompletionManager
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared interface AbstractIntention
        satisfies AbstractQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,TypeChecker,LookupElement> {
    
    shared actual Integer getTextEditOffset(TextEdit change) => change.start;
    
    shared actual List<PhasedUnit> getUnits(TypeChecker tc)
            => CeylonList(tc.phasedUnits.phasedUnits);
    
    shared actual Indents<Document> indents => ideaIndents;
    
    shared actual TextRange newRegion(Integer start, Integer length) => TextRange.from(start, length);
    
    shared actual TextChange newTextChange(String desc, PhasedUnit|CeylonFile unitOrFile) {
        VirtualFile? vfile = if (is PhasedUnit unitOrFile) 
                      then VirtualFileManager.instance.findFileByUrl("file://" + unitOrFile.unit.fullPath)
                      else unitOrFile.virtualFile;

        assert (exists vfile);
        
        return TextChange(FileDocumentManager.instance.getDocument(vfile));
    }
    
    shared actual IdeCompletionManager<out Anything,out Anything,LookupElement,Document> completionManager
            => ideaCompletionManager;
    
    shared actual ImportProposals<out Anything,out Anything,Document,InsertEdit,TextEdit,TextChange> importProposals
            => ideaImportProposals;
}
