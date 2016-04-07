import ceylon.collection {
    ArrayList
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
import com.redhat.ceylon.cmr.api {
    RepositoryManager
}
import com.redhat.ceylon.ide.common.model {
    IdeModuleManager,
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

shared class IdeaModuleManager(
    shared RepositoryManager repositoryManager,
    IdeaCeylonProject ceylonProject
)
        extends IdeModuleManager<IJModule,VirtualFile,VirtualFile,VirtualFile>
        (ceylonProject) {
    
    shared actual IdeaModelLoader newModelLoader(BaseIdeModuleManager self,
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
        value modulePath = moduleName.replace(".", "/") + "/module.ceylon";

        return ModuleRootManager.getInstance(mod)
                .sourceRoots.array.coalesced
                .find((el) => el.findChild(modulePath) exists)
                exists;
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
                    for (pkg in JDKUtils.getJDKPackagesByModule(moduleName)) {
                        // TODO
                        //if (root.getPackageFragment(pkg.string).\iexists()) {
                        //    roots.add(root);
                        //    break;
                        //}
                    }
                } else if (JDKUtils.isOracleJDKModule(moduleName)) {
                    for (pkg in JDKUtils.getOracleJDKPackagesByModule(moduleName)) {
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
