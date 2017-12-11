/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
package org.eclipse.ceylon.ide.intellij.settings;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractCompletionConfigurable extends BaseConfigurable {
    protected JCheckBox useColoredLabelsInCheckBox;
    protected JCheckBox displayParameterTypes;
    protected JRadioButton noArgumentListsRadioButton;
    protected JRadioButton positionalArgumentListsRadioButton;
    protected JRadioButton bothPositionalAndNamedRadioButton;
    protected JRadioButton insertsRadioButton;
    protected JRadioButton overwritesRadioButton;
    protected JCheckBox useLinkedMode;
    protected JCheckBox proposeChainCompletions;
    //    protected JCheckBox enableFiltersInAdditionCheckBox;
    protected JPanel myPanel;
    protected JButton restoreDefaultsButton;
    protected ButtonGroup trailingIdentifier;
    protected ButtonGroup inexactMatches;

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
