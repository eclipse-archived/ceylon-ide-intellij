package org.intellij.plugins.ceylon.ide.annotator;

import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.facet.CeylonFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.facet.FacetManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.ide.common.typechecker.IdePhasedUnit;

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
    public static TypeChecker getFor(PsiElement element) {
        if (element.getContainingFile() instanceof CeylonFile) {
            CeylonFile ceylonFile = (CeylonFile) element.getContainingFile();

            if (ceylonFile.getPhasedUnit() instanceof IdePhasedUnit) {
                IdePhasedUnit phasedUnit = (IdePhasedUnit) ceylonFile.getPhasedUnit();
                return phasedUnit.getTypeChecker();
            }

            //LOGGER.warn("CeylonFile has no IdePhasedUnit: " + ceylonFile.getVirtualFile().getCanonicalPath());

            // TODO .ceylon files loaded from .src archives don't belong to any module, what should we do?
            Module module = ModuleUtil.findModuleForFile(ceylonFile.getVirtualFile(), ceylonFile.getProject());

            if (module != null) {
                ITypeCheckerProvider provider = module.getComponent(ITypeCheckerProvider.class);
                return provider.getTypeChecker();
            }
        }

        return null;
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
