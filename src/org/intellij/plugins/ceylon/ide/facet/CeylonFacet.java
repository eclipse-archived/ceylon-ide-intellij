package org.intellij.plugins.ceylon.ide.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CeylonFacet extends Facet<CeylonFacetConfiguration> {

    public static final FacetTypeId<CeylonFacet> ID = new FacetTypeId<>("ceylon");

    public CeylonFacet(Module module, String name, CeylonFacetConfiguration configuration) {
        super(getFacetType(), module, name, configuration, null);
    }

    public static CeylonFacetType getFacetType() {
        return (CeylonFacetType) FacetTypeRegistry.getInstance().findFacetType(ID);
    }

    @Nullable
    public static CeylonFacet forModule(@NotNull Module module) {
        return FacetManager.getInstance(module).getFacetByType(ID);
    }
}
