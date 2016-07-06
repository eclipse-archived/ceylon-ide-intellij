package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import com.intellij.psi.impl.file.PsiBinaryFileImpl;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.CeylonBinaryData;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.classFileDecompilerUtil_;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.ceylonLanguage_;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CeylonClassFileFileViewProvider extends SingleRootFileViewProvider {
    CeylonClassFileFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile virtualFile,
                           boolean eventSystemEnabled) {
        super(manager, virtualFile, eventSystemEnabled, ceylonLanguage_.get_());
    }

    @NotNull
    @Override
    public CharSequence getContents() {
        return ClsFileImpl.decompile(getVirtualFile());
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Project project, @NotNull VirtualFile file,
                                 @NotNull FileType fileType) {
        FileIndexFacade fileIndex = ServiceManager.getService(project, FileIndexFacade.class);
        if (!fileIndex.isInLibraryClasses(file) && fileIndex.isInSource(file)) {
            return new PsiBinaryFileImpl((PsiManagerImpl)getManager(), this);
        }

        CeylonBinaryData data = classFileDecompilerUtil_.get_().getCeylonBinaryData(file);
        if (!data.getInner()) {
            if (data.getCeylonCompiledFile()) {
                return new CeylonClsFile(this);
            }
            return new ClsFileImpl(this);
        }

        return null;
    }
}

class CeylonClsFile extends ClsFileImpl {

    public CeylonClsFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider);
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        if (getPackageName().equals("ceylon.language")) {
            PsiClass[] classes = getClasses();
            if (classes.length == 0) {
                return this;
            }

            String sourceFileName = ((ClsClassImpl)classes[0]).getSourceFileName();
            if (sourceFileName.equals("true_.java") || sourceFileName.equals("false_.java")) {
                sourceFileName = "Boolean.ceylon";
            } else if (sourceFileName.endsWith("_.java")) {
                sourceFileName = sourceFileName.replace("_.java", ".ceylon");
            } else {
                sourceFileName = sourceFileName.replace(".java", ".ceylon");
            }
            String relativePath = "ceylon/language/" + sourceFileName;

            ProjectFileIndex index = ProjectFileIndex.SERVICE.getInstance(getProject());
            for (OrderEntry orderEntry : index.getOrderEntriesForFile(getContainingFile().getVirtualFile())) {
                if (!(orderEntry instanceof LibraryOrSdkOrderEntry)) {
                    continue;
                }
                for (VirtualFile root : orderEntry.getFiles(OrderRootType.SOURCES)) {
                    VirtualFile source = root.findFileByRelativePath(relativePath);
                    if (source != null && source.isValid()) {
                        PsiFile psiSource = getManager().findFile(source);
                        if (psiSource instanceof PsiClassOwner) {
                            return psiSource;
                        }
                    }
                }
            }

        }
        return super.getNavigationElement();
    }
}