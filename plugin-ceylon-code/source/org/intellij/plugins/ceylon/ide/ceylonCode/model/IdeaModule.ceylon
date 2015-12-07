import ceylon.collection {
    HashSet,
    MutableSet
}

import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.redhat.ceylon.ide.common.model {
    IdeModule
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

shared class IdeaModule(
    shared actual IdeaModuleManager moduleManager,
    shared actual IdeaModuleSourceMapper moduleSourceMapper)
        extends IdeModule<Module,VirtualFile,VirtualFile,VirtualFile>() {
    
    shared actual Set<String> listPackages() {
        MutableSet<String> packageList = HashSet<String>();
        value name = nameAsString;
        if (JDKUtils.isJDKModule(name)) {
            packageList.addAll(toCeylonStringIterable(JDKUtils.getJDKPackagesByModule(name)));
        } else if (JDKUtils.isOracleJDKModule(name)) {
            packageList.addAll(toCeylonStringIterable(JDKUtils.getOracleJDKPackagesByModule(name)));
        }
        // TODO finish this
        
        return packageList;
    }
    
    shared actual AbstractModelLoader modelLoader
            => moduleManager.modelLoader;
    
    shared actual void refreshJavaModel() {
        // TODO
        print("TODO Refresh model");
    }
}
