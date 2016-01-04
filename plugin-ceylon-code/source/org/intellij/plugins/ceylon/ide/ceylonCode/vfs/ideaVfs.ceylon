import com.redhat.ceylon.ide.common.vfs {
    FileVirtualFile,
    FolderVirtualFile,
    BaseFolderVirtualFile,
    ResourceVirtualFile
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
import com.intellij.openapi.vfs {
    VirtualFile,
    VfsUtilCore {
        getRelativePath
    }
}
import java.util {
    List,
    ArrayList
}

shared class PsiFileVirtualFile(PsiFile file, String? forcedPath = null)
        satisfies FileVirtualFile<Object, PsiFile, PsiFile> {
    
    shared actual FolderVirtualFile<Object, PsiFile, PsiFile>? parent => null;

    shared actual Boolean equals(Object that)
            => (super of FileVirtualFile<Object, PsiFile, PsiFile>).equals(that);
    shared actual Integer hash
            => (super of FileVirtualFile<Object, PsiFile, PsiFile>).hash;
    
    shared actual InputStream inputStream {
        return ByteArrayInputStream(javaString(file.text).bytes);
    }

    shared actual String name => file.name;
    shared actual default String path
            => forcedPath else file.virtualFile.canonicalPath;
    shared actual PsiFile nativeResource => file;
    shared actual String charset => file.virtualFile.charset.string;

    shared actual Boolean \iexists() => file.physical;
    
}

shared alias IdeaResource => ResourceVirtualFile<VirtualFile,VirtualFile,VirtualFile>;

shared class VirtualFileVirtualFile(VirtualFile file)
        satisfies FileVirtualFile<VirtualFile, VirtualFile, VirtualFile> {
    
    shared actual FolderVirtualFile<VirtualFile, VirtualFile, VirtualFile>? 
    parent => if (exists parent = file.parent)
              then IdeaVirtualFolder(parent)
              else null;
    
    shared actual Boolean equals(Object that) {
        if (is VirtualFileVirtualFile that) {
            return that.file == file;
        }
        return false;
    }
    shared actual Integer hash => file.hash;
    
    shared actual InputStream inputStream => file.inputStream;
    
    shared actual String name => file.name;
    shared actual default String path => file.canonicalPath;
    shared actual VirtualFile nativeResource => file;
    shared actual String charset => file.charset.string;
    
    shared actual Boolean \iexists() => file.\iexists();
    
}

shared class IdeaVirtualFolder(VirtualFile folder)
        satisfies FolderVirtualFile<VirtualFile,VirtualFile,VirtualFile> {
    
    shared actual List<out IdeaResource> children {
        value result = ArrayList<IdeaResource>();
        
        folder.children.array.coalesced.each((child) {
            if (child.directory) {
                result.add(IdeaVirtualFolder(child));
            } else {
                
                result.add(VirtualFileVirtualFile(child));
            }
        });
        
        return result;
    }
    
    shared actual Boolean \iexists() => folder.\iexists();
    
    shared actual FileVirtualFile<VirtualFile,VirtualFile,VirtualFile>? 
    findFile(String fileName) => if (exists child = folder.findChild(fileName))
                                 then VirtualFileVirtualFile(child)
                                 else null;
    
    shared actual String name => folder.name;
    
    shared actual VirtualFile nativeResource => folder;
    
    shared actual FolderVirtualFile<VirtualFile,VirtualFile,VirtualFile>?
    parent => if (exists parent = folder.parent)
              then IdeaVirtualFolder(parent)
              else null;
    
    shared actual String path => folder.canonicalPath;
    
    shared actual String[] toPackageName(BaseFolderVirtualFile srcDir) {
        assert(is IdeaVirtualFolder srcDir);
        
        if (exists relativePath = getRelativePath(folder, srcDir.folder)) {
            return relativePath.split('/'.equals).sequence();
        }
        return empty;
    }
    
    shared actual Integer hash => folder.hash;
    
    shared actual Boolean equals(Object that) {
        if (is IdeaVirtualFolder that) {
            return that.folder == folder;
        }
        return false;
    }
    
}