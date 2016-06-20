package org.intellij.plugins.ceylon.ide.runner;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Unit;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static com.intellij.openapi.extensions.Extensions.getExtensions;
import static com.intellij.util.containers.ContainerUtil.findInstance;

/**
 * Produces run configurations for the JVM. Can be overriden to produce configuration for other
 * backends.
 */
public class CeylonRunConfigurationProducer extends RunConfigurationProducer<CeylonRunConfiguration> {

    protected CeylonRunConfigurationProducer() {
        super(findInstance(
                getExtensions(ConfigurationType.CONFIGURATION_TYPE_EP),
                CeylonRunConfigurationType.class
        ));
    }

    CeylonRunConfigurationProducer(ConfigurationType configurationType) {
        super(configurationType);
    }

    @Override
    protected boolean setupConfigurationFromContext(CeylonRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        IdeaCeylonProjects projects = context.getProject().getComponent(IdeaCeylonProjects.class);
        if (projects == null) {
            return false;
        }
        CeylonProject project = projects.getProject(context.getModule());
        if (project == null || !isBackendEnabled(project.getIdeConfiguration(), context)) {
            return false;
        }
        final RunConfigParams params = getRunConfigParams(sourceElement.get());

        if (params == null) {
            return false;
        }
        String pfx = StringUtil.isEmpty(params.pkg) ? "" : params.pkg + ".";
        final String topLevelNameFull = pfx + params.topLevel;
        configuration.setTopLevelNameFull(topLevelNameFull);
        configuration.setName(topLevelNameFull + " ➝ " + getBackend().nativeAnnotation);
        configuration.setCeylonModule(params.module);
        configuration.setBackend(getBackend());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(CeylonRunConfiguration rc, ConfigurationContext context) {
        RunConfigParams params = null;
        if (context.getLocation() != null) {
            params = getRunConfigParams(context.getLocation().getPsiElement());
        }

        IdeaCeylonProjects projects = context.getProject().getComponent(IdeaCeylonProjects.class);
        if (projects == null) {
            return false;
        }
        CeylonProject project = projects.getProject(context.getModule());

        return params != null
                && params.equals(new RunConfigParams(rc))
                && isBackendEnabled(project.getIdeConfiguration(), context);
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
                                final String packageName = cu.getUnit().getPackage().getNameAsString();
                                return new RunConfigParams(moduleName, packageName, identifier, getBackend());
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

    @NotNull
    Backend getBackend() {
        return Backend.Java;
    }

    boolean isBackendEnabled(CeylonIdeConfig config, ConfigurationContext context) {
        return config.getCompileToJvm() != null
                && config.getCompileToJvm().booleanValue()
                && isCompatibleWithModuleBackends(context);
    }

    boolean isCompatibleWithModuleBackends(ConfigurationContext context) {
        PsiElement location = context.getPsiLocation();

        if (location != null && location.getContainingFile() instanceof CeylonFile) {
            CeylonFile file = (CeylonFile) location.getContainingFile();

            if (file.getCompilationUnit() != null) {
                Unit unit = file.getCompilationUnit().getUnit();
                if (unit != null) {
                    Module mod = unit.getPackage().getModule();
                    return mod.getNativeBackends().none()
                            || mod.getNativeBackends().supports(getBackend());
                }
            }
        }
        return false;
    }

    static class RunConfigParams {
        final String module;
        final String pkg;
        final String topLevel;
        final Backend backend;

        RunConfigParams(String module, String pkg, String topLevel, Backend backend) {
            this.module = module;
            this.pkg = pkg;
            this.topLevel = topLevel;
            this.backend = backend;
        }

        public RunConfigParams(CeylonRunConfiguration rc) {
            module = rc.getCeylonModule();
            final String full = rc.getTopLevelNameFull();
            final int dot = full.lastIndexOf('.');
            topLevel = (dot < 0) ? full : full.substring(dot + 1);
            pkg = (dot < 0) ? "" : full.substring(0, dot);
            backend = rc.getBackend();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final RunConfigParams that = (RunConfigParams) o;
            return Objects.equals(this.module, that.module)
                    && Objects.equals(this.pkg, that.pkg)
                    && Objects.equals(this.topLevel, that.topLevel)
                    && Objects.equals(this.backend, that.backend);
        }

        @Override
        public int hashCode() {
            return Objects.hash(module, pkg, topLevel, backend);
        }
    }
}
