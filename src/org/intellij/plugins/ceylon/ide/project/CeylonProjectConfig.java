package org.intellij.plugins.ceylon.ide.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.ceylon.ide.project.config.AbstractProjectConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CeylonProjectConfig extends AbstractProjectConfig<Project> {

    private static final Map<Project, AbstractProjectConfig> PROJECT_CONFIGS = new HashMap<>();

    public static AbstractProjectConfig get(Project project) {
        AbstractProjectConfig projectConfig = PROJECT_CONFIGS.get(project);
        if (projectConfig == null) {
            projectConfig = new CeylonProjectConfig(project);
            PROJECT_CONFIGS.put(project, projectConfig);
        }
        return projectConfig;
    }

    public static void remove(Project project) {
        PROJECT_CONFIGS.remove(project);
    }

    private CeylonProjectConfig(Project project) {
        super(project);
    }

    @Override
    protected File getProjectLocation() {
        return new File(project.getBaseDir().getPath());
    }

    @Override
    protected File getProjectFolder(String folderName) {
        VirtualFile child = project.getBaseDir().findChild(folderName);

        if (child == null) {
            return new File(project.getBaseDir().getPath());
        }

        return new File(child.getPath());
    }

    @Override
    protected void deleteOldOutputFolder(String oldOutputRepo) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void createNewOutputFolder() {
        throw new UnsupportedOperationException();
    }
}
