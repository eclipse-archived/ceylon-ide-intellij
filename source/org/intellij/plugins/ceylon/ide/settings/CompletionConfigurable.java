package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.redhat.ceylon.ide.common.settings.CompletionOptions;
import org.intellij.plugins.ceylon.ide.completion.completionSettings_;
import org.intellij.plugins.ceylon.ide.settings.ceylonSettings_;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompletionConfigurable extends BaseConfigurable {
    private JCheckBox useColoredLabelsInCheckBox;
    private JCheckBox displayParameterTypes;
    private JRadioButton noArgumentListsRadioButton;
    private JRadioButton positionalArgumentListsRadioButton;
    private JRadioButton bothPositionalAndNamedRadioButton;
    private JRadioButton insertsRadioButton;
    private JRadioButton overwritesRadioButton;
    private JCheckBox useLinkedMode;
    private JCheckBox proposeChainCompletions;
    //    private JCheckBox enableFiltersInAdditionCheckBox;
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
//        options.setEnableCompletionFilters(enableFiltersInAdditionCheckBox.isSelected());

        ceylonSettings_.get_()
                .setHighlightedLabels(useColoredLabelsInCheckBox.isSelected());
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
//        enableFiltersInAdditionCheckBox.setSelected(options.getEnableCompletionFilters());

        useColoredLabelsInCheckBox.setSelected(ceylonSettings_.get_().getHighlightedLabels());
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
//        isModified |= options.getEnableCompletionFilters() != enableFiltersInAdditionCheckBox.isSelected();
        isModified |= ceylonSettings_.get_().getHighlightedLabels() != useColoredLabelsInCheckBox.isSelected();

        return isModified;
    }

    @Override
    public void disposeUIResources() {

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        myPanel = new JPanel();
        myPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        myPanel.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
        myPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder("Ceylon code completion"));
        displayParameterTypes = new JCheckBox();
        displayParameterTypes.setSelected(true);
        displayParameterTypes.setText("Display parameter types in completion proposals");
        panel1.add(displayParameterTypes, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("For inexact matches propose");
        panel1.add(label1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        noArgumentListsRadioButton = new JRadioButton();
        noArgumentListsRadioButton.setSelected(false);
        noArgumentListsRadioButton.setText("no argument lists");
        panel1.add(noArgumentListsRadioButton, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        positionalArgumentListsRadioButton = new JRadioButton();
        positionalArgumentListsRadioButton.setSelected(true);
        positionalArgumentListsRadioButton.setText("positional argument lists");
        panel1.add(positionalArgumentListsRadioButton, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        bothPositionalAndNamedRadioButton = new JRadioButton();
        bothPositionalAndNamedRadioButton.setText("both positional and named argument lists");
        panel1.add(bothPositionalAndNamedRadioButton, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        final JLabel label2 = new JLabel();
        label2.setText("Completion with trailing identifier character");
        panel1.add(label2, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        insertsRadioButton = new JRadioButton();
        insertsRadioButton.setSelected(true);
        insertsRadioButton.setText("inserts");
        panel1.add(insertsRadioButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 2, false));
        overwritesRadioButton = new JRadioButton();
        overwritesRadioButton.setText("overwrites");
        panel1.add(overwritesRadioButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        useColoredLabelsInCheckBox = new JCheckBox();
        useColoredLabelsInCheckBox.setText("Use colored labels in proposal lists");
        panel1.add(useColoredLabelsInCheckBox, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        restoreDefaultsButton = new JButton();
        restoreDefaultsButton.setText("Restore Defaults");
        myPanel.add(restoreDefaultsButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        myPanel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder("Linked mode argument completion"));
        useLinkedMode = new JCheckBox();
        useLinkedMode.setSelected(true);
        useLinkedMode.setText("Use linked mode to complete argument lists");
        panel2.add(useLinkedMode, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        proposeChainCompletions = new JCheckBox();
        proposeChainCompletions.setEnabled(true);
        proposeChainCompletions.setText("Propose chain completions for arguments");
        panel2.add(proposeChainCompletions, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        inexactMatches = new ButtonGroup();
        inexactMatches.add(noArgumentListsRadioButton);
        inexactMatches.add(positionalArgumentListsRadioButton);
        inexactMatches.add(bothPositionalAndNamedRadioButton);
        trailingIdentifier = new ButtonGroup();
        trailingIdentifier.add(insertsRadioButton);
        trailingIdentifier.add(overwritesRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return myPanel;
    }
}
