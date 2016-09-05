package org.intellij.plugins.ceylon.ide.facet;

import com.intellij.facet.FacetType;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import com.redhat.ceylon.common.config.CeylonConfig;
import com.redhat.ceylon.common.config.CeylonConfigFinder;
import com.redhat.ceylon.common.config.DefaultToolOptions;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.ceylonSettings_;
import org.intellij.plugins.ceylon.ide.project.CeylonModuleBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static org.intellij.plugins.ceylon.ide.project.CeylonModuleBuilder.setCompilerOutput;

/**
 * Detects if a missing Ceylon facet could be added to the current module.
 */
public class CeylonFacetDetector extends FacetBasedFrameworkDetector<CeylonFacet, CeylonFacetConfiguration> {

    public CeylonFacetDetector() {
        super("ceylon");
    }

    @Override
    public FacetType<CeylonFacet, CeylonFacetConfiguration> getFacetType() {
        return CeylonFacet.getFacetType();
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CeylonFileType.INSTANCE;
    }

    @NotNull
    @Override
    public ElementPattern<FileContent> createSuitableFilePattern() {
        return FileContentPattern.fileContent();
    }

    @Override
    public void setupFacet(@NotNull CeylonFacet facet, ModifiableRootModel model) {
        IdeaCeylonProjects ceylonModel = facet.getModule().getProject().getComponent(IdeaCeylonProjects.class);
        IdeaCeylonProject project = (IdeaCeylonProject) ceylonModel.getProject(facet.getModule());

        try {
            VirtualFile virtualFile = model.getSourceRoots()[0];
            CeylonConfig config = CeylonConfigFinder.loadLocalConfig(new File(virtualFile.getCanonicalPath()));
            if (config != null) {
                String outputRepo = DefaultToolOptions.getCompilerOutputRepo(config);
                if (outputRepo == null) {
                    outputRepo = "./modules";
                }
                File outputDirectory = new File(project.getRootDirectory(), outputRepo);
                setCompilerOutput(model, outputDirectory);
            }
        } catch (IOException | ConfigurationException e) {
            e.printStackTrace();
        }

        String defaultVm = ceylonSettings_.get_().getDefaultTargetVm();
        project.getIdeConfiguration().setCompileToJs(ceylon.language.Boolean.instance(!defaultVm.equals("js")));
        project.getIdeConfiguration().setCompileToJvm(ceylon.language.Boolean.instance(!defaultVm.equals("jvm")));
        project.getConfiguration().setProjectOffline(ceylon.language.Boolean.instance(false));

        ModulesConfigurator.showFacetSettingsDialog(facet, CeylonFacetConfiguration.COMPILATION_TAB);
        TypeCheckerProvider tcp = (TypeCheckerProvider) facet.getModule().getComponent(ITypeCheckerProvider.class);
        if (tcp != null) {
            tcp.moduleAdded();
        }
    }
}
