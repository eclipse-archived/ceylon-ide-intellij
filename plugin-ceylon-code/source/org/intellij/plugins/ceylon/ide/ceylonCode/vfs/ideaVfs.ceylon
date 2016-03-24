import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaClass
}

import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.util {
    Key
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.redhat.ceylon.ide.common.util {
    synchronize
}
import com.redhat.ceylon.ide.common.vfs {
    FileVirtualFile,
    FolderVirtualFile,
    ResourceVirtualFile
}

import java.util {
    List,
    JArrayList=ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}

shared alias IdeaResource => ResourceVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>;

shared class VirtualFileVirtualFile(VirtualFile file, Module mod)
        satisfies FileVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile> {
    
    shared actual IdeaVirtualFolder parent {
        if (exists p = file.parent) {
            return IdeaVirtualFolder(p, mod);
        }

        throw Exception("File has no parent: ``file.canonicalPath``");
    }
    
    shared actual Boolean equals(Object that) {
        if (is VirtualFileVirtualFile that) {
            return that.file == file;
        }
        return false;
    }
    
    hash => file.hash;
    
    inputStream => file.inputStream;
    
    name => file.name;
    path => file.canonicalPath;
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
    
    path => folder.canonicalPath;
        
    hash => folder.hash;
    
    shared actual Boolean equals(Object that) {
        if (is IdeaVirtualFolder that) {
            return that.folder == folder;
        }
        return false;
    }
    
    ceylonProject => mod.project.getComponent(javaClass<IdeaCeylonProjects>()).getProject(mod);
    
    nativeProject => mod;
}

shared object vfsKeychain {
    
    shared alias VfsKey<T> => Key<T>;
    
    value keys = ArrayList<VfsKey<out Anything>>();

    shared VfsKey<T>? find<T>(Module mod) {
        if (is MyKey<T> key = keys.find(
            (k) => if (is MyKey<T> k) then k.mod == mod else false)) {
            return key;
        }
        
        return null;
    }
    
    shared VfsKey<T> findOrCreate<T>(Module mod) {
        if (is MyKey<T> key = keys.find(
            (k) => if (is MyKey<T> k) then k.mod == mod else false)) {
            return key;
        } else {
            return synchronize(keys, () {
                value key = MyKey<T>("VfsKey", mod);
                keys.add(key);
                return key;
            });
        }
    }
    
    class MyKey<T>(String name, shared Module mod) extends Key<T>(name) {
    }
}