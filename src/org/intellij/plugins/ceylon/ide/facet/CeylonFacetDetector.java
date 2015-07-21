package org.intellij.plugins.ceylon.ide.facet;

import com.intellij.facet.FacetType;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
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
        ModulesConfigurator.showFacetSettingsDialog(facet, CeylonFacetConfiguration.COMPILATION_TAB);
        facet.getModule().getComponent(TypeCheckerProvider.class).typecheck();
    }
}
