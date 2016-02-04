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
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    CeylonProjects
}
import com.redhat.ceylon.ide.common.util {
    synchronize
}
import com.redhat.ceylon.ide.common.vfs {
    FileVirtualFile,
    FolderVirtualFile,
    ResourceVirtualFile
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

import java.io {
    InputStream
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
    
    shared actual FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile> 
    parent => if (exists parent = file.parent)
              then IdeaVirtualFolder(parent, mod)
              else nothing;
    
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
    ceylonProject => mod.project.getComponent(javaClass<IdeaCeylonProjects>()).getProject(mod) else nothing;
    
    shared actual Module nativeProject => mod;
    
    shared actual CeylonProjects<Module,VirtualFile,VirtualFile,VirtualFile>.VirtualFileSystem
    vfs => mod.project.getComponent(javaClass<IdeaCeylonProjects>()).vfs;
    
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
    
    shared actual String name => folder.name;
    
    shared actual VirtualFile nativeResource => folder;
    
    shared actual FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>?
    parent => if (exists parent = folder.parent)
              then IdeaVirtualFolder(parent, mod)
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
    ceylonProject => mod.project.getComponent(javaClass<IdeaCeylonProjects>()).getProject(mod) else nothing;
    
    shared actual Package? ceylonPackage
            => folder.getUserData(vfsKeychain.findOrCreate<Package>(mod));
    
    shared actual Boolean isSource
            => folder.getUserData(vfsKeychain.findOrCreate<Boolean>(mod));
    
    shared actual FolderVirtualFile<Module,VirtualFile,VirtualFile,VirtualFile>? rootFolder
            => folder.getUserData(vfsKeychain.findOrCreate<IdeaVirtualFolder>(mod));
    
    shared actual Module nativeProject => mod;
    
    shared actual CeylonProjects<Module,VirtualFile,VirtualFile,VirtualFile>.VirtualFileSystem
    vfs => mod.project.getComponent(javaClass<IdeaCeylonProjects>()).vfs;
    
    
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