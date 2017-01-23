package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.util.Computable;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import org.intellij.plugins.ceylon.ide.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacetConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypeCheckerProvider implements ModuleComponent, ITypeCheckerProvider {

    private Module module;
    private IdeaCeylonProjects ceylonModel;

    public TypeCheckerProvider(Module module, IdeaCeylonProjects ceylonModel) {
        this.module = module;
        this.ceylonModel = ceylonModel;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "TypeCheckerProvider";
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void addFacetToModule(final Module module, @Nullable String jdkProvider,
                                 boolean forAndroid, boolean showSettings) {
        if (ceylonModel == null) {
            ceylonModel = module.getProject().getComponent(IdeaCeylonProjects.class);
            ceylonModel.addProject(module);
        }

        IdeaCeylonProject ceylonProject = (IdeaCeylonProject) ceylonModel.getProject(module);

        if (forAndroid) {
            ceylonProject.$setupForAndroid(jdkProvider);
        }

        ceylonProject.getConfiguration().save();

        CeylonFacet facet = CeylonFacet.forModule(module);
        if (facet == null) {
            facet = ApplicationManager.getApplication().runWriteAction(new Computable<CeylonFacet>() {
                @Override
                public CeylonFacet compute() {
                    return FacetManager.getInstance(module)
                            .addFacet(CeylonFacet.getFacetType(),
                                    CeylonFacet.getFacetType().getPresentableName(), null);
                }
            });
            facet.getConfiguration().setModule(module);
        }

        if (showSettings) {
            ModulesConfigurator.showFacetSettingsDialog(facet, CeylonFacetConfiguration.COMPILATION_TAB);
        }
    }

    @Override
    public void disposeComponent() {
        ceylonModel.removeProject(module);

        ceylonModel = null;
        module = null;
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void moduleAdded() {
        if (FacetManager.getInstance(module).getFacetByType(CeylonFacet.ID) == null) {
            return;
        }

        ceylonModel.addProject(module);
    }

    @Nullable
    public IdeaCeylonProject getCeylonProject() {
        return (IdeaCeylonProject) ceylonModel.getProject(module);
    }

    @Nullable
    @Override
    public TypeChecker getTypeChecker() {
        IdeaCeylonProject ceylonProject = getCeylonProject();
        if (ceylonProject == null) {
            return null;
        }
        return ceylonProject.getTypechecker();
    }
}
