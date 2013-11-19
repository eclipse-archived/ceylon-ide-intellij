package org.intellij.plugins.ceylon.runner;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
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
import java.util.Objects;

/**
 * TODO
 */
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
        String pfx = "".equals(params.pkg) ? "" : params.pkg + ".";
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
                final Tree.Declaration ceylonNode =  toplevel.getCeylonNode();
                // Needs to be a method with 0 parameters or class definition to be runnable.
                if (ceylonNode instanceof Tree.AnyMethod) {
                    final Tree.AnyMethod ceylonMethod = (Tree.AnyMethod) ceylonNode;
                    final List<Tree.ParameterList> parameterLists = ceylonMethod.getParameterLists();
                    if (parameterLists.isEmpty() || !parameterLists.get(0).getParameters().isEmpty()) {
                        return null;
                    }
                } else if (!(ceylonNode instanceof Tree.ClassDefinition)) {
                    return null;
                }

                CeylonCompositeElement parent = toplevel;
                //noinspection StatementWithEmptyBody
                while ((parent = PsiTreeUtil.getParentOfType(parent, CeylonPsi.DeclarationPsi.class)) != null && parent.getCeylonNode() == ceylonNode) ;

                if (parent == null) {
                    // We're in a method with no parent declaration (except possible nodes that represent the same method).
                    final Tree.Identifier methodIdent = ceylonNode.getIdentifier();
                    if (methodIdent != null) {
                        final String identifier = methodIdent.getText();
                        final Tree.CompilationUnit cu = ceylonFile.getMyTree().getCompilationUnit();
                        if (cu != null && cu.getUnit() != null && cu.getUnit().getPackage() != null && cu.getUnit().getPackage().getModule() != null) {
                            final Package pkg = cu.getUnit().getPackage();
                            final Module mdl = pkg.getModule();
                            final String moduleName = mdl.getNameAsString();
                            final String packageName = pkg.getNameAsString();
                            return new RunConfigParams(moduleName, packageName, identifier);
                        }
                    }
                }
            }
        }

        return null;
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
