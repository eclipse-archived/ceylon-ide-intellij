package org.intellij.plugins.ceylon.ide.compiled;

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.compiled.ClsStubBuilder;
import com.intellij.psi.impl.compiled.InnerClassSourceStrategy;
import com.intellij.psi.impl.compiled.OutOfOrderInnerClassException;
import com.intellij.psi.impl.compiled.StubBuildingVisitor;
import com.intellij.psi.impl.java.stubs.PsiClassStub;
import com.intellij.psi.impl.java.stubs.PsiJavaFileStub;
import com.intellij.psi.impl.java.stubs.impl.PsiJavaFileStubImpl;
import com.intellij.util.cls.ClsFormatException;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.org.objectweb.asm.ClassReader;

import java.io.IOException;

import static org.intellij.plugins.ceylon.ide.compiled.CeylonDecompiler.isCeylonCompiledFile;

class CeylonClsStubBuilder extends ClsStubBuilder {
    private static final int STUB_VERSION = 1;

    @Override
    public int getStubVersion() {
        return STUB_VERSION;
    }

    @Nullable
    @Override
    public PsiJavaFileStub buildFileStub(@NotNull FileContent fileContent)
            throws ClsFormatException {

        byte[] bytes = fileContent.getContent();
        VirtualFile file = fileContent.getFile();

        if (fileContent.getFileName().indexOf('$') >= 0 && !isCeylonCompiledFile(file)) {
            return null;
        }

        try {
            ClassReader reader = new ClassReader(bytes);
            String internalName = reader.getClassName();
            String className = file.getNameWithoutExtension();
            String fqn = StubBuildingVisitor.getFqn(internalName, className, null);
            String packageName = getPackageName(fqn, className);
            PsiJavaFileStubImpl stub = new PsiJavaFileStubImpl(packageName, true);

            try {
                StubBuildingVisitor<VirtualFile> visitor = new StubBuildingVisitor<VirtualFile>(file, STRATEGY, stub, 0, className);
                reader.accept(visitor, ClassReader.SKIP_FRAMES);
                PsiClassStub<?> result = visitor.getResult();
                if (result == null) return null;
            } catch (OutOfOrderInnerClassException e) {
                return null;
            }

            return stub;
        } catch (Exception e) {
            throw new ClsFormatException(file.getPath() + ": " + e.getMessage(), e);
        }
    }

    private static String getPackageName(String fqn, String shortName) {
        return fqn == null || Comparing.equal(shortName, fqn) ? "" : fqn.substring(0, fqn.lastIndexOf('.'));
    }

    private static final InnerClassSourceStrategy<VirtualFile> STRATEGY = new InnerClassSourceStrategy<VirtualFile>() {
        @Nullable
        @Override
        public VirtualFile findInnerClass(String innerName, VirtualFile outerClass) {
            String baseName = outerClass.getNameWithoutExtension();
            VirtualFile dir = outerClass.getParent();
            assert dir != null : outerClass;
            return dir.findChild(baseName + "$" + innerName + ".class");
        }

        @Override
        public void accept(VirtualFile innerClass, StubBuildingVisitor<VirtualFile> visitor) {
            try {
                byte[] bytes = innerClass.contentsToByteArray(false);
                new ClassReader(bytes).accept(visitor, ClassReader.SKIP_FRAMES);
            } catch (IOException ignored) {
            }
        }
    };
}
