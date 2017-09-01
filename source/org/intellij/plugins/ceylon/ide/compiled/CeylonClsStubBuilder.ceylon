import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi.compiled {
    ClsStubBuilder
}
import com.intellij.psi.impl.compiled {
    InnerClassSourceStrategy,
    OutOfOrderInnerClassException,
    StubBuildingVisitor
}
import com.intellij.psi.impl.java.stubs {
    PsiJavaFileStub
}
import com.intellij.psi.impl.java.stubs.impl {
    PsiJavaFileStubImpl
}
import com.intellij.util.cls {
    ClsFormatException
}
import com.intellij.util.indexing {
    FileContent
}

import java.io {
    IOException
}

import org.intellij.plugins.ceylon.ide.compiled {
    classFileDecompilerUtil
}
import org.jetbrains.org.objectweb.asm {
    ClassReader
}

class CeylonClsStubBuilder() extends ClsStubBuilder() {

    stubVersion => 1;

    function getPackageName(String internalName) {
        Integer p = internalName.lastIndexOf("/");
        return p>0 then internalName.substring(0, p).replace("/", ".") else "";
    }

    shared actual PsiJavaFileStub? buildFileStub(FileContent fileContent) {
        value file = fileContent.file;
        value data = classFileDecompilerUtil.getCeylonBinaryData(file);
        if (data.inner) {
            return null;
        }
        try {
            value reader = ClassReader(fileContent.content);
            value className = file.nameWithoutExtension;
            value packageName = getPackageName(reader.className);
            value stub = PsiJavaFileStubImpl(packageName, true);
            try {
                value visitor = StubBuildingVisitor(file, strategy, stub, 0, className);
                reader.accept(visitor, ClassReader.skipFrames);
                if (!visitor.result exists) {
                    return null;
                }
            }
            catch (OutOfOrderInnerClassException e) {
                return null;
            }
            return stub;
        }
        catch (e) {
            throw ClsFormatException(file.path + ": " + e.message, e);
        }
    }

    object strategy satisfies InnerClassSourceStrategy<VirtualFile> {

        shared actual VirtualFile? findInnerClass(String innerName, VirtualFile outerClass) {
            value baseName = outerClass.nameWithoutExtension;
            return outerClass.parent.findChild("``baseName``$``innerName``.class");
        }

        shared actual void accept(VirtualFile innerClass, StubBuildingVisitor<VirtualFile> visitor) {
            try {
                ClassReader(innerClass.contentsToByteArray(false))
                    .accept(visitor, ClassReader.skipFrames);
            }
            catch (IOException ignored) {}
        }

    }
}
