package org.intellij.plugins.ceylon.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.util.indexing.IndexingDataKeys;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.CeylonFileType;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.intellij.plugins.ceylon.parser.CeylonIdeaParser;
import org.intellij.plugins.ceylon.parser.MyTree;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CeylonFile extends PsiFileBase {

    private final MyTree myTree = new MyTree();
    private String packageName; // TODO should be updated if the file is moved, package is renamed etc.

    public CeylonFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CeylonLanguage.INSTANCE);
    }

    public MyTree getMyTree() {
        return myTree;
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

        Node userData = getNode().getUserData(CeylonIdeaParser.CEYLON_NODE_KEY);

        if (!(userData instanceof Tree.CompilationUnit)) {
            return null;// TODO is always null :(
        }

        final List<Tree.PackageDescriptor> packageDescriptors = ((Tree.CompilationUnit) userData).getPackageDescriptors();
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
}
