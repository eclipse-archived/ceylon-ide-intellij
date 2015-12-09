import ceylon.collection {
    HashSet,
    MutableSet,
    MutableList
}

import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    JavaPsiFacade,
    PsiPackage
}
import com.intellij.psi.search {
    GlobalSearchScope
}
import com.redhat.ceylon.ide.common.model {
    IdeModule
}
import com.redhat.ceylon.ide.common.util {
    toCeylonStringIterable,
    platformUtils,
    Status
}
import com.redhat.ceylon.model.cmr {
    JDKUtils
}
import com.redhat.ceylon.model.loader {
    AbstractModelLoader
}

import java.lang {
    Runnable
}

shared class IdeaModule(
    shared actual IdeaModuleManager moduleManager,
    shared actual IdeaModuleSourceMapper moduleSourceMapper, 
    MutableList<VirtualFile> arrayList)
        extends IdeModule<Module,VirtualFile,VirtualFile,VirtualFile>() {
    
    shared actual Set<String> listPackages() {
        MutableSet<String> packageList = HashSet<String>();
        value name = nameAsString;
        if (JDKUtils.isJDKModule(name)) {
            packageList.addAll(toCeylonStringIterable(
                JDKUtils.getJDKPackagesByModule(name)));
        } else if (JDKUtils.isOracleJDKModule(name)) {
            packageList.addAll(toCeylonStringIterable(
                JDKUtils.getOracleJDKPackagesByModule(name)));
        } else {
            if (!moduleManager.isExternalModuleLoadedFromSource(nameAsString),
                exists mod = ceylonProject) {
                scanPackages(mod.ideArtifact, packageList);
            }
        }
        
        return packageList;
    }
    
    void scanPackages(Module mod, MutableSet<String> packageList) {
        ApplicationManager.application.runReadAction(object satisfies Runnable {
            shared actual void run() {
                if (exists rootPack = 
                    JavaPsiFacade.getInstance(mod.project).findPackage("")) {
                    
                    value scope = mod.getModuleScope(true);
                    value subPacks = rootPack.getSubPackages(scope);
                    
                    for (pack in subPacks.array.coalesced) {
                        packageList.add(pack.qualifiedName);
                        listPackagesInternal(pack, packageList, scope);
                    }
                } else {
                    platformUtils.log(Status._WARNING,
                        "No root package in module " + mod.name);
                }
            }
        });
    }
    
    void listPackagesInternal(PsiPackage pack, MutableSet<String> packageList,
        GlobalSearchScope scope) {
        
        for (child in pack.getSubPackages(scope).array.coalesced) {
            packageList.add(child.qualifiedName);
            listPackagesInternal(child, packageList, scope);
        }
    }

    shared actual AbstractModelLoader modelLoader
            => moduleManager.modelLoader;
    
    shared actual void refreshJavaModel() {
        // TODO
        print("TODO Refresh model");
    }
}
