import com.intellij.openapi.components {
    ServiceManager
}
import com.intellij.openapi.fileTypes {
    FileType
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.roots {
    FileIndexFacade,
    ProjectFileIndex,
    LibraryOrSdkOrderEntry,
    OrderRootType
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    SingleRootFileViewProvider,
    PsiManager,
    PsiFile,
    FileViewProvider,
    PsiElement,
    PsiClassOwner
}
import com.intellij.psi.impl {
    PsiManagerImpl
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl,
    ClsFileImpl
}
import com.intellij.psi.impl.file {
    PsiBinaryFileImpl
}
import com.redhat.ceylon.model.typechecker.model {
    Module
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonLanguage
}

class CeylonClassFileFileViewProvider(VirtualFile virtualFile, PsiManager manager, Boolean eventSystemEnabled)
        extends SingleRootFileViewProvider(manager, virtualFile, eventSystemEnabled, CeylonLanguage.instance) {

    contents => ClsFileImpl.decompile(virtualFile);

    shared actual PsiFile? createFile(Project project, VirtualFile file, FileType fileType) {
        value fileIndex = ServiceManager.getService(project, `FileIndexFacade`);
        if (!fileIndex.isInLibraryClasses(file) && fileIndex.isInSource(file)) {
            assert (is PsiManagerImpl manager = this.manager);
            return PsiBinaryFileImpl(manager, this);
        }
        else {
            value data = classFileDecompilerUtil.getCeylonBinaryData(file);
            if (!data.inner) {
                return data.ceylonCompiledFile
                    then CeylonClsFile(this)
                    else ClsFileImpl(this);
            }
            else {
                return null;
            }
        }
    }
}

class CeylonClsFile(FileViewProvider viewProvider)
        extends ClsFileImpl(viewProvider) {

    function realSourceFileName(String sourceFileName) {
        if (sourceFileName == "true_.java" || sourceFileName == "false_.java") {
            return "Boolean.ceylon";
        } else if (sourceFileName.endsWith("_.java")) {
            return sourceFileName.replace("_.java", ".ceylon");
        } else {
            return sourceFileName.replace(".java", ".ceylon");
        }
    }

    shared actual PsiElement navigationElement {
        if (packageName == Module.languageModuleName) {
            value classes = this.classes;
            if (classes.size == 0) {
                return this;
            }
            assert (is ClsClassImpl cl = classes[0]);
            String relativePath = "ceylon/language/" + realSourceFileName(cl.sourceFileName);
            value index = ProjectFileIndex.SERVICE.getInstance(project);
            for (orderEntry in index.getOrderEntriesForFile(containingFile.virtualFile)) {
                if (orderEntry is LibraryOrSdkOrderEntry) {
                    for (root in orderEntry.getFiles(OrderRootType.sources)) {
                        if (exists source = root.findFileByRelativePath(relativePath),
                            source.valid,
                            is PsiClassOwner psiSource = manager.findFile(source)) {
                            return psiSource;
                        }
                    }
                }
            }
        }
        return super.navigationElement;
    }
}
