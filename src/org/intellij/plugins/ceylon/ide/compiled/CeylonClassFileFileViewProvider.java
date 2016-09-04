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
import com.redhat.ceylon.model.typechecker.model.Module;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.CeylonBinaryData;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.classFileDecompilerUtil_;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class CeylonClassFileFileViewProvider extends SingleRootFileViewProvider {

    CeylonClassFileFileViewProvider(@NotNull PsiManager manager,
                                    @NotNull VirtualFile virtualFile,
                                    boolean eventSystemEnabled) {
        super(manager, virtualFile, eventSystemEnabled, CeylonLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public CharSequence getContents() {
        return ClsFileImpl.decompile(getVirtualFile());
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Project project,
                                 @NotNull VirtualFile file,
                                 @NotNull FileType fileType) {

        FileIndexFacade fileIndex
                = ServiceManager.getService(project, FileIndexFacade.class);
        if (!fileIndex.isInLibraryClasses(file) && fileIndex.isInSource(file)) {
            return new PsiBinaryFileImpl((PsiManagerImpl) getManager(), this);
        }

        CeylonBinaryData data = classFileDecompilerUtil_.get_().getCeylonBinaryData(file);
        if (!data.getInner()) {
            return data.getCeylonCompiledFile() ?
                    new CeylonClsFile(this) :
                    new ClsFileImpl(this);
        }

        return null;
    }
}

class CeylonClsFile extends ClsFileImpl {

    CeylonClsFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider);
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        if (getPackageName().equals(Module.LANGUAGE_MODULE_NAME)) {
            PsiClass[] classes = getClasses();
            if (classes.length == 0) {
                return this;
            }

            String relativePath = "ceylon/language/" + getSourceFileName(classes[0]);

            ProjectFileIndex index = ProjectFileIndex.SERVICE.getInstance(getProject());
            VirtualFile virtualFile = getContainingFile().getVirtualFile();
            for (OrderEntry orderEntry : index.getOrderEntriesForFile(virtualFile)) {
                if (orderEntry instanceof LibraryOrSdkOrderEntry) {
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

        }
        return super.getNavigationElement();
    }

    private static String getSourceFileName(PsiClass aClass) {
        ClsClassImpl clsClass = (ClsClassImpl) aClass;
        String sourceFileName = clsClass.getSourceFileName();
        if (sourceFileName.equals("true_.java")
                || sourceFileName.equals("false_.java")) {
            return "Boolean.ceylon";
        } else if (sourceFileName.endsWith("_.java")) {
            return sourceFileName.replace("_.java", ".ceylon");
        } else {
            return sourceFileName.replace(".java", ".ceylon");
        }
    }
}