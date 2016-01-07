package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiManager;
import com.intellij.psi.compiled.ClassFileDecompilers;
import com.intellij.psi.compiled.ClsStubBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.org.objectweb.asm.ClassReader;
import org.jetbrains.org.objectweb.asm.ClassVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.util.Objects;

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
        return new CeylonFileViewProvider(manager, file, physical);
    }

    @Override
    public boolean accepts(@NotNull VirtualFile file) {
        if (!Objects.equals(file.getExtension(), JavaClassFileType.INSTANCE.getDefaultExtension())) {
            return false;
        }

        return isInnerClass(file);
    }

    static boolean isInnerClass(VirtualFile file) {
        try {
            ClassReader reader = new ClassReader(file.contentsToByteArray());

            final Ref<Boolean> isInnerClass = new Ref<>(Boolean.FALSE);

            String name = file.getNameWithoutExtension();
            int index = name.lastIndexOf('$');
            final String parentName, childName;
            if (index > 0 && index < name.length() - 1) {
                parentName = name.substring(0, index);
                childName = name.substring(index + 1);
            } else {
                return false;
            }

            reader.accept(new ClassVisitor(Opcodes.ASM5) {
                @Override
                public void visitOuterClass(String owner, String name, String desc) {
                    isInnerClass.set(Boolean.TRUE);
                }

                @Override
                public void visitInnerClass(String name, String outer, String inner, int access) {
                    if (childName == null || parentName == null) {
                        return;
                    }
                    if ((inner == null || childName.equals(inner)) && outer != null && parentName.equals(outer.substring(outer.lastIndexOf('/') + 1))) {
                        isInnerClass.set(Boolean.TRUE);
                    }
                }
            }, 0);

            return !isInnerClass.get();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
