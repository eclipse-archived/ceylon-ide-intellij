package org.intellij.plugins.ceylon.jps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.ex.JpsElementBase;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;

public class JpsCeylonModuleExtension extends JpsElementBase<JpsCeylonModuleExtension> implements JpsElement {
    public static final JpsElementChildRoleBase<JpsCeylonModuleExtension> KIND = JpsElementChildRoleBase.create("ceylon extension");

    private JpsCeylonModuleProperties properties;

    public JpsCeylonModuleExtension(JpsCeylonModuleProperties properties) {
        this.properties = properties;
    }

    @NotNull
    @Override
    public JpsCeylonModuleExtension createCopy() {
        return new JpsCeylonModuleExtension(properties);
    }

    @Override
    public void applyChanges(@NotNull JpsCeylonModuleExtension modified) {
        properties = modified.properties;
    }

    public JpsCeylonModuleProperties getProperties() {
        return properties;
    }

}