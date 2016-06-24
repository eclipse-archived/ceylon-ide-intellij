import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaClass,
    javaString
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
import com.intellij.psi {
    PsiManager
}
import com.redhat.ceylon.ide.common.util {
    synchronize
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

        throw Exception("File has no parent: ``file.canonicalPath``");
    }
    
    equals(Object that)
            => if (is VirtualFileVirtualFile that)
            then that.file == file
            else false;
    
    hash => file.hash;
    
    inputStream => doWithLock(() => ByteArrayInputStream(javaString(PsiManager.getInstance(mod.project).findViewProvider(file).contents.string).getBytes(file.charset.string)));
    
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
    
    equals(Object that)
            => if (is IdeaVirtualFolder that)
            then that.folder == folder
            else false;
    
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

    alias AnyMyKey => MyKey<in Nothing>;

    shared void clear(Module mod)
            => keys.removeWhere((key)
                => if (is AnyMyKey key) then key.mod == mod else false);
}