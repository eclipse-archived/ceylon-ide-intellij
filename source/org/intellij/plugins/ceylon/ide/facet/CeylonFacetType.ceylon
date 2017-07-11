import com.intellij.facet {
    Facet,
    FacetType
}
import com.intellij.openapi.\imodule {
    JavaModuleType,
    Module,
    ModuleType
}

import org.intellij.plugins.ceylon.ide.project {
    CeylonModuleType
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared class CeylonFacetType()
        extends FacetType<CeylonFacet,CeylonFacetConfiguration>(CeylonFacet.id, "ceylon", "Ceylon") {

    createDefaultConfiguration() => CeylonFacetConfiguration();

    shared actual CeylonFacet createFacet(Module mod, String name,
        CeylonFacetConfiguration configuration, Facet<out Anything> underlyingFacet) {

        configuration.setModule(mod);
        return CeylonFacet(mod, name, configuration);
    }

    isSuitableModuleType(ModuleType<out Anything> moduleType)
            => moduleType is JavaModuleType
            || moduleType is CeylonModuleType
            ||"PLUGIN_MODULE" == moduleType.id;

    icon => icons.ceylon;
}
