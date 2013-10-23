package org.intellij.plugins.ceylon.runner;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.containers.ContainerUtil;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.psi.CeylonCompositeElement;
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
        final PsiElement srcElt = sourceElement.get();
        PsiFile file = srcElt.getContainingFile();
        if (file instanceof CeylonFile) {
            CeylonRunConfiguration runConfig = (CeylonRunConfiguration) configuration;

            final ASTNode identifier = getTopLevelMethodNode(srcElt);

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
        if ((configuration instanceof CeylonRunConfiguration) && context.getLocation() != null) {
            identifier = getTopLevelMethodNode(context.getLocation().getPsiElement());
        }

        return identifier != null && identifier.getText().equals(((CeylonRunConfiguration) configuration).getTopLevelName());
    }

    private ASTNode getTopLevelMethodNode(PsiElement psiElement) {

        CeylonPsi.AnyMethodPsi method = PsiTreeUtil.getTopmostParentOfType(psiElement, CeylonPsi.AnyMethodPsi.class);

        if (method != null) {
            final Node ceylonNode = method.getCeylonNode();
            if (ceylonNode != null) {
                CeylonCompositeElement parent = method;
                // todo: make sure this doesn't loop infinitely
                //noinspection StatementWithEmptyBody
                while ((parent = PsiTreeUtil.getParentOfType(parent, CeylonPsi.DeclarationPsi.class)) != null && parent.getCeylonNode() == ceylonNode);

                if (parent == null) {
                    // We're in a method with no parent declaration (except possible nodes that represent the same method).
                    return method.getNode().findChildByType(CeylonTypes.IDENTIFIER);
                }
            }
        }

        return null;
    }
}
