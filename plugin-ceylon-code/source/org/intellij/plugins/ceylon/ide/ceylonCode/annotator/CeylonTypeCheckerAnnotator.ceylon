import com.intellij.lang.annotation {
    AnnotationHolder,
    Annotator
}
import com.intellij.psi {
    PsiElement
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsi
}

shared class CeylonTypeCheckerAnnotator() satisfies Annotator {

    shared actual void annotate(PsiElement element, AnnotationHolder holder) {
        if (is CeylonPsi.CompilationUnitPsi element,
            is CeylonFile ceylonFile = element.containingFile,
            exists pu = ceylonFile.ensureTypechecked(),
            exists cu = pu.compilationUnit) {

            cu.visit(ErrorsVisitor(holder, ceylonFile));
        }
    }
}
