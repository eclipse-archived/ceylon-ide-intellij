package org.intellij.plugins.ceylon.ide.project;

import com.redhat.ceylon.common.config.CeylonConfig;

import javax.swing.*;

import static com.redhat.ceylon.ide.project.config.AbstractProjectConfig.*;

public class PageOne implements CeylonConfigForm {
    private JCheckBox compileForJvm;
    private JCheckBox compileToJs;
    private JCheckBox enableJavaCalling;
    private JCheckBox showCompilerWarnings;
    private JPanel panel;

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void updateCeylonConfig(CeylonConfig config) {
        config.setBoolOption(PROJECT_COMPILE_TO_JVM, compileForJvm.isSelected());
        config.setBoolOption(PROJECT_COMPILE_TO_JS, compileToJs.isSelected());
        config.setBoolOption(PROJECT_SHOW_COMPILER_WARNINGS, showCompilerWarnings.isSelected());
        config.setBoolOption(PROJECT_ENABLE_JAVA_CALLING_CEYLON, enableJavaCalling.isSelected());
    }

    @Override
    public boolean isModified(CeylonConfig config) {
        return config.getBoolOption(PROJECT_COMPILE_TO_JVM, false) != compileForJvm.isSelected()
                || config.getBoolOption(PROJECT_COMPILE_TO_JS, false) != compileToJs.isSelected()
                || config.getBoolOption(PROJECT_SHOW_COMPILER_WARNINGS, false) != showCompilerWarnings.isSelected()
                || config.getBoolOption(PROJECT_ENABLE_JAVA_CALLING_CEYLON, false) != enableJavaCalling.isSelected();
    }

    @Override
    public void loadCeylonConfig(CeylonConfig config) {
        compileForJvm.setSelected(config.getBoolOption(PROJECT_COMPILE_TO_JVM, false));
        compileToJs.setSelected(config.getBoolOption(PROJECT_COMPILE_TO_JS, false));
        showCompilerWarnings.setSelected(config.getBoolOption(PROJECT_SHOW_COMPILER_WARNINGS, false));
        enableJavaCalling.setSelected(config.getBoolOption(PROJECT_ENABLE_JAVA_CALLING_CEYLON, false));
    }
}
