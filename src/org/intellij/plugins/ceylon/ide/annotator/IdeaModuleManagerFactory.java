package org.intellij.plugins.ceylon.ide.annotator;

import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModuleManager;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModuleSourceMapper;

class IdeaModuleManagerFactory implements ModuleManagerFactory {
    private final RepositoryManager repositoryManager;
    private final IdeaCeylonProject ceylonProject;

    IdeaModuleManagerFactory(RepositoryManager repositoryManager, IdeaCeylonProject ceylonProject) {
        this.repositoryManager = repositoryManager;
        this.ceylonProject = ceylonProject;
    }

    @Override
    public ModuleManager createModuleManager(final Context context) {
        return new IdeaModuleManager(repositoryManager, ceylonProject);
    }

    @Override
    public ModuleSourceMapper createModuleManagerUtil(Context context, ModuleManager moduleManager) {
        return new IdeaModuleSourceMapper(context, (IdeaModuleManager) moduleManager);
    }
}
