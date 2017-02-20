import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject
}
import javax.swing {
    ...
}

"A panel to modify settings in .ceylon/config"
shared interface CeylonConfigForm {

    shared formal JPanel panel;

    "Applies the user settings to the module's config."
    shared formal void apply(IdeaCeylonProject project);

    "Checks if the settings were modified by the user.
     Returns true if the settings were modified."
    shared formal Boolean isModified(IdeaCeylonProject project);

    "Loads the setting from a .ceylon/config file."
    shared formal void load(IdeaCeylonProject project);
}
