package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NotNull;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.text.BlockSupport;
import com.intellij.psi.util.PsiTreeUtil;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.platform.Status;
import com.redhat.ceylon.ide.common.platform.platformUtils_;
import com.redhat.ceylon.ide.common.typechecker.LocalAnalysisResult;
import com.redhat.ceylon.model.typechecker.model.Cancellable;

public class CeylonFile extends PsiFileBase {

    public CeylonFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CeylonLanguage.INSTANCE);
    }

    private CeylonPsi.CompilationUnitPsi getCompilationUnitPsi() {
        return PsiTreeUtil.findChildOfType(this, CeylonPsi.CompilationUnitPsi.class);
    }

    public Tree.CompilationUnit getCompilationUnit() {
        CeylonPsi.CompilationUnitPsi cu = getCompilationUnitPsi();
        if (cu != null) {
            return cu.getCeylonNode();
        }
        return null;
    }

    CeylonLocalAnalyzer getLocalAnalyzer() {
        FileViewProvider fileViewProvider;
        if (getOriginalFile() != null
                && getOriginalFile() instanceof CeylonFile) {
            fileViewProvider = getOriginalFile().getViewProvider();
        } else {
            fileViewProvider = getViewProvider();
        }
        
        if (fileViewProvider instanceof CeylonSourceFileViewProvider) {
            return ((CeylonSourceFileViewProvider) fileViewProvider)
                    .getCeylonLocalAnalyzer();
        }
        
        return null;
    }
    
    public LocalAnalysisResult getLocalAnalysisResult() {
        CeylonLocalAnalyzer localAnalyzer = getLocalAnalyzer();
        if (localAnalyzer != null) {
            return localAnalyzer.getResult();
        }
        platformUtils_.get_().log(Status.getStatus$_WARNING(), "localAnalysisResult requested, but was null");
        return null;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CeylonFileType.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        if (BlockSupport.TREE_DEPTH_LIMIT_EXCEEDED.equals(key)) {
            // This prevents ReparsedSuccessfullyException from ever being thrown so we can bind AST nodes to Ceylon Spec nodes in CeylonIdeaParser.
            //noinspection unchecked
            return (T) Boolean.TRUE;
        }
        return super.getUserData(key);
    }

    public PhasedUnit getUpToDatePhasedUnit() {
        LocalAnalysisResult analysisResult = getLocalAnalysisResult();
        if (analysisResult != null) {
            if (analysisResult.getTypecheckedRootNode() != null) {
                return analysisResult.getLastPhasedUnit();
            }
        }
        return null;
    }
    
    private PhasedUnit ensureTypechecked(Cancellable cancellable, int timeoutSeconds) {
        CeylonLocalAnalyzer localAnalyzer = getLocalAnalyzer();
        if (localAnalyzer != null) {
            return localAnalyzer.ensureTypechecked(cancellable, timeoutSeconds);
        }
        return null;
    }

    public PhasedUnit ensureTypechecked() {
        return ensureTypechecked(null);
    }
    
    public PhasedUnit ensureTypechecked(Cancellable cancellable) {
        return ensureTypechecked(cancellable, 5);
    }

    public PhasedUnit forceReparse() {
        putUserData(IdeaCeylonParser.FORCED_CU_KEY, null);
        onContentReload();
        getNode().getLastChildNode();
        return ensureTypechecked();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
    }
}
