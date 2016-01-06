import ceylon.interop.java {
    javaString
}

import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject
}
import com.redhat.ceylon.ide.common.vfs {
    FileVirtualFile,
    FolderVirtualFile,
    ResourceVirtualFile
}

import java.io {
    InputStream,
    ByteArrayInputStream
}
import java.util {
    List,
    ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject
}

shared class PsiFileVirtualFile(PsiFile file, String? forcedPath = null)
        satisfies FileVirtualFile<Module,Object, PsiFile, PsiFile> {
    
    shared actual FolderVirtualFile<Module,Object, PsiFile, PsiFile>? parent => null;

    shared actual Boolean equals(Object that)
            => (super of FileVirtualFile<Module,Object, PsiFile, PsiFile>).equals(that);
    shared actual Integer hash
            => (super of FileVirtualFile<Module,Object, PsiFile, PsiFile>).hash;
    
    shared actual InputStream inputStream {
        return ByteArrayInputStream(javaString(file.text).bytes);
    }

    shared actual String name => file.name;
    shared actual default String path
            => forcedPath else file.virtualFile.canonicalPath;
    shared actual PsiFile nativeResource => file;
    shared actual String charset => file.virtualFile.charset.string;

    shared actual Boolean \iexists() => file.physical;
    
    shared actual CeylonProject<Module,Object,PsiFile,PsiFile> ceylonProject
    => nothing;
}

shared alias IdeaResource => ResourceVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>;

shared class VirtualFileVirtualFile(VirtualFile file, IdeaCeylonProject project)
        satisfies FileVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile> {
    
    shared actual FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>? 
    parent => if (exists parent = file.parent)
              then IdeaVirtualFolder(parent, project)
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
    
    shared actual CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>
    ceylonProject => project;
}

shared class IdeaVirtualFolder(VirtualFile folder, IdeaCeylonProject project)
        satisfies FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile> {
    
    shared actual List<out IdeaResource> children {
        value result = ArrayList<IdeaResource>();
        
        folder.children.array.coalesced.each((child) {
            if (child.directory) {
                result.add(IdeaVirtualFolder(child, project));
            } else {
                
                result.add(VirtualFileVirtualFile(child, project));
            }
        });
        
        return result;
    }
    
    shared actual String name => folder.name;
    
    shared actual VirtualFile nativeResource => folder;
    
    shared actual FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>?
    parent => if (exists parent = folder.parent)
              then IdeaVirtualFolder(parent, project)
              else null;
    
    shared actual String path => folder.canonicalPath;
        
    shared actual Integer hash => folder.hash;
    
    shared actual Boolean equals(Object that) {
        if (is IdeaVirtualFolder that) {
            return that.folder == folder;
        }
        return false;
    }
    
    shared actual CeylonProject<Module,VirtualFile,VirtualFile,VirtualFile>
    ceylonProject => project;
}