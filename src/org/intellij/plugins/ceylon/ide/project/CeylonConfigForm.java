package org.intellij.plugins.ceylon.ide.project;

import com.redhat.ceylon.common.config.CeylonConfig;

import javax.swing.*;

/**
 * A panel to modify settings in .ceylon/config
 */
public interface CeylonConfigForm {

    JPanel getPanel();

    /**
     * Applies the user settings to the module's config.
     * @param config the configuration to update
     */
    void updateCeylonConfig(CeylonConfig config);

    /**
     * Checks if the settings were modified by the user.
     * @param config the original config to compare to
     * @return true if the settings were modified
     */
    boolean isModified(CeylonConfig config);

    /**
     * Loads the setting from a .ceylon/config file.
     * @param config the settings to load
     */
    void loadCeylonConfig(CeylonConfig config);
}
