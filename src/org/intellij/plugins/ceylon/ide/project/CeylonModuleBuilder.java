package org.intellij.plugins.ceylon.ide.project;

import ceylon.interop.java.CeylonStringIterable;
import com.intellij.facet.FacetManager;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.CeylonSettings;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.ceylonSettings_;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.java.JavaResourceRootType;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

public class CeylonModuleBuilder extends JavaModuleBuilder {

    private PageOne pageOne;
    private PageTwo pageTwo;
    private CeylonSettings ceylonSettings = ceylonSettings_.get_();

    private List<Pair<String, String>> mySourcePaths;

    public CeylonModuleBuilder() {
        addListener(new ModuleBuilderListener() {
            @Override
            public void moduleCreated(@NotNull Module module) {
                persistConfiguration(module);
                ((TypeCheckerProvider) module.getComponent(ITypeCheckerProvider.class)).moduleAdded();
            }
        });
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {

        rootModel.inheritSdk();

        ContentEntry contentEntry = doAddContentEntry(rootModel);
        if (contentEntry != null) {
            final List<Pair<String, String>> sourcePaths = getSourcePaths();

            if (sourcePaths != null) {
                for (final Pair<String, String> sourcePath : sourcePaths) {
                    String first = sourcePath.first;
                    new File(first).mkdirs();
                    VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByPath(FileUtil.toSystemIndependentName(first));
                    if (sourceRoot != null) {
                        JpsModuleSourceRootType<? extends JpsElement> elType =
                                "resource".equals(sourcePath.second)
                                        ? JavaResourceRootType.RESOURCE
                                        : JavaSourceRootType.SOURCE;

                        contentEntry.addSourceFolder(
                                sourceRoot,
                                elType
                        );
                    }
                }
            }

            String outPath = getContentEntryPath() + File.separator + pageTwo.getOutputDirectory();
            File outDirectory = new File(FileUtil.toSystemIndependentName(outPath));
            if (!outDirectory.exists()) {
                outDirectory.mkdir();
            }

            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(outDirectory);
            if (virtualFile != null) {
                contentEntry.addExcludeFolder(virtualFile);
            }

            setCompilerOutput(rootModel, outDirectory);
        }

    }

    public static void setCompilerOutput(ModifiableRootModel rootModel, File outDirectory) throws ConfigurationException {
        CompilerModuleExtension compiler = rootModel.getModuleExtension(CompilerModuleExtension.class);
        try {
            compiler.setCompilerOutputPath(outDirectory.getCanonicalPath());
            compiler.setCompilerOutputPathForTests(outDirectory.getCanonicalPath());
        } catch (IOException e) {
            throw new ConfigurationException(e.getMessage());
        }
        compiler.inheritCompilerOutputPath(false);
        compiler.commit();
    }

    public void persistConfiguration(Module module) {
        IdeaCeylonProjects projects = module.getProject().getComponent(IdeaCeylonProjects.class);
        projects.addProject(module);
        IdeaCeylonProject ceylonProject = (IdeaCeylonProject) projects.getProject(module);

        pageOne.apply(ceylonProject);
        pageTwo.apply(ceylonProject);

        ceylonProject.getConfiguration().setProjectSourceDirectories(
                new CeylonStringIterable(singletonList(ceylonSettings.getDefaultSourceFolder()))
        );
        ceylonProject.getConfiguration().setProjectResourceDirectories(
                new CeylonStringIterable(singletonList(ceylonSettings.getDefaultResourceFolder()))
        );
        ceylonProject.getConfiguration().save();
        ceylonProject.getIdeConfiguration().save();
        CeylonFacet facet = FacetManager.getInstance(module).addFacet(CeylonFacet.getFacetType(), CeylonFacet.getFacetType().getPresentableName(), null);
        facet.getConfiguration().setModule(module);
    }

    @Override
    public ModuleType getModuleType() {
        return CeylonModuleType.getInstance();
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() {
        if (mySourcePaths == null) {
            final List<Pair<String, String>> paths = new ArrayList<>();
            String source = ceylonSettings.getDefaultSourceFolder();
            String resource = ceylonSettings.getDefaultResourceFolder();
            String sourcePath = getContentEntryPath() + File.separator + source;
            String resourcePath = getContentEntryPath() + File.separator + resource;
            new File(sourcePath).mkdirs();
            new File(resourcePath).mkdirs();
            paths.add(Pair.create(sourcePath, "source"));
            paths.add(Pair.create(resourcePath, "resource"));
            return paths;
        }
        return mySourcePaths;
    }

    @Override
    public void setSourcePaths(List<Pair<String, String>> sourcePaths) {
        mySourcePaths = sourcePaths != null ? new ArrayList<>(sourcePaths) : null;
    }

    @Override
    public void addSourcePath(Pair<String, String> sourcePathInfo) {
        if (mySourcePaths == null) {
            mySourcePaths = new ArrayList<>();
        }
        mySourcePaths.add(sourcePathInfo);
    }

    public void setPageOne(PageOne pageOne) {
        this.pageOne = pageOne;
    }

    public void setPageTwo(PageTwo pageTwo) {
        this.pageTwo = pageTwo;
    }
}
