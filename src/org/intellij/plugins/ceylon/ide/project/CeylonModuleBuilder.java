package org.intellij.plugins.ceylon.ide.project;

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
import com.redhat.ceylon.ide.common.model.CeylonProject;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacetState;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CeylonModuleBuilder extends JavaModuleBuilder {

    private PageOne pageOne;
    private PageTwo pageTwo;

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
                        contentEntry.addSourceFolder(sourceRoot, false, sourcePath.second);
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

            String outUrl = pageTwo.getOutputDirectory().startsWith("./") ? "file://$MODULE_DIR$/" + pageTwo.getOutputDirectory().substring(2) : outPath;
            CompilerModuleExtension compiler = rootModel.getModuleExtension(CompilerModuleExtension.class);
            compiler.setCompilerOutputPath(outUrl);
            compiler.setCompilerOutputPathForTests(outUrl);
            compiler.inheritCompilerOutputPath(false);
            compiler.commit();
        }

    }

    public void persistConfiguration(Module module) {
        IdeaCeylonProjects projects = module.getProject().getComponent(IdeaCeylonProjects.class);
        projects.addProject(module);
        CeylonProject<Module> ceylonProject = projects.getProject(module);
        CeylonFacetState state = new CeylonFacetState();

        pageOne.apply(ceylonProject, state);
        pageTwo.apply(ceylonProject, state);

        ceylonProject.getConfiguration().save();
        CeylonFacet facet = FacetManager.getInstance(module).addFacet(CeylonFacet.getFacetType(), CeylonFacet.getFacetType().getPresentableName(), null);
        facet.getConfiguration().loadState(state);
    }

    @Override
    public ModuleType getModuleType() {
        return CeylonModuleType.getInstance();
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() {
        if (mySourcePaths == null) {
            final List<Pair<String, String>> paths = new ArrayList<>();
            @NonNls final String path = getContentEntryPath() + File.separator + "source";
            new File(path).mkdirs();
            paths.add(Pair.create(path, ""));
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
