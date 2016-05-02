package org.intellij.plugins.ceylon.jps;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.serialization.JpsGlobalExtensionSerializer;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.facet.JpsFacetConfigurationSerializer;

import java.util.Collections;
import java.util.List;

import static com.intellij.util.xmlb.XmlSerializer.deserialize;
import static com.intellij.util.xmlb.XmlSerializer.serializeInto;

public class JpsCeylonModelSerializerExtension extends JpsModelSerializerExtension {

    private static final List<? extends JpsFacetConfigurationSerializer<JpsCeylonModuleExtension>> FACET_PROPERTIES_LOADERS =
            Collections.singletonList(
                    new JpsFacetConfigurationSerializer<JpsCeylonModuleExtension>(
                            JpsCeylonModuleExtension.KIND, "ceylon", "Ceylon") {

                        @Override
                        public JpsCeylonModuleExtension loadExtension(@NotNull Element facetConfigurationElement,
                                                                      String name,
                                                                      JpsElement parent,
                                                                      JpsModule module) {
                            return new JpsCeylonModuleExtension(
                                    deserialize(facetConfigurationElement, JpsCeylonModuleProperties.class));
                        }

                        @Override
                        protected void saveExtension(JpsCeylonModuleExtension extension,
                                                     Element facetConfigurationTag,
                                                     JpsModule module) {
                            serializeInto(extension.getProperties(), facetConfigurationTag);
                        }
                    }
            );

    private static final List<? extends JpsGlobalExtensionSerializer> GLOBAL_SETTINGS_SERIALIZER =
            Collections.singletonList(
                    new JpsGlobalExtensionSerializer("ceylon.xml", "CeylonSettings") {

                        @Override
                        public void loadExtension(@NotNull JpsGlobal jpsGlobal,
                                                  @NotNull Element componentTag) {
                            JpsCeylonGlobalSettings.INSTANCE =
                                    deserialize(componentTag, JpsCeylonGlobalSettings.class);
                        }

                        @Override
                        public void loadExtensionWithDefaultSettings(@NotNull JpsGlobal jpsGlobal) {
                            JpsCeylonGlobalSettings.INSTANCE = new JpsCeylonGlobalSettings();
                        }

                        @Override
                        public void saveExtension(@NotNull JpsGlobal jpsGlobal,
                                                  @NotNull Element componentTag) {

                        }
                    }
            );

    @NotNull
    @Override
    public List<? extends JpsFacetConfigurationSerializer<?>> getFacetConfigurationSerializers() {
        return FACET_PROPERTIES_LOADERS;
    }

    @NotNull
    @Override
    public List<? extends JpsGlobalExtensionSerializer> getGlobalExtensionSerializers() {
        return GLOBAL_SETTINGS_SERIALIZER;
    }
}
