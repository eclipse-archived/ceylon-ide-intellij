package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiManager;
import com.intellij.psi.compiled.ClassFileDecompilers;
import com.intellij.psi.compiled.ClsStubBuilder;
import org.intellij.plugins.ceylon.ide.ceylonCode.compiled.classFileDecompilerUtil_;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This decompiler tries to determine if a class was compiled by the Ceylon compiler.
 * It will also ignore "internal" classes ($impl, anonymous classes etc).
 */
public class CeylonDecompiler extends ClassFileDecompilers.Full {

    private CeylonClsStubBuilder stubBuilder = new CeylonClsStubBuilder();

    @NotNull
    @Override
    public ClsStubBuilder getStubBuilder() {
        return stubBuilder;
    }

    @NotNull
    @Override
    public FileViewProvider createFileViewProvider(@NotNull VirtualFile file,
                                                   @NotNull PsiManager manager, boolean physical) {
        return new CeylonClassFileFileViewProvider(manager, file, physical);
    }

    @Override
    public boolean accepts(@NotNull VirtualFile file) {
        if (!Objects.equals(file.getExtension(), JavaClassFileType.INSTANCE.getDefaultExtension())) {
            return false;
        }

        return classFileDecompilerUtil_.get_().isCeylonCompiledFile(file);
    }
}
