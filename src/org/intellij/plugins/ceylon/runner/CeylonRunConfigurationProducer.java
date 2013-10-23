package org.intellij.plugins.ceylon.runner;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonPsi;
import org.intellij.plugins.ceylon.psi.CeylonTypes;
import org.jetbrains.annotations.Nullable;

/**
 * TODO
 */
public class CeylonRunConfigurationProducer extends RunConfigurationProducer<RunConfiguration> {

    protected CeylonRunConfigurationProducer() {
        super(ContainerUtil.findInstance(Extensions.getExtensions(ConfigurationType.CONFIGURATION_TYPE_EP), CeylonRunConfigurationType.class));
    }

    @Override
    protected boolean setupConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        PsiFile file = sourceElement.get().getContainingFile();
        if (file instanceof CeylonFile) {
            CeylonRunConfiguration runConfig = (CeylonRunConfiguration) configuration;

            final ASTNode identifier = getMethodNode(context.getLocation());

            if (identifier != null) {
                runConfig.setFilePath(file.getVirtualFile().getCanonicalPath());
                runConfig.setTopLevelName(identifier.getText());
                runConfig.setName(identifier.getText() + "() in " + file.getName());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context) {
        ASTNode identifier = null;
        if ((configuration instanceof CeylonRunConfiguration)) {
            identifier = getMethodNode(context.getLocation());
        }

        return identifier != null && identifier.getText().equals(((CeylonRunConfiguration) configuration).getTopLevelName());
    }

    private ASTNode getMethodNode(Location contextLocation) {
        ASTNode identifier = null;
        // TODO be more accurate than containing file (e.g. check if we are inside the same toplevel method)
        if ((contextLocation != null && contextLocation.getPsiElement().getContainingFile() instanceof CeylonFile)) {
            CeylonFile file = (CeylonFile) contextLocation.getPsiElement().getContainingFile();

            CeylonPsi.DeclarationPsi topLevelDeclaration = file.findChildByClass(CeylonPsi.DeclarationPsi.class);
            if (topLevelDeclaration != null) {
                ASTNode topLevelMethod = topLevelDeclaration.getNode().findChildByType(CeylonTypes.ANY_METHOD);

                if (topLevelMethod != null) {
                    identifier = topLevelMethod.findChildByType(CeylonTypes.IDENTIFIER);
                }
            }
        }
        return identifier;
    }
}
