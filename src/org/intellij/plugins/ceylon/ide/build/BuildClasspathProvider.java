package org.intellij.plugins.ceylon.ide.build;

import com.intellij.compiler.server.BuildProcessParametersProvider;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.CommonProcessors;
import org.intellij.plugins.ceylon.ide.startup.CeylonIdePlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BuildClasspathProvider extends BuildProcessParametersProvider {

    private List<String> modulesPaths = null;

    @NotNull
    @Override
    public List<String> getClassPath() {
        if (modulesPaths == null) {
            modulesPaths = getModulePaths();
        }
        return modulesPaths;
    }

    private List<String> getModulePaths() {
        File repo = CeylonIdePlugin.getEmbeddedCeylonRepository();

        final List<String> modulePaths = new ArrayList<>();
        modulePaths.add(CeylonIdePlugin.getClassesDir().getAbsolutePath());

        FileUtil.visitFiles(repo, new CommonProcessors.FindProcessor<File>() {
            @Override
            protected boolean accept(File file) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    modulePaths.add(file.getAbsolutePath());
                }
                return false;
            }
        });

        return modulePaths;
    }
}
