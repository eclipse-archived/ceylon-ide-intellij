package org.intellij.plugins.ceylon.ide.project;

import com.redhat.ceylon.ide.common.util.versionsAvailableForBoostrap_;

import javax.swing.*;

import static ceylon.interop.java.createJavaStringArray_.createJavaStringArray;

public class PageZero {

    private JCheckBox createBootstrapFiles;
    private JComboBox<String> version;
    private JPanel panel;

    PageZero() {
        String[] versions = createJavaStringArray(versionsAvailableForBoostrap_.get_());
        version.setModel(new DefaultComboBoxModel<>(versions));
    }

    JPanel getPanel() {
        return panel;
    }

    Boolean isCreateBootstrapFiles() {
        return createBootstrapFiles.isSelected();
    }

    public String getVersion() {
        return version.getSelectedItem().toString();
    }
}
