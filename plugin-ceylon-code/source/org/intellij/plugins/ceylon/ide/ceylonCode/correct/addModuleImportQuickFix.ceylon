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
    AddModuleImportQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}

import org.intellij.plugins.ceylon.ide.ceylonCode.imports {
    ideaModuleImportUtils
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

object ideaAddModuleImportQuickFix
        satisfies AddModuleImportQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    importUtil => ideaModuleImportUtils;
    
    shared actual void newProposal(IdeaQuickFixData data, String desc, Unit unit,
        String name, String version)
            => data.registerFix {
                desc = desc;
                change = null;
                image = ideaIcons.correction;
                callback = (project, editor, file) {
                    ideaAddModuleImportQuickFix.applyChanges(data.project, unit, name, version);
                };
            };

}