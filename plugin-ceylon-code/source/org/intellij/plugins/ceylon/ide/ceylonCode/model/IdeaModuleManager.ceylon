import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    CeylonIterable
}

import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.\imodule {
    IJModule=Module
}
import com.intellij.openapi.roots {
    ModuleRootManager
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.intellij.psi {
    JavaPsiFacade
}
import com.redhat.ceylon.ide.common.model {
    IdeModuleManager,
    IdeModelLoader,
    BaseIdeModuleManager,
    BaseIdeModuleSourceMapper,
    BaseIdeModule,
    BaseCeylonProject
}
import com.redhat.ceylon.model.cmr {
    JDKUtils
}
import com.redhat.ceylon.model.typechecker.model {
    Modules,
    CeylonModule=Module
}

shared class IdeaModuleManager(IdeaCeylonProject ceylonProject)
        extends IdeModuleManager<IJModule,VirtualFile,VirtualFile,VirtualFile>(ceylonProject) {
    
    shared actual IdeModelLoader newModelLoader(BaseIdeModuleManager self,
        BaseIdeModuleSourceMapper sourceMapper, Modules modules) {
        
        assert (is IdeaModuleSourceMapper sourceMapper);
        assert (is IdeaModuleManager self);
        
        return IdeaModelLoader(self, sourceMapper, modules);
    }
    
    shared actual Boolean moduleFileInProject(String moduleName,
        BaseCeylonProject? ceylonProject) {
        
        if (!exists ceylonProject) {
            return false;
        }
        assert (is IdeaCeylonProject ceylonProject);
        value mod = ceylonProject.ideaModule;
        
        value lock = ApplicationManager.application.acquireReadActionLock();
        
        try {
            value facade = JavaPsiFacade.getInstance(mod.project);
            return facade.findPackage(moduleName) exists;
        } finally {
            lock.finish();
        }
    }
    
    shared actual BaseIdeModule newModule(String moduleName, String version) {
        value mod = ceylonProject.ideArtifact;
        value roots = ArrayList<VirtualFile>();
        
        if (moduleName.equals(CeylonModule.\iDEFAULT_MODULE_NAME)) {
            roots.addAll(ModuleRootManager.getInstance(mod)
                    .getSourceRoots(true).array.coalesced);
        } else {
            value sr = ModuleRootManager.getInstance(mod).getSourceRoots(true);
            
            for (root in sr.iterable) {
                if (JDKUtils.isJDKModule(moduleName)) {
                    for (pkg in CeylonIterable(JDKUtils.getJDKPackagesByModule(moduleName))) {
                        // TODO
                        //if (root.getPackageFragment(pkg.string).\iexists()) {
                        //    roots.add(root);
                        //    break;
                        //}
                    }
                } else if (JDKUtils.isOracleJDKModule(moduleName)) {
                    for (pkg in CeylonIterable(JDKUtils.getOracleJDKPackagesByModule(moduleName))) {
                        // TODO
                        //if (root.getPackageFragment(pkg.string).\iexists()) {
                        //    roots.add(root);
                        //    break;
                        //}
                    }
                }
                // TODO else {}
            }
        }
        
        assert (is IdeaModuleSourceMapper msm = moduleSourceMapper);
        return IdeaModule(this, msm, roots);
    }
}
