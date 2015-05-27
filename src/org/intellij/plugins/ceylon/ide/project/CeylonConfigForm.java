package org.intellij.plugins.ceylon.ide.project;

import com.intellij.openapi.module.Module;
import com.redhat.ceylon.ide.common.CeylonProject;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacetState;

import javax.swing.*;

/**
 * A panel to modify settings in .ceylon/config
 */
public interface CeylonConfigForm {

    JPanel getPanel();

    /**
     * Applies the user settings to the module's config.
     */
    void apply(CeylonProject<Module> project, CeylonFacetState state);

    /**
     * Checks if the settings were modified by the user.
     * @return true if the settings were modified
     */
    boolean isModified(CeylonProject<Module> project, CeylonFacetState state);

    /**
     * Loads the setting from a .ceylon/config file.
     */
    void load(CeylonProject<Module> project, CeylonFacetState state);
}
