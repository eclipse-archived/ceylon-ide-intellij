package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.text.BlockSupport;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.antlr.runtime.CommonToken;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerInvoker;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CeylonFile extends PsiFileBase {

    private Tree.CompilationUnit rootNode;
    private List<CommonToken> tokens;
    private PhasedUnit phasedUnit;
    private boolean typechecked = false;
    
    public CeylonFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CeylonLanguage.INSTANCE);
    }

    void setRootNode(Tree.CompilationUnit rootNode) {
        typechecked = false;
        this.rootNode = rootNode;
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
        synchronized (this) {
            if (!typechecked) {
                ITypeCheckerInvoker invoker = Extensions.getExtensions(ITypeCheckerInvoker.EP_NAME)[0];

                if (invoker.typecheck(this) != null) {
                    typechecked = true;
                    return phasedUnit;
                }
            }

            return phasedUnit;
        }
    }
}
