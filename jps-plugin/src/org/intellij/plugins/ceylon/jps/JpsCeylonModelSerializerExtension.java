package org.intellij.plugins.ceylon.jps;

import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.facet.JpsFacetConfigurationSerializer;

import java.util.Collections;
import java.util.List;

public class JpsCeylonModelSerializerExtension extends JpsModelSerializerExtension {

    private static final List<? extends JpsFacetConfigurationSerializer<JpsCeylonModuleExtension>> FACET_PROPERTIES_LOADERS =
            Collections.singletonList(new JpsFacetConfigurationSerializer<JpsCeylonModuleExtension>(JpsCeylonModuleExtension.KIND, "ceylon", "Ceylon") {
                @Override
                public JpsCeylonModuleExtension loadExtension(@NotNull Element facetConfigurationElement,
                                                              String name,
                                                              JpsElement parent, JpsModule module) {
                    return new JpsCeylonModuleExtension(XmlSerializer.deserialize(facetConfigurationElement, JpsCeylonModuleProperties.class));
                }

                @Override
                protected void saveExtension(JpsCeylonModuleExtension extension, Element facetConfigurationTag, JpsModule module) {
                    XmlSerializer.serializeInto(extension.getProperties(), facetConfigurationTag);
                }
            });

    @NotNull
    @Override
    public List<? extends JpsFacetConfigurationSerializer<?>> getFacetConfigurationSerializers() {
        return FACET_PROPERTIES_LOADERS;
    }
}
