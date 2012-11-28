package org.intellij.plugins.ceylon.psi;

import com.google.common.base.Objects;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.util.indexing.IndexingDataKeys;
import org.intellij.plugins.ceylon.CeylonFileType;
import org.intellij.plugins.ceylon.CeylonLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CeylonFile extends PsiFileBase {

    private String packageName; // TODO should be updated if the file is moved, package is renamed etc.

    public CeylonFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CeylonLanguage.INSTANCE);
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
        CeylonPackageDescriptor packageDescriptor = findChildByClass(CeylonPackageDescriptor.class);

        if (packageDescriptor != null) {
            packageName = packageDescriptor.getPackagePath().getText();
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

    @NotNull
    public CeylonImportDeclaration[] getPotentialImportsForType(CeylonTypeName name) {
        CeylonImportList importList = findChildByClass(CeylonImportList.class);

        if (importList == null) {
            return new CeylonImportDeclaration[0];
        }

        List<CeylonImportDeclaration> imports = importList.getImportDeclarationList();

        for (CeylonImportDeclaration anImport : imports) {
            for (CeylonImportElement importElement : anImport.getImportElementList().getImportElementList()) {
                if (importElement.getText().equals(name.getText())) {
                    return new CeylonImportDeclaration[]{anImport};
                }
            }
        }

        List<CeylonImportDeclaration> wildcardDeclarations = new ArrayList<CeylonImportDeclaration>();

        for (CeylonImportDeclaration anImport : imports) {
            if (!anImport.getImportElementList().getImportWildcardList().isEmpty()) {
                wildcardDeclarations.add(anImport);
            }
        }

        return wildcardDeclarations.toArray(new CeylonImportDeclaration[wildcardDeclarations.size()]);
    }
}
