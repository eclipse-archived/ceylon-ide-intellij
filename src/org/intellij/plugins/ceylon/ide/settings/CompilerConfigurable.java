package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.ceylonSettings_;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.CeylonSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CompilerConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    private JRadioButton inProcessMode;
    private JRadioButton outProcessMode;
    private JPanel mainPanel;
    private JCheckBox verboseCheckbox;
    private JCheckBox allCheckBox;
    private JCheckBox loaderCheckBox;
    private JCheckBox astCheckBox;
    private JCheckBox codeCheckBox;
    private JCheckBox cmrCheckBox;
    private JCheckBox benchmarkCheckBox;

    private List<JCheckBox> verbosities = Arrays.asList(
            allCheckBox, loaderCheckBox, astCheckBox,
            codeCheckBox, cmrCheckBox, benchmarkCheckBox
    );

    public CompilerConfigurable() {
        verboseCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                for (JCheckBox cb : verbosities) {
                    cb.setEnabled(verboseCheckbox.isSelected());
                }
            }
        });

        allCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                for (JCheckBox cb : verbosities) {
                    if (cb != allCheckBox) {
                        cb.setEnabled(!allCheckBox.isSelected());
                    }
                }
            }
        });
    }

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
        CeylonSettings settings = ceylonSettings_.get_();

        return (
                outProcessMode.isSelected() != settings.getUseOutProcessBuild()
                || verboseCheckbox.isSelected() != settings.getCompilerVerbose()
                || !Objects.equals(getVerbosityLevel(), settings.getVerbosityLevel())
        );
    }

    @Override
    public void apply() throws ConfigurationException {
        CeylonSettings settings = ceylonSettings_.get_();
        settings.setUseOutProcessBuild(outProcessMode.isSelected());
        settings.setCompilerVerbose(verboseCheckbox.isSelected());
        settings.setVerbosityLevel(getVerbosityLevel());
    }

    @Override
    public void reset() {
        CeylonSettings settings = ceylonSettings_.get_();
        outProcessMode.setSelected(settings.getUseOutProcessBuild());
        inProcessMode.setSelected(!settings.getUseOutProcessBuild());
        verboseCheckbox.setSelected(settings.getCompilerVerbose());
        setVerbosityLevel(settings.getVerbosityLevel());
    }

    @Override
    public void disposeUIResources() {

    }

    private String getVerbosityLevel() {
        if (allCheckBox.isSelected()) {
            return "all";
        } else {
            StringBuilder builder = new StringBuilder();

            for (JCheckBox cb : verbosities) {
                if (cb.isSelected()) {
                    if (builder.length() > 0) {
                        builder.append(",");
                    }
                    builder.append(cb.getText());
                }
            }

            return builder.toString();
        }
    }

    private void setVerbosityLevel(String level) {
        for (JCheckBox cb : verbosities) {
            cb.setSelected(false);
        }

        String[] parts = level.split(",");

        for (String part : parts) {
            for (JCheckBox cb : verbosities) {
                if (Objects.equals(cb.getText(), part)) {
                    cb.setSelected(true);
                }
            }
        }
    }
}
