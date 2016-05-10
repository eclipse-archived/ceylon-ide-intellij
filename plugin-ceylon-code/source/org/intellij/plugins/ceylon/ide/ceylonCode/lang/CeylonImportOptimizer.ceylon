import com.intellij.lang {
    ImportOptimizer
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.ide.common.imports {
    AbstractImportsCleaner
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.lang {
    Runnable
}

import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    DocumentWrapper
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

shared class CeylonImportOptimizer()
        satisfies AbstractImportsCleaner & ImportOptimizer {
    
    shared actual Runnable processFile(PsiFile psiFile) {
        value doc = psiFile.viewProvider.document;

        assert(is CeylonFile psiFile);
        value cu = psiFile.compilationUnit;
        
        return object satisfies Runnable {
            shared actual void run() {
                cleanImports(cu, DocumentWrapper(doc));
            }
        };
    }
    
    shared actual Boolean supports(PsiFile? psiFile)
            => psiFile is CeylonFile;

    shared actual Declaration? select(List<Declaration> proposals) {
        // TODO
        return proposals.first;
    }
}
