import ceylon.collection {
    HashSet,
    MutableSet,
    MutableList
}

import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile,
    VirtualFileManager
}
import com.redhat.ceylon.cmr.api {
    RepositoryManager,
    ArtifactContext
}
import com.redhat.ceylon.ide.common.model {
    IdeModule,
    BaseIdeModule
}
import com.redhat.ceylon.ide.common.util {
    toCeylonStringIterable
}
import com.redhat.ceylon.model.cmr {
    JDKUtils
}
import com.redhat.ceylon.model.loader {
    AbstractModelLoader
}

import java.io {
    File
}

shared class IdeaModule(
    shared actual IdeaModuleManager moduleManager,
    shared actual IdeaModuleSourceMapper moduleSourceMapper, 
    MutableList<VirtualFile> arrayList)
        extends IdeModule<Module,VirtualFile,VirtualFile,VirtualFile>() {

    MutableSet<String> packageList = HashSet<String>();
    
    shared actual Set<String> listPackages() {
        value name = nameAsString;
        if (JDKUtils.isJDKModule(name)) {
            packageList.addAll(toCeylonStringIterable(
                JDKUtils.getJDKPackagesByModule(name)));
        } else if (JDKUtils.isOracleJDKModule(name)) {
            packageList.addAll(toCeylonStringIterable(
                JDKUtils.getOracleJDKPackagesByModule(name)));
        } else if (java) {
            if (!moduleManager.isExternalModuleLoadedFromSource(nameAsString),
                exists mod = ceylonProject,
                packageList.empty) {
                
                scanPackages(mod.ideArtifact, packageList);
            }
        }
        
        return packageList;
    }
    
    // TODO copy paste from CeylonProjectModulesContainer, should be in ide-common
    File? getModuleArtifact(RepositoryManager provider, BaseIdeModule mod) {
        if (!mod.isSourceArchive) {
            File? moduleFile = mod.artifact;
            if (!exists moduleFile) {
                return null;
            }
            
            if (moduleFile.\iexists()) {
                return moduleFile;
            }
        }
        
        variable ArtifactContext ctx = ArtifactContext(mod.nameAsString, mod.version,
            ArtifactContext.\iCAR);
        variable File? moduleArtifact = provider.getArtifact(ctx);
        if (!exists a = moduleArtifact) {
            ctx = ArtifactContext(mod.nameAsString, mod.version,
                ArtifactContext.\iJAR);
            moduleArtifact = provider.getArtifact(ctx);
        }
        
        return moduleArtifact;
    }

    void scanPackages(Module mod, MutableSet<String> packageList) {
        if (exists jarToSearch = returnCarFile()
            else getModuleArtifact(moduleManager.repositoryManager, this)) {
            
            if (exists vfile = VirtualFileManager.instance.findFileByUrl(
                "jar://" + jarToSearch.absolutePath + "!/")) {

                listPackagesInternal(vfile, packageList);
            }
            
            

            //ApplicationManager.application.runReadAction(object satisfies Runnable {
            //    shared actual void run() {
            //        if (exists rootPack = 
            //            JavaPsiFacade.getInstance(mod.project).findPackage("")) {
            //            
            //            //value scope = mod.getModuleScope(true);
            //            value subPacks = rootPack.getSubPackages(scope);
            //            
            //            for (pack in subPacks.array.coalesced) {
            //                packageList.add(pack.qualifiedName);
            //                listPackagesInternal(pack, packageList, scope);
            //            }
            //        } else {
            //            platformUtils.log(Status._WARNING,
            //                "No root package in module " + mod.name);
            //        }
            //    }
            //});
        }
    }
    
    //void listPackagesInternal(PsiPackage pack, MutableSet<String> packageList,
    //    GlobalSearchScope scope) {
    //    
    //    for (child in pack.getSubPackages(scope).array.coalesced) {
    //        packageList.add(child.qualifiedName);
    //        listPackagesInternal(child, packageList, scope);
    //    }
    //}
    void listPackagesInternal(VirtualFile vfile, MutableSet<String> packageList,
        String parentPackage = "") {
        
        vfile.children.array.coalesced
            .filter((_) => _.directory)
            .each((child) {
                String pack =
                        (parentPackage.empty then "" else parentPackage + ".")
                        + child.name;
                packageList.add(pack);
                listPackagesInternal(child, packageList, pack);
            }
        );
    }

    shared actual AbstractModelLoader modelLoader
            => moduleManager.modelLoader;
    
    shared actual void refreshJavaModel() {
        // TODO
        print("TODO Refresh model");
    }
}
