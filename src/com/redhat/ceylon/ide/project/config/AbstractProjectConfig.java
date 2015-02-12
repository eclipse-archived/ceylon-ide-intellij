package com.redhat.ceylon.ide.project.config;

import com.redhat.ceylon.common.config.*;
import com.redhat.ceylon.common.config.Repositories.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProjectConfig<P> {

    public static final String PROJECT_COMPILE_TO_JVM = "project.compile-to-jvm";
    public static final String PROJECT_COMPILE_TO_JS = "project.compile-to-js";
    public static final String PROJECT_SHOW_COMPILER_WARNINGS = "project.show-compiler-warnings";
    public static final String PROJECT_ENABLE_JAVA_CALLING_CEYLON = "project.enable-java-calling-ceylon";
    public static final String PROJECT_OUTPUT_DIRECTORY = "project.output-directory";
    public static final String PROJECT_SYSTEM_REPO = "project.system-repo";

    protected final P project;

    private CeylonConfig mergedConfig;
    private CeylonConfig projectConfig;
    private Repositories mergedRepositories;
    private Repositories projectRepositories;

    private String transientOutputRepo;
    private List<String> transientProjectLocalRepos;
    private List<String> transientProjectRemoteRepos;

    private boolean isOfflineChanged = false;
    private boolean isEncodingChanged = false;
    private Boolean transientOffline;
    private String transientEncoding;

    protected AbstractProjectConfig(P project) {
        this.project = project;
        initMergedConfig();
        initProjectConfig();
    }

    protected abstract File getProjectLocation();

    protected abstract File getProjectFolder(String folderName);

    private void initMergedConfig() {
        mergedConfig = CeylonConfig.createFromLocalDir(getProjectLocation());
        mergedRepositories = Repositories.withConfig(mergedConfig);
    }

    private void initProjectConfig() {
        projectConfig = new CeylonConfig();
        File projectConfigFile = getProjectConfigFile();
        if (projectConfigFile.exists() && projectConfigFile.isFile()) {
            try {
                projectConfig = CeylonConfigFinder.loadConfigFromFile(projectConfigFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        projectRepositories = Repositories.withConfig(projectConfig);
    }

    public Repositories getMergedRepositories() {
        return mergedRepositories;
    }

    public Repositories getProjectRepositories() {
        return projectRepositories;
    }

    public String getOutputRepo() {
        Repository outputRepo = mergedRepositories.getOutputRepository();
        return outputRepo.getUrl();
    }

    public void setOutputRepo(String outputRepo) {
        transientOutputRepo = outputRepo;
    }

//    public IPath getOutputRepoPath() {
//        Repository outputRepo = mergedRepositories.getOutputRepository();
//        String outputRepoUrl = outputRepo.getUrl();
//
//        IPath outputRepoPath;
//        if (outputRepoUrl.startsWith("./") || outputRepoUrl.startsWith(".\\")) {
//            outputRepoPath = getProjectLocation().getAbsolutePath().append(outputRepoUrl.substring(2));
//        } else {
//            outputRepoPath = project.getFullPath().append(outputRepoUrl);
//        }
//
//        return outputRepoPath;
//    }

    public List<String> getGlobalLookupRepos() {
        return toRepositoriesUrlList(mergedRepositories.getGlobalLookupRepositories());
    }

    public List<String> getOtherRemoteRepos() {
        return toRepositoriesUrlList(mergedRepositories.getOtherLookupRepositories());
    }

    public List<String> getProjectLocalRepos() {
        return toRepositoriesUrlList(projectRepositories.getRepositoriesByType(Repositories.REPO_TYPE_LOCAL_LOOKUP));
    }

    public void setProjectLocalRepos(List<String> projectLocalRepos) {
        transientProjectLocalRepos = projectLocalRepos;
    }

    public List<String> getProjectRemoteRepos() {
        return toRepositoriesUrlList(projectRepositories.getRepositoriesByType(Repositories.REPO_TYPE_REMOTE_LOOKUP));
    }

    public void setProjectRemoteRepos(List<String> projectRemoteRepos) {
        transientProjectRemoteRepos = projectRemoteRepos;
    }

    public String getEncoding() {
        return mergedConfig.getOption(DefaultToolOptions.DEFAULTS_ENCODING);
    }

    public String getProjectEncoding() {
        return projectConfig.getOption(DefaultToolOptions.DEFAULTS_ENCODING);
    }

    public void setProjectEncoding(String encoding) {
        this.isEncodingChanged = true;
        this.transientEncoding = encoding;
    }

    public boolean isOffline() {
        return mergedConfig.getBoolOption(DefaultToolOptions.DEFAULTS_OFFLINE, false);
    }

    public Boolean isProjectOffline() {
        return projectConfig.getBoolOption(DefaultToolOptions.DEFAULTS_OFFLINE);
    }

    public void setProjectOffline(Boolean offline) {
        this.isOfflineChanged = true;
        this.transientOffline = offline;
    }

    public void refresh() {

        initMergedConfig();
        initProjectConfig();
        isOfflineChanged = false;
        isEncodingChanged = false;
        transientEncoding = null;
        transientOffline = null;
        transientOutputRepo = null;
        transientProjectLocalRepos = null;
        transientProjectRemoteRepos = null;
    }

    public void save() {
        initProjectConfig();

        String oldOutputRepo = getOutputRepo();
        List<String> oldProjectLocalRepos = getProjectLocalRepos();
        List<String> oldProjectRemoteRepos = getProjectRemoteRepos();

        boolean isOutputRepoChanged = transientOutputRepo != null && !transientOutputRepo.equals(oldOutputRepo);
        boolean isProjectLocalReposChanged = transientProjectLocalRepos != null && !transientProjectLocalRepos.equals(oldProjectLocalRepos);
        boolean isProjectRemoteReposChanged = transientProjectRemoteRepos != null && !transientProjectRemoteRepos.equals(oldProjectRemoteRepos);

        if (isOutputRepoChanged) {
            deleteOldOutputFolder(oldOutputRepo);
            createNewOutputFolder();
        } else if (transientOutputRepo != null) {
            // fix #422: output folder must be create for new projects
            File newOutputRepoFolder = getProjectFolder(removeCurrentDirPrefix(transientOutputRepo));
            if (!newOutputRepoFolder.exists()) {
                createNewOutputFolder();
            }
        }

        if (isOutputRepoChanged || isProjectLocalReposChanged || isProjectRemoteReposChanged || isOfflineChanged || isEncodingChanged) {
            try {
                if (isOutputRepoChanged) {
                    Repository newOutputRepo = new Repositories.SimpleRepository("", transientOutputRepo, null);
                    projectRepositories.setRepositoriesByType(Repositories.REPO_TYPE_OUTPUT, new Repository[]{newOutputRepo});
                }
                if (isProjectLocalReposChanged) {
                    Repository[] newLocalRepos = toRepositoriesArray(transientProjectLocalRepos);
                    projectRepositories.setRepositoriesByType(Repositories.REPO_TYPE_LOCAL_LOOKUP, newLocalRepos);
                }
                if (isProjectRemoteReposChanged) {
                    Repository[] newRemoteRepos = toRepositoriesArray(transientProjectRemoteRepos);
                    projectRepositories.setRepositoriesByType(Repositories.REPO_TYPE_REMOTE_LOOKUP, newRemoteRepos);
                }
                if (isOfflineChanged) {
                    if (transientOffline != null) {
                        projectConfig.setBoolOption(DefaultToolOptions.DEFAULTS_OFFLINE, transientOffline);
                    } else {
                        projectConfig.removeOption(DefaultToolOptions.DEFAULTS_OFFLINE);
                    }
                }
                if (isEncodingChanged) {
                    if (transientEncoding != null) {
                        projectConfig.setOption(DefaultToolOptions.DEFAULTS_ENCODING, transientEncoding);
                    } else {
                        projectConfig.removeOption(DefaultToolOptions.DEFAULTS_ENCODING);
                    }
                }

                ConfigWriter.write(projectConfig, getProjectConfigFile());
                refresh();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private File getProjectConfigFile() {
        File projectCeylonDir = new File(getProjectLocation(), ".ceylon");
        File projectCeylonConfigFile = new File(projectCeylonDir, "config");
        return projectCeylonConfigFile;
    }

    private List<String> toRepositoriesUrlList(Repository[] repositories) {
        List<String> result = new ArrayList<String>();
        if (repositories != null) {
            for (Repository repository : repositories) {
                result.add(repository.getUrl());
            }
        }
        return result;
    }

    private Repository[] toRepositoriesArray(List<String> repositoriesUrl) {
        Repository[] repositories = new Repository[repositoriesUrl.size()];
        for (int i = 0; i < repositoriesUrl.size(); i++) {
            repositories[i] = new Repositories.SimpleRepository("", repositoriesUrl.get(i), null);
        }
        return repositories;
    }

    protected abstract void deleteOldOutputFolder(String oldOutputRepo);

    protected abstract void createNewOutputFolder();

    private String removeCurrentDirPrefix(String url) {
        return url.startsWith("./") || url.startsWith(".\\") ? url.substring(2) : url;
    }

}