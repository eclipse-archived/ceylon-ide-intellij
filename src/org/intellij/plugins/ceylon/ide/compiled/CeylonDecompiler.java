package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiManager;
import com.intellij.psi.compiled.ClassFileDecompilers;
import com.intellij.psi.compiled.ClsStubBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.org.objectweb.asm.Attribute;
import org.jetbrains.org.objectweb.asm.ClassReader;
import org.jetbrains.org.objectweb.asm.ClassVisitor;
import org.jetbrains.org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.util.Objects;

/**
 * This decompiler forces IntelliJ <=15 to treat classes like CeylonClass$impl as regular classes
 * instead of inner classes (a $ in the name does not imply inner classes).
 */
public class CeylonDecompiler extends ClassFileDecompilers.Full {

    private static final Key<Boolean> IS_INNER_CLASS = Key.create("ceylon.is.inner.class.key");
    private static final Attribute[] EMPTY_ATTRIBUTES = new Attribute[0];

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
        if (ApplicationInfo.getInstance().getBuild().getBaselineVersion() >= 145) {
            // Inner class detection was fixed in IntelliJ 2016.1, we can skip our version
            // See https://youtrack.jetbrains.com/issue/IDEA-132606
            return false;
        }
        if (!Objects.equals(file.getExtension(), JavaClassFileType.INSTANCE.getDefaultExtension())) {
            return false;
        }

        return file.getNameWithoutExtension().contains("$");
    }

    static boolean detectInnerClass(VirtualFile file, @Nullable byte[] content) {
        String name = file.getNameWithoutExtension();
        int p = name.lastIndexOf('$', name.length() - 2);
        if (p <= 0) return false;

        Boolean isInner = IS_INNER_CLASS.get(file);
        if (isInner != null) return isInner;

        if (content == null) {
            try {
                content = file.contentsToByteArray(false);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ClassReader reader = new ClassReader(content);
        final Ref<Boolean> ref = Ref.create(Boolean.FALSE);
        final String className = reader.getClassName();
        reader.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public void visitOuterClass(String owner, String name, String desc) {
                ref.set(Boolean.TRUE);
            }

            @Override
            public void visitInnerClass(String name, String outer, String inner, int access) {
                if (className.equals(name)) {
                    ref.set(Boolean.TRUE);
                }
            }
        }, EMPTY_ATTRIBUTES, ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);

        isInner = ref.get();
        IS_INNER_CLASS.set(file, isInner);
        return isInner;
    }

}
