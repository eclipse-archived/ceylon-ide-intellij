import com.intellij.codeInsight.completion {
    InsertHandler,
    InsertionContext
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.extensions {
    Extensions
}
import com.redhat.ceylon.model.typechecker.model {
    TypeDeclaration,
    Unit
}

object classInsertHandler satisfies InsertHandler<LookupElement> {
    
    shared actual void handleInsert(InsertionContext ctx, LookupElement t) {
        if (is [TypeDeclaration, Unit] seq = t.\iobject) {
            value importHandler = Extensions.getExtensions(importHandlerEpName).get(0);
            value decl = seq.first;
            value unit = seq[1];
            
            if (!exists im = unit.getImport(unit.getAliasedName(decl)),
                !decl.unit.\ipackage.nameAsString.equals("ceylon.language")) {
                
                importHandler.importDeclaration(decl, ctx);
            }
        }
    }
}
