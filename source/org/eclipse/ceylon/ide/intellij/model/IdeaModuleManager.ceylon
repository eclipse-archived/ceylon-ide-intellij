/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
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
import org.eclipse.ceylon.cmr.api {
    RepositoryManager
}
import org.eclipse.ceylon.ide.common.model {
    IdeModuleManager,
    BaseIdeModuleManager,
    BaseIdeModuleSourceMapper,
    BaseIdeModule,
    BaseCeylonProject,
    CeylonProjects
}
import org.eclipse.ceylon.model.cmr {
    JDKUtils
}
import org.eclipse.ceylon.model.typechecker.model {
    Modules,
    CeylonModule=Module
}

shared class IdeaModuleManager(
    shared RepositoryManager repositoryManager,
    CeylonProjects<IJModule, VirtualFile, VirtualFile, VirtualFile> model,
    IdeaCeylonProject ceylonProject
)
        extends IdeModuleManager<IJModule,VirtualFile,VirtualFile,VirtualFile>
        (model, ceylonProject) {
    
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

        for (root in ModuleRootManager.getInstance(mod).sourceRoots) {
            if (root.findChild(modulePath) exists) {
                return true;
            }
        }
        else {
            return false;
        }
    }
    
    shared actual BaseIdeModule newModule(String moduleName, String version) {
        value mod = ceylonProject.ideArtifact;
        value roots = ArrayList<VirtualFile>();
        
        if (moduleName==CeylonModule.defaultModuleName) {
            roots.addAll { *ModuleRootManager.getInstance(mod).getSourceRoots(true) };
        } else {
            for (root in ModuleRootManager.getInstance(mod).getSourceRoots(true)) {
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
