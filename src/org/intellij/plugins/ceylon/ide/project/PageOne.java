package org.intellij.plugins.ceylon.ide.project;

import com.redhat.ceylon.compiler.typechecker.analyzer.Warning;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.ceylonSettings_;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.StringTokenizer;

public class PageOne implements CeylonConfigForm {
    private JCheckBox compileForJvm;
    private JCheckBox compileToJs;
    private JPanel panel;
    private JCheckBox workOffline;
    private JTextArea suppressedWarnings;

    public PageOne() {
        String defaultVm = ceylonSettings_.get_().getDefaultTargetVm();
        compileForJvm.setSelected(!defaultVm.equals("js"));
        compileToJs.setSelected(!defaultVm.equals("jvm"));
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void apply(IdeaCeylonProject project) {
        project.getIdeConfiguration().setCompileToJvm(ceylon.language.Boolean.instance(compileForJvm.isSelected()));
        project.getIdeConfiguration().setCompileToJs(ceylon.language.Boolean.instance(compileToJs.isSelected()));
        project.getConfiguration().setProjectOffline(ceylon.language.Boolean.instance(workOffline.isSelected()));
        project.getConfiguration().setProjectSuppressWarningsEnum(EnumSet.copyOf(parseWarnings(suppressedWarnings.getText())));
    }

    @Override
    public boolean isModified(IdeaCeylonProject project) {
        return project.getIdeConfiguration().getCompileToJvm() == null
                || project.getIdeConfiguration().getCompileToJs() == null
                || project.getConfiguration().getProjectOffline() == null
                || project.getIdeConfiguration().getCompileToJvm().booleanValue() != compileForJvm.isSelected()
                || project.getIdeConfiguration().getCompileToJs().booleanValue() != compileToJs.isSelected()
                || project.getConfiguration().getProjectOffline().booleanValue() != workOffline.isSelected()
                || warningsAsString(project.getConfiguration().getSuppressWarningsEnum()) != suppressedWarnings.getText();
    }

    @Override
    public void load(IdeaCeylonProject project) {
        String defaultVm = ceylonSettings_.get_().getDefaultTargetVm();
        compileForJvm.setSelected(safeNullBoolean(project.getIdeConfiguration().getCompileToJvm(), !defaultVm.equals("js")));
        compileToJs.setSelected(safeNullBoolean(project.getIdeConfiguration().getCompileToJs(), !defaultVm.equals("jvm")));
        workOffline.setSelected(safeNullBoolean(project.getConfiguration().getProjectOffline(), false));
        suppressedWarnings.setText(warningsAsString(project.getConfiguration().getSuppressWarningsEnum()));
    }

    @NotNull
    private List<Warning> parseWarnings(String text) {
        List<Warning> warnings = new ArrayList<Warning>();
        StringTokenizer tokenizer = new StringTokenizer(text, ",; \t\n\r\f");
        while (tokenizer.hasMoreTokens()) {
            try {
                warnings.add(Warning.valueOf(tokenizer.nextToken()));
            }
            catch (IllegalArgumentException iae) {}
        }
        return warnings;
    }

    @NotNull
    private String warningsAsString(Iterable<Warning> warnings) {
        StringBuilder text = new StringBuilder();
        for (Warning names: warnings) {
            if (text.length()>0) {
                text.append(", ");
            }
            text.append(names);
        }
        return text.toString();
    }

    private boolean safeNullBoolean(ceylon.language.Boolean bool, boolean def) {
        return bool == null ? def : bool.booleanValue();
    }
}
