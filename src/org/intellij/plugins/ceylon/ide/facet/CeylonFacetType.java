package org.intellij.plugins.ceylon.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.util.IconLoader;
import org.intellij.plugins.ceylon.ide.project.CeylonModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CeylonFacetType extends FacetType<CeylonFacet, CeylonFacetConfiguration> {

    public CeylonFacetType() {
        super(CeylonFacet.ID, "ceylon", "Ceylon");
    }

    @Override
    public CeylonFacetConfiguration createDefaultConfiguration() {
        return new CeylonFacetConfiguration();
    }

    @Override
    public CeylonFacet createFacet(@NotNull Module module, String name, @NotNull CeylonFacetConfiguration configuration, Facet underlyingFacet) {
        configuration.setModule(module);
        return new CeylonFacet(module, name, configuration);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        // TODO perhaps we should test if the module uses a JDK instead? (cause right now we are forgetting things like PluginModuleType
        return moduleType instanceof JavaModuleType || moduleType instanceof CeylonModuleType;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/ceylon.png");
    }
}
