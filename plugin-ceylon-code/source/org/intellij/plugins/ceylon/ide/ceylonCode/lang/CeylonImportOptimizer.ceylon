import com.intellij.lang {
    ImportOptimizer
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.ide.common.imports {
    AbstractImportsCleaner
}
import com.redhat.ceylon.ide.common.util {
    Indents
}

import java.lang {
    Runnable
}

import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    InsertEdit,
    TextEdit,
    TextChange,
    IdeaDocumentChanges
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}

shared class CeylonImportOptimizer()
        satisfies AbstractImportsCleaner<Document, InsertEdit, TextEdit, TextChange>
                & IdeaDocumentChanges
                & ImportOptimizer {

    shared actual String getDocContent(Document doc, Integer start, Integer length)
            => doc.getText(TextRange.from(start, length));
    
    shared actual Indents<Document> indents => ideaIndents;
    
    shared actual Runnable processFile(PsiFile psiFile) {
        value doc = psiFile.viewProvider.document;
        value change = TextChange(doc);
        
        assert(is CeylonFile psiFile);
        value cu = psiFile.compilationUnit;
        
        return object satisfies Runnable {
            shared actual void run() {
                if (cleanImports(cu, doc, change)) {
                    change.apply();
                }
            }
        };
    }
    
    shared actual Boolean supports(PsiFile? psiFile)
            => psiFile is CeylonFile;
    
    
    
}