package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.redhat.ceylon.ide.common.settings.CompletionOptions;
import org.intellij.plugins.ceylon.ide.ceylonCode.completion.completionSettings_;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompletionConfigurable extends BaseConfigurable {
    private JCheckBox displayParameterTypes;
    private JRadioButton noArgumentListsRadioButton;
    private JRadioButton positionalArgumentListsRadioButton;
    private JRadioButton bothPositionalAndNamedRadioButton;
    private JRadioButton insertsRadioButton;
    private JRadioButton overwritesRadioButton;
    private JCheckBox useLinkedMode;
    private JCheckBox proposeChainCompletions;
    private JCheckBox enableFiltersInAdditionCheckBox;
    private JPanel myPanel;
    private JButton restoreDefaultsButton;
    private ButtonGroup trailingIdentifier;
    private ButtonGroup inexactMatches;

    public CompletionConfigurable() {
        useLinkedMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proposeChainCompletions.setEnabled(useLinkedMode.isSelected());
            }
        });
        restoreDefaultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                completionSettings_.get_().loadState(new CompletionOptions());
                reset();
            }
        });
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Completion";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return myPanel;
    }

    @Override
    public void apply() throws ConfigurationException {
        CompletionOptions options = completionSettings_.get_().getOptions();

        options.setInexactMatches(getInexactMatches());
        options.setParameterTypesInCompletion(displayParameterTypes.isSelected());
        options.setCompletionMode(getCompletionMode());
        options.setLinkedModeArguments(useLinkedMode.isSelected());
        options.setChainLinkedModeArguments(proposeChainCompletions.isSelected());
        options.setEnableCompletionFilters(enableFiltersInAdditionCheckBox.isSelected());
    }

    private String getInexactMatches() {
        if (noArgumentListsRadioButton.isSelected()) {
            return "none";
        }
        if (positionalArgumentListsRadioButton.isSelected()) {
            return "positional";
        }
        return "both";
    }

    private String getCompletionMode() {
        if (insertsRadioButton.isSelected()) {
            return "insert";
        }
        return "overwrite";
    }

    @Override
    public void reset() {
        CompletionOptions options = completionSettings_.get_().getOptions();
        displayParameterTypes.setSelected(options.getParameterTypesInCompletion());

        switch (options.getInexactMatches()) {
            case "none":
                inexactMatches.setSelected(noArgumentListsRadioButton.getModel(), true);
                break;
            case "positional":
                inexactMatches.setSelected(positionalArgumentListsRadioButton.getModel(), true);
                break;
            default:
                inexactMatches.setSelected(bothPositionalAndNamedRadioButton.getModel(), true);
                break;
        }

        if (options.getCompletionMode().equals("insert")) {
            trailingIdentifier.setSelected(insertsRadioButton.getModel(), true);
        } else {
            trailingIdentifier.setSelected(overwritesRadioButton.getModel(), true);
        }

        useLinkedMode.setSelected(options.getLinkedModeArguments());
        proposeChainCompletions.setSelected(options.getChainLinkedModeArguments());
        enableFiltersInAdditionCheckBox.setSelected(options.getEnableCompletionFilters());
    }

    @Override
    public boolean isModified() {
        CompletionOptions options = completionSettings_.get_().getOptions();
        boolean isModified;

        isModified = options.getParameterTypesInCompletion() != displayParameterTypes.isSelected();
        isModified |= !options.getInexactMatches().equals(getInexactMatches());
        isModified |= options.getChainLinkedModeArguments() != proposeChainCompletions.isSelected();
        isModified |= !options.getCompletionMode().equals(getCompletionMode());
        isModified |= options.getLinkedModeArguments() != useLinkedMode.isSelected();
        isModified |= options.getChainLinkedModeArguments() != proposeChainCompletions.isSelected();
        isModified |= options.getEnableCompletionFilters() != enableFiltersInAdditionCheckBox.isSelected();

        return isModified;
    }

    @Override
    public void disposeUIResources() {

    }
}
