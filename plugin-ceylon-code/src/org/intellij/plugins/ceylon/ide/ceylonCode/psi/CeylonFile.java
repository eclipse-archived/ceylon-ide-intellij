package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.antlr.runtime.CommonToken;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NotNull;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.util.Key;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.text.BlockSupport;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Cancellable;

public class CeylonFile extends PsiFileBase {

    private Tree.CompilationUnit rootNode;
    private List<CommonToken> tokens;
    private PhasedUnit phasedUnit;
    private boolean typechecked = false;
    private Lock localTypecheckingLock = new ReentrantLock();

    public CeylonFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CeylonLanguage.INSTANCE);
    }

    void setRootNode(Tree.CompilationUnit rootNode) {
        try {
            if(! localTypecheckingLock.tryLock(1, TimeUnit.SECONDS)) {
                throw new ProcessCanceledException();
            }
        } catch (InterruptedException e) {
            throw new ProcessCanceledException(e);
        }
        try {
            typechecked = false;
            this.rootNode = rootNode;
        }
        finally {
            localTypecheckingLock.unlock();
        }
    }

    void setTokens(List<CommonToken> tokens) {
        this.tokens = ContainerUtil.immutableList(tokens);
    }

    private CeylonPsi.CompilationUnitPsi getCompilationUnitPsi() {
        return PsiTreeUtil.findChildOfType(this, CeylonPsi.CompilationUnitPsi.class);
    }

    public Tree.CompilationUnit getCompilationUnit() {
        if (rootNode == null) {
            CeylonPsi.CompilationUnitPsi cu = getCompilationUnitPsi();
            if (cu != null) {
                return cu.getCeylonNode();
            }
        }
        return rootNode;
    }

    public List<CommonToken> getTokens() {
        return tokens;
    }

    public PhasedUnit getPhasedUnit() {
        return phasedUnit;
    }

    public void setPhasedUnit(PhasedUnit phasedUnit) {
        this.phasedUnit = phasedUnit;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CeylonFileType.INSTANCE;
    }

    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        if (BlockSupport.TREE_DEPTH_LIMIT_EXCEEDED.equals(key)) {
            // This prevents ReparsedSuccessfullyException from ever being thrown so we can bind AST nodes to Ceylon Spec nodes in CeylonIdeaParser.
            //noinspection unchecked
            return (T) Boolean.TRUE;
        }
        return super.getUserData(key);
    }

    public PhasedUnit ensureTypechecked() {
        return ensureTypechecked(null);
    }
    
    public PhasedUnit ensureTypechecked(Cancellable cancellable) {
        try {
            if(! localTypecheckingLock.tryLock(1, TimeUnit.SECONDS)) {
                throw new ProcessCanceledException();
            }
        } catch (InterruptedException e) {
            throw new ProcessCanceledException(e);
        }
        try {
            if (!typechecked) {
                if (doLocalTypecheck_.doLocalTypecheck(this, cancellable) != null) {
                    typechecked = true;
                }
            }
        }
        finally {
            localTypecheckingLock.unlock();
        }
        
        return phasedUnit;
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
        setRootNode(null);
    }
}
