package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CompilerConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    private JRadioButton inProcessMode;
    private JRadioButton outProcessMode;
    private JPanel mainPanel;

    @NotNull
    @Override
    public String getId() {
        return "preferences.Ceylon.compiler";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Ceylon compiler";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        CeylonSettings settings = CeylonSettings.getInstance();

        return (outProcessMode.isSelected() != settings.isUseOutProcessBuild());
    }

    @Override
    public void apply() throws ConfigurationException {
        CeylonSettings settings = CeylonSettings.getInstance();
        settings.setUseOutProcessBuild(outProcessMode.isSelected());
    }

    @Override
    public void reset() {
        CeylonSettings settings = CeylonSettings.getInstance();
        outProcessMode.setSelected(settings.isUseOutProcessBuild());
        inProcessMode.setSelected(!settings.isUseOutProcessBuild());
    }

    @Override
    public void disposeUIResources() {

    }
}
