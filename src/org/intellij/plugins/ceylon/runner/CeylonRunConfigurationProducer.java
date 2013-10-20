package org.intellij.plugins.ceylon.runner;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.intellij.plugins.ceylon.psi.CeylonTypes;

/**
 * TODO
 */
public class CeylonRunConfigurationProducer extends RunConfigurationProducer<RunConfiguration> {

    protected CeylonRunConfigurationProducer() {
        super(ContainerUtil.findInstance(Extensions.getExtensions(ConfigurationType.CONFIGURATION_TYPE_EP), CeylonRunConfigurationType.class));
    }

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        if (!(sourceElement.get() instanceof CeylonFile)) {
            return false;
        }
        CeylonFile file = (CeylonFile) sourceElement.get();
        CeylonRunConfiguration runConfig = (CeylonRunConfiguration) configuration;


        CeylonPsi.DeclarationPsi topLevelDeclaration = file.findChildByClass(CeylonPsi.DeclarationPsi.class);
        if (topLevelDeclaration != null) {
            ASTNode topLevelMethod = topLevelDeclaration.getNode().findChildByType(CeylonTypes.ANY_METHOD);

            if (topLevelMethod != null) {
                ASTNode identifier = topLevelMethod.findChildByType(CeylonTypes.IDENTIFIER);

                if (identifier != null) {
                    runConfig.setFilePath(file.getVirtualFile().getCanonicalPath());
                    runConfig.setTopLevelName(identifier.getText());
                    runConfig.setName(identifier.getText() + "() in " + file.getName());
                }
            }
        }

        return runConfig.getName() != null;
    }

    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context) {
        if (!(configuration instanceof CeylonRunConfiguration)) {
            return false;
        }

        if (!(context.getLocation().getPsiElement().getContainingFile() instanceof CeylonFile)) {
            return false;
        }

        CeylonFile file = (CeylonFile) context.getLocation().getPsiElement().getContainingFile();

        CeylonPsi.DeclarationPsi topLevelDeclaration = file.findChildByClass(CeylonPsi.DeclarationPsi.class);
        if (topLevelDeclaration != null) {
            ASTNode topLevelMethod = topLevelDeclaration.getNode().findChildByType(CeylonTypes.ANY_METHOD);

            if (topLevelMethod != null) {
                ASTNode identifier = topLevelMethod.findChildByType(CeylonTypes.IDENTIFIER);

                if (identifier != null) {
                    return identifier.getText().equals(((CeylonRunConfiguration) configuration).getTopLevelName());
                }
            }
        }

        return false;
    }
}
