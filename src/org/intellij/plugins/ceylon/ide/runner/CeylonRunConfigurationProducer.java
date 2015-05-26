package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Module;
import org.intellij.plugins.ceylon.ide.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;

import java.util.List;
import java.util.Objects;

public class CeylonRunConfigurationProducer extends RunConfigurationProducer<CeylonRunConfiguration> {

    protected CeylonRunConfigurationProducer() {
        super(ContainerUtil.findInstance(Extensions.getExtensions(ConfigurationType.CONFIGURATION_TYPE_EP), CeylonRunConfigurationType.class));
    }

    @Override
    protected boolean setupConfigurationFromContext(CeylonRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        final RunConfigParams params = getRunConfigParams(sourceElement.get());

        if (params == null) {
            return false;
        }
        String pfx = params.pkg.isEmpty() ? "" : params.pkg + ".";
        final String topLevelNameFull = pfx + params.topLevel;
        configuration.setTopLevelNameFull(topLevelNameFull);
        configuration.setName(topLevelNameFull);
        configuration.setCeylonModule(params.module);
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(CeylonRunConfiguration rc, ConfigurationContext context) {
        RunConfigParams params = null;
        if (context.getLocation() != null) {
            params = getRunConfigParams(context.getLocation().getPsiElement());
        }

        return params != null && params.equals(new RunConfigParams(rc));
    }

    private RunConfigParams getRunConfigParams(PsiElement psiElement) {

        PsiFile file = psiElement.getContainingFile();
        if (file instanceof CeylonFile) {
            final CeylonFile ceylonFile = (CeylonFile) file;

            // todo: top-level classes may be run too
            CeylonPsi.DeclarationPsi toplevel = PsiTreeUtil.getTopmostParentOfType(psiElement, CeylonPsi.DeclarationPsi.class);

            if (toplevel != null) {
                final Tree.Declaration ceylonNode = toplevel.getCeylonNode();
                if (isRunnable(ceylonNode)) {
                    CeylonCompositeElement parent = getTopMostElement(toplevel, ceylonNode);
                    if (parent == null) {
                        // We're in a method with no parent declaration (except possible nodes that represent the same method).
                        final Tree.Identifier methodIdentifier = ceylonNode.getIdentifier();
                        if (methodIdentifier != null) {
                            final String identifier = methodIdentifier.getText();
                            final Tree.CompilationUnit cu = ceylonFile.getCompilationUnit();
                            if (cu != null && cu.getUnit() != null && cu.getUnit().getPackage() != null && cu.getUnit().getPackage().getModule() != null) {
                                final Module mdl = cu.getUnit().getPackage().getModule();
                                final String moduleName = mdl.getNameAsString();
                                final String packageName = ceylonFile.getPackageName();
                                return new RunConfigParams(moduleName, packageName, identifier);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private CeylonCompositeElement getTopMostElement(CeylonPsi.DeclarationPsi toplevel, Tree.Declaration ceylonNode) {
        CeylonCompositeElement parent = toplevel;
        //noinspection StatementWithEmptyBody
        while ((parent = PsiTreeUtil.getParentOfType(parent, CeylonPsi.DeclarationPsi.class)) != null && parent.getCeylonNode() == ceylonNode) ;
        return parent;
    }

    // Needs to be a method with 0 parameters or class definition to be runnable.
    private boolean isRunnable(Tree.Declaration ceylonNode) {
        if (ceylonNode instanceof Tree.AnyMethod) {
            return isRunnableMethod((Tree.AnyMethod) ceylonNode);
        }
        return ceylonNode instanceof Tree.ClassDefinition;
    }

    private boolean isRunnableMethod(Tree.AnyMethod ceylonMethod) {
        final List<Tree.ParameterList> parameterLists = ceylonMethod.getParameterLists();
        if (parameterLists.isEmpty() || !parameterLists.get(0).getParameters().isEmpty()) {
            return false;
        }
        return true;
    }

    static class RunConfigParams {
        final String module;
        final String pkg;
        final String topLevel;

        RunConfigParams(String module, String pkg, String topLevel) {
            this.module = module;
            this.pkg = pkg;
            this.topLevel = topLevel;
        }

        public RunConfigParams(CeylonRunConfiguration rc) {
            module = rc.getCeylonModule();
            final String full = rc.getTopLevelNameFull();
            final int dot = full.lastIndexOf('.');
            topLevel = (dot < 0) ? full : full.substring(dot + 1);
            pkg = (dot < 0) ? "" : full.substring(0, dot);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final RunConfigParams that = (RunConfigParams) o;
            return Objects.equals(this.module, that.module)
                    && Objects.equals(this.pkg, that.pkg)
                    && Objects.equals(this.topLevel, that.topLevel);
        }

        @Override
        public int hashCode() {
            return Objects.hash(module, pkg, topLevel);
        }
    }
}
