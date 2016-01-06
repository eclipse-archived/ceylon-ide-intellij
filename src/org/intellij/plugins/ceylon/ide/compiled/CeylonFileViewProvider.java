package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.compiled.ClsFileImpl;
import com.intellij.psi.impl.file.PsiBinaryFileImpl;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.asm4.ClassReader;
import org.jetbrains.asm4.ClassVisitor;
import org.jetbrains.asm4.Opcodes;

import java.io.IOException;

public class CeylonFileViewProvider extends SingleRootFileViewProvider {
    public CeylonFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile virtualFile,
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
    protected PsiFile createFile(@NotNull Project project, @NotNull VirtualFile file,
                                 @NotNull FileType fileType) {
        FileIndexFacade fileIndex = ServiceManager.getService(project, FileIndexFacade.class);
        if (!fileIndex.isInLibraryClasses(file) && fileIndex.isInSource(file)) {
            return new PsiBinaryFileImpl((PsiManagerImpl)getManager(), this);
        }

        if (!CeylonDecompiler.isCeylonCompiledFile(file)) {
            return null;
        }

        return new ClsFileImpl(this);
    }

    private boolean isInnerClass(VirtualFile file) {
        try {
            ClassReader reader = new ClassReader(file.contentsToByteArray());
            final Ref<Boolean> isInner = new Ref<>(Boolean.FALSE);

            reader.accept(new ClassVisitor(Opcodes.ASM4) {
                @Override
                public void visitOuterClass(String owner, String name, String desc) {
                    isInner.set(Boolean.TRUE);
                }
            }, 0);

            return isInner.get();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
