import com.redhat.ceylon.ide.common.vfs {
    FileVirtualFile,
    FolderVirtualFile
}
import com.intellij.psi {
    PsiFile
}
import java.io {
    InputStream,
    ByteArrayInputStream
}
import ceylon.interop.java {
    javaString
}

shared class PsiFileVirtualFile(PsiFile file) satisfies FileVirtualFile<Object, Nothing, PsiFile> {
    
    shared actual FolderVirtualFile<Object, Nothing, PsiFile>? parent => null;

    shared actual Boolean equals(Object that)
            => (super of FileVirtualFile<Object, Nothing, PsiFile>).equals(that);
    shared actual Integer hash
            => (super of FileVirtualFile<Object, Nothing, PsiFile>).hash;
    
    shared actual InputStream inputStream {
        return ByteArrayInputStream(javaString(file.text).bytes);
    }

    shared actual String name => file.name;
    shared actual default String path => file.virtualFile.canonicalPath;
    shared actual PsiFile nativeResource => file;
    shared actual String charset => file.virtualFile.charset.string;
}