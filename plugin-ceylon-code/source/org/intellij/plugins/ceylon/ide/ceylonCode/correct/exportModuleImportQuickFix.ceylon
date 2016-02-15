import com.redhat.ceylon.ide.common.correct {
    ExportModuleImportQuickFix
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.editor {
    Document,
    Editor
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}
import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
}
import org.intellij.plugins.ceylon.ide.ceylonCode.imports {
    ideaModuleImportUtils
}

object ideaExportModuleImportQuickFix
        satisfies ExportModuleImportQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & AbstractIntention {
    
    shared actual void newExportModuleImportProposal(IdeaQuickFixData data, Unit u,
        String desc, String name, String version) {
        
        data.registerFix(desc, null, null, ideaIcons.modules, false, 
            void (Project project, Editor editor, PsiFile psiFile) {
                applyChanges(data.project, u, name);
            }
        );
    }
    
    shared actual AbstractModuleImportUtil<CeylonFile,Module,Document,InsertEdit,TextEdit,TextChange> importUtil
            => ideaModuleImportUtils;
    

}