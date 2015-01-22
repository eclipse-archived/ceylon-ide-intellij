package org.intellij.plugins.ceylon.ide.project;

import com.redhat.ceylon.common.config.CeylonConfig;

import javax.swing.*;

import static com.redhat.ceylon.ide.project.config.AbstractProjectConfig.*;

public class PageOne {
    private JCheckBox compileForJvm;
    private JCheckBox compileToJs;
    private JCheckBox enableJavaCalling;
    private JCheckBox showCompilerWarnings;
    private JPanel panel;

    public JPanel getPanel() {
        return panel;
    }

    public void updateCeylonConfig(CeylonConfig config) {
        config.setBoolOption(PROJECT_COMPILE_TO_JVM, compileForJvm.isSelected());
        config.setBoolOption(PROJECT_COMPILE_TO_JS, compileToJs.isSelected());
        config.setBoolOption(PROJECT_SHOW_COMPILER_WARNINGS, showCompilerWarnings.isSelected());
        config.setBoolOption(PROJECT_ENABLE_JAVA_CALLING_CEYLON, enableJavaCalling.isSelected());
    }
}
