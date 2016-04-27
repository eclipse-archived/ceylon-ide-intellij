package org.intellij.plugins.ceylon.ide.build;

import com.intellij.compiler.server.BuildProcessParametersProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.redhat.ceylon.common.Versions;
import org.intellij.plugins.ceylon.ide.startup.CeylonIdePlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildClasspathProvider extends BuildProcessParametersProvider {

    private Logger logger = Logger.getInstance(BuildClasspathProvider.class);

    private List<String> modulesToLoad = Arrays.asList(
            "org.antlr.runtime",
            "com.redhat.ceylon.cli",
            "net.minidev.json-smart",
            "com.redhat.ceylon.model",
            "com.redhat.ceylon.common",
            "com.redhat.ceylon.typechecker",
            "com.redhat.ceylon.compiler.js",
            "com.redhat.ceylon.compiler.java",
            "com.redhat.ceylon.tool.provider",
            "com.redhat.ceylon.module-resolver",
            "com.redhat.ceylon.module-resolver-javascript"
    );

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
        List<String> modulePaths = new ArrayList<>();

        modulePaths.add(CeylonIdePlugin.getClassesDir().getAbsolutePath());

        for (String name : modulesToLoad) {
            String version;

            switch (name) {
                case "net.minidev.json-smart":
                    version = "1.1.1";
                    break;
                case "org.antlr.runtime":
                    version = "3.4";
                    break;
                default:
                    version = Versions.CEYLON_VERSION_NUMBER;
            }

            String fileName = FileUtil.join(
                    name.replace('.', File.separatorChar),
                    version,
                    name + "-" + version + ".jar"
            );
            File carFile = new File(repo, fileName);

            if (carFile.isFile()) {
                modulePaths.add(carFile.getAbsolutePath());
            } else {
                logger.error("File was not added to build classpath because it was not found: "
                    + carFile.getAbsolutePath());
            }
        }

        return modulePaths;
    }
}
