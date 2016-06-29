import ceylon.interop.java {
    javaClass,
    javaString
}

import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    PsiManager
}
import com.redhat.ceylon.ide.common.vfs {
    FileVirtualFile,
    FolderVirtualFile,
    ResourceVirtualFile
}

import java.io {
    ByteArrayInputStream
}
import java.util {
    List,
    JArrayList=ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects,
    doWithLock
}

shared alias IdeaResource => ResourceVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>;

shared class VirtualFileVirtualFile(VirtualFile file, Module mod)
        satisfies FileVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile> {
    
    shared actual IdeaVirtualFolder parent {
        if (exists p = file.parent) {
            return IdeaVirtualFolder(p, mod);
        }

        throw Exception("File has no parent: ``file.path``");
    }
    
    equals(Object that)
            => if (is VirtualFileVirtualFile that)
            then that.file == file
            else false;
    
    hash => file.hash;
    
    inputStream => doWithLock(() => 
        let (contents = PsiManager.getInstance(mod.project).findViewProvider(file)?.contents?.string else "")
        ByteArrayInputStream(javaString(contents).getBytes(file.charset.string)));
    
    name => file.name;
    path => file.canonicalPath else file.path;
    nativeResource => file;
    charset => file.charset.string;
    
    ceylonProject => mod.project.getComponent(javaClass<IdeaCeylonProjects>()).getProject(mod);
    
    nativeProject => mod;
}

shared class IdeaVirtualFolder(VirtualFile folder, Module mod)
        satisfies FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile> {
    
    shared actual List<out IdeaResource> children {
        value result = JArrayList<IdeaResource>();
        
        folder.children.array.coalesced.each((child) {
            if (child.directory) {
                result.add(IdeaVirtualFolder(child, mod));
            } else {
                
                result.add(VirtualFileVirtualFile(child, mod));
            }
        });
        
        return result;
    }
    
    name => folder.name;
    
    nativeResource => folder;
    
    parent => if (exists parent = folder.parent)
              then IdeaVirtualFolder(parent, mod)
              else null;
    
    path => folder.canonicalPath else folder.path;
        
    hash => folder.hash;
    
    equals(Object that)
            => if (is IdeaVirtualFolder that)
            then that.folder == folder
            else false;
    
    ceylonProject => mod.project.getComponent(javaClass<IdeaCeylonProjects>()).getProject(mod);
    
    nativeProject => mod;
}
