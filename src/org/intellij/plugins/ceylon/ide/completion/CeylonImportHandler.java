package org.intellij.plugins.ceylon.ide.completion;

import com.intellij.psi.PsiFile;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.ide.ceylonCode.completion.ImportHandler;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;

public class CeylonImportHandler extends ImportHandler {
    @Override
    public Tree.CompilationUnit getCompilationUnit(PsiFile file) {
        return ((CeylonFile) file).getCompilationUnit();
    }
}
