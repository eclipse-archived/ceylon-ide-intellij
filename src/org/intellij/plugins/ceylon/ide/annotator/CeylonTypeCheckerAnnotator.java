package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.jetbrains.annotations.NotNull;

public class CeylonTypeCheckerAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof CeylonFile)) {
            return;
        }
        final CeylonFile ceylonFile = (CeylonFile) element;

        PhasedUnit phasedUnit = TypeCheckerInvoker.invokeTypeChecker(ceylonFile);

        if (phasedUnit == null) return;

        if (phasedUnit.getCompilationUnit() != null) {
            phasedUnit.getCompilationUnit().visit(new CeylonTypeCheckerVisitor(holder));
        }
    }

}
