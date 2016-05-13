package org.intellij.plugins.ceylon.ide.wizard;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import com.redhat.ceylon.model.typechecker.model.Module;
import org.apache.commons.lang.StringUtils;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Set;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.all;
import static org.intellij.plugins.ceylon.ide.validate.NameValidator.packageNameIsLegal;
import static org.intellij.plugins.ceylon.ide.validate.NameValidator.unitNameIsLegal;
import static java.util.Arrays.asList;
import static org.intellij.plugins.ceylon.ide.CeylonBundle.message;

public class CreateCeylonModuleWizard extends DialogWrapper {
    private final IdeaCeylonProject project;
    private JPanel contentPane;
    private JTextField moduleName;
    private JTextField moduleVersion;
    private JCheckBox sharedPackage;
    private JTextField compilationUnit;
    private JPanel headerPane;

    public CreateCeylonModuleWizard(@NotNull Project project, @Nullable IdeaCeylonProject ceylonProject) {
        super(project, true);
        this.project = ceylonProject;

        setTitle(message("ceylon.module.wizard.title"));
        headerPane.setBackground(JBColor.WHITE);
        init();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String name = moduleName.getText();

        if (StringUtils.isBlank(name)) {
            return new ValidationInfo(message("ceylon.module.wizard.error.blank"), moduleName);
        } else if (!packageNameIsLegal(name)) {
            return new ValidationInfo(message("ceylon.module.wizard.error.illegal", name), moduleName);
        } else if (moduleExists(name)) {
            return new ValidationInfo(message("ceylon.module.wizard.error.duplicate", name), moduleName);
        }

        String unit = compilationUnit.getText();

        if (!unitNameIsLegal(unit)) {
            return new ValidationInfo(message("ceylon.module.wizard.error.unit"), compilationUnit);
        }

        return null;
    }

    private boolean moduleExists(String moduleName) {
        Set<Module> modules;

        // Might not be a Ceylon project yet
        if (project == null) {
            return false;
        }
        if (all(asList(project, project.getModules(), project.getModules().getTypecheckerModules(),
                modules = project.getModules().getTypecheckerModules().getListOfModules()),
                notNull())) {
            for (Module module : modules) {
                if (module.getNameAsString().equals(moduleName)) {
                    return true;
                }
            }
            return false;
        }
        // cannot tell whether the module exists, so let's be safe! Should never happen.
        return true;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    public String getModuleName() {
        return moduleName.getText();
    }

    public String getModuleVersion() {
        return moduleVersion.getText();
    }

    public String getCompilationUnitName() {
        return compilationUnit.getText();
    }

    public boolean isSharedPackage() {
        return sharedPackage.isSelected();
    }

}
