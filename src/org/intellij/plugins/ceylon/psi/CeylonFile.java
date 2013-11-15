package org.intellij.plugins.ceylon.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.text.BlockSupport;
import com.intellij.util.indexing.IndexingDataKeys;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.CeylonFileType;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.parser.MyTree;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CeylonFile extends PsiFileBase {

    private MyTree myTree;
    private String packageName; // TODO should be updated if the file is moved, package is renamed etc.

    public CeylonFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CeylonLanguage.INSTANCE);
    }

    public MyTree getMyTree() {
        return myTree;
    }

    public void setMyTree(MyTree myTree) {
        if (getOriginalFile() instanceof CeylonFile && getOriginalFile() != this) {
            ((CeylonFile)getOriginalFile()).setMyTree(myTree);
        }
        this.myTree = myTree;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CeylonFileType.INSTANCE;
    }

    public String getPackageName() {
        if (packageName != null) {
            return packageName;
        }

        final Tree.CompilationUnit compilationUnit = getMyTree().getCompilationUnit();

        if (compilationUnit == null) {
            return null;
        }

        final List<Tree.PackageDescriptor> packageDescriptors = compilationUnit.getPackageDescriptors();
        final Tree.PackageDescriptor packageDescriptor = packageDescriptors.isEmpty() ? null : packageDescriptors.get(0);

        if (packageDescriptor != null) {
            packageName = packageDescriptor.getImportPath().getText();
        } else {
            VirtualFile virtualFile = getVirtualFile();
            if (virtualFile == null) {
                virtualFile = getOriginalFile().getVirtualFile();
            }
            if (virtualFile == null) {
                virtualFile = getContainingFile().getUserData(IndexingDataKeys.VIRTUAL_FILE);
            }

            if (virtualFile != null) {
                packageName = ProjectRootManager.getInstance(getProject()).getFileIndex().getPackageNameByDirectory(virtualFile.getParent());
            } else {
                System.out.println("Can't detect VirtualFile for " + getName());
            }
        }

        return packageName;
    }

    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        if (BlockSupport.TREE_DEPTH_LIMIT_EXCEEDED.equals(key)) {
            // This prevents ReparsedSuccessfullyException from ever being thrown so we can bind AST nodes to Ceylon Spec nodes in CeylonIdeaParser.
            return (T) Boolean.TRUE;
        }
        return super.getUserData(key);
    }
}
