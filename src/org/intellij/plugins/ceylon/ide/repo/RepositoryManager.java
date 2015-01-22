package org.intellij.plugins.ceylon.ide.repo;

import com.redhat.ceylon.cmr.api.ModuleSearchResult;

import java.io.File;
import java.util.Collection;

public interface RepositoryManager {
    Collection<ModuleSearchResult.ModuleDetails> findArchivesByName(String moduleName) throws Exception;

    File getLocalArchive(String moduleName, String version) throws Exception;
}
