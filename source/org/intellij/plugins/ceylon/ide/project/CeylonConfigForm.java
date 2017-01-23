package org.intellij.plugins.ceylon.ide.project;

import org.intellij.plugins.ceylon.ide.model.IdeaCeylonProject;

import javax.swing.*;

/**
 * A panel to modify settings in .ceylon/config
 */
public interface CeylonConfigForm {

    JPanel getPanel();

    /**
     * Applies the user settings to the module's config.
     */
    void apply(IdeaCeylonProject project);

    /**
     * Checks if the settings were modified by the user.
     *
     * @return true if the settings were modified
     */
    boolean isModified(IdeaCeylonProject project);

    /**
     * Loads the setting from a .ceylon/config file.
     */
    void load(IdeaCeylonProject project);
}
