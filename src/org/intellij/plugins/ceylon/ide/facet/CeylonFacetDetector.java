package org.intellij.plugins.ceylon.ide.facet;

import com.intellij.facet.FacetType;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.settings.CeylonSettings;
import org.jetbrains.annotations.NotNull;

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
        String defaultVm = CeylonSettings.getInstance().getDefaultTargetVm();
        project.getIdeConfiguration().setCompileToJs(ceylon.language.Boolean.instance(!defaultVm.equals("js")));
        project.getIdeConfiguration().setCompileToJvm(ceylon.language.Boolean.instance(!defaultVm.equals("jvm")));
        project.getConfiguration().setProjectOffline(ceylon.language.Boolean.instance(false));

        ModulesConfigurator.showFacetSettingsDialog(facet, CeylonFacetConfiguration.COMPILATION_TAB);
        TypeCheckerProvider tcp = (TypeCheckerProvider) facet.getModule().getComponent(ITypeCheckerProvider.class);
        if (tcp != null) {
            tcp.moduleAdded();
            tcp.typecheck();
        }
    }
}
