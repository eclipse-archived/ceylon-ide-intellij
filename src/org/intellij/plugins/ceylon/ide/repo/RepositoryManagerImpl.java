package org.intellij.plugins.ceylon.ide.repo;

import com.intellij.openapi.module.Module;
import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.Repository;
import com.redhat.ceylon.cmr.api.RepositoryManagerBuilder;

import java.io.File;
import java.util.Collection;

public class RepositoryManagerImpl implements RepositoryManager {
    private Module ideaModule;

    public RepositoryManagerImpl(Module module) {
        this.ideaModule = module;
    }

    @Override
    public Collection<ModuleSearchResult.ModuleDetails> findArchivesByName(String moduleName) throws Exception {
        String repoUrl = "https://modules.ceylon-lang.org/test/";
        Repository repository = new RepositoryManagerBuilder(null).repositoryBuilder().buildRepository(repoUrl);

        ModuleQuery query = new ModuleQuery(moduleName, ModuleQuery.Type.JVM);
        ModuleSearchResult result = new ModuleSearchResult();
        repository.searchModules(query, result);

        return result.getResults();
    }

    public File getLocalArchive(String moduleName, String version) throws Exception {
//        String repoUrl = "/Users/bastien/Dev/ceylon/ceylon-0.4/repo/";
        String repoUrl = "/Users/bastien/.ceylon/cache/";

        return new File(repoUrl + moduleName.replace('.', '/') + "/" + version + "/" + moduleName + "-" + version + ".car");
//        com.redhat.ceylon.cmr.api.RepositoryManager manager = new RepositoryManagerBuilder(null).buildRepository();
//
//        return manager.getArtifact(moduleName, version);
    }
}
