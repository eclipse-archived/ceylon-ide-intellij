package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.text.BlockSupport;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IndexingDataKeys;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.antlr.runtime.CommonToken;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CeylonFile extends PsiFileBase {

    private Tree.CompilationUnit rootNode;
    private List<CommonToken> tokens;
    private PhasedUnit phasedUnit;

    public CeylonFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CeylonLanguage.INSTANCE);
    }

    void setRootNode(Tree.CompilationUnit rootNode) {
        this.rootNode = rootNode;
    }

    void setTokens(List<CommonToken> tokens) {
        this.tokens = ContainerUtil.immutableList(tokens);
    }

    public Tree.CompilationUnit getCompilationUnit() {
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

    public String getPackageName() {
        final Tree.CompilationUnit compilationUnit = getCompilationUnit();

        if (compilationUnit == null) {
            return null;
        }
        String packageName = null;

        final List<Tree.PackageDescriptor> packageDescriptors = compilationUnit.getPackageDescriptors();

        if (packageDescriptors.size() > 0) {
            packageName = packageDescriptors.get(0).getImportPath().getText();
        } else {
            VirtualFile virtualFile = attemptToGetVirtualFile();
            if (virtualFile != null) {
                packageName = ProjectRootManager.getInstance(getProject()).getFileIndex().getPackageNameByDirectory(virtualFile.getParent());
            } else {
                System.out.println("Can't detect VirtualFile for " + getName());
            }
        }

        return packageName;
    }

    private VirtualFile attemptToGetVirtualFile() {
        return firstNonNull(getVirtualFile(),
                getOriginalFile().getVirtualFile(),
                getContainingFile().getUserData(IndexingDataKeys.VIRTUAL_FILE));
    }

    private VirtualFile firstNonNull(VirtualFile... files) {
        for (VirtualFile file : files) {
            if (file != null) return file;
        }
        return null;
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
}
