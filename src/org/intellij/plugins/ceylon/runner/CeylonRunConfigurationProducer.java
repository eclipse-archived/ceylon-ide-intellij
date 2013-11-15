package org.intellij.plugins.ceylon.runner;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import org.intellij.plugins.ceylon.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.psi.CeylonFile;
import org.intellij.plugins.ceylon.psi.CeylonPsi;

import java.util.List;

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
        if (file instanceof CeylonFile && configuration instanceof CeylonRunConfiguration) {
            final CeylonFile ceylonFile = (CeylonFile) file;
            CeylonRunConfiguration runConfig = (CeylonRunConfiguration) configuration;

            final String identifier = getTopLevelMethodNode(srcElt);
            if (identifier != null) {
                final Tree.CompilationUnit cu = ceylonFile.getMyTree().getCompilationUnit();
                if (cu != null && cu.getUnit() != null && cu.getUnit().getPackage() != null && cu.getUnit().getPackage().getModule() != null) {
                    final Package pkg = cu.getUnit().getPackage();
                    final Module mdl = pkg.getModule();
                    runConfig.setCeylonModule(mdl.getNameAsString());
                    String pfx = "".equals(pkg.getNameAsString()) ? "" : pkg.getNameAsString() + ".";
                    final String topLevelNameFull = pfx + identifier;
                    runConfig.setTopLevelNameFull(topLevelNameFull);
                    runConfig.setName(topLevelNameFull);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(RunConfiguration configuration, ConfigurationContext context) {
        String identifier = null;
        if ((configuration instanceof CeylonRunConfiguration) && context.getLocation() != null) {
            identifier = getTopLevelMethodNode(context.getLocation().getPsiElement());
        }

        return identifier != null && identifier.equals(((CeylonRunConfiguration) configuration).getTopLevelNameFull());
    }

    private String getTopLevelMethodNode(PsiElement psiElement) {

        // todo: top-level classes may be run too
        CeylonPsi.AnyMethodPsi method = PsiTreeUtil.getTopmostParentOfType(psiElement, CeylonPsi.AnyMethodPsi.class);

        if (method != null) {
            final Tree.AnyMethod ceylonMethod = method.getCeylonNode();
            if (ceylonMethod != null) {
                final List<Tree.ParameterList> parameterLists = ceylonMethod.getParameterLists();
                if (!parameterLists.isEmpty()) {
                    if (parameterLists.get(0).getParameters().isEmpty()) {
                        CeylonCompositeElement parent = method;
                        // todo: make sure this doesn't loop infinitely
                        //noinspection StatementWithEmptyBody
                        while ((parent = PsiTreeUtil.getParentOfType(parent, CeylonPsi.DeclarationPsi.class)) != null && parent.getCeylonNode() == ceylonMethod) ;

                        if (parent == null) {
                            // We're in a method with no parent declaration (except possible nodes that represent the same method).
                            final Tree.Identifier methodIdent = ceylonMethod.getIdentifier();
                            return methodIdent == null ? null : methodIdent.getText();
                        }
                    }
                }
            }
        }

        return null;
    }
}
