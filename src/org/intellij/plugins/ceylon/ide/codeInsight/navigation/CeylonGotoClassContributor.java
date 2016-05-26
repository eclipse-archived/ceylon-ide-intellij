package org.intellij.plugins.ceylon.ide.codeInsight.navigation;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.MultiMap;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.JavaUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Package;
import org.intellij.plugins.ceylon.ide.ceylonCode.codeInsight.navigation.DeclarationNavigationItem;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaModule;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonClass;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.intellij.util.containers.ContainerUtilRt.newArrayList;

public class CeylonGotoClassContributor implements GotoClassContributor {

    private static final Logger LOGGER = Logger.getInstance(CeylonGotoClassContributor.class);
    private MultiMap<String, Declaration> declarationMap = MultiMap.createSet();

    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        declarationMap.clear();

        long nano = System.nanoTime();

        IdeaCeylonProjects ceylonProjects = project.getComponent(IdeaCeylonProjects.class);

        if (ceylonProjects == null) {
            return new String[0];
        }

        for (Module module : ModuleManager.getInstance(project).getModules()) {
            IdeaCeylonProject ceylonProject = (IdeaCeylonProject) ceylonProjects.getProject(module);

            if (ceylonProject == null) {
                continue;
            }

            CeylonProject.Modules modules = ceylonProject.getModules();

            if (modules == null) {
                continue;
            }

            // The global model might get updated while we are iterating declarations, so
            // we should make defensive copies to avoid ConcurrentModificationException
            Set<com.redhat.ceylon.model.typechecker.model.Module> moduleList =
                    modules.getTypecheckerModules().getListOfModules();

            for (com.redhat.ceylon.model.typechecker.model.Module m : moduleList) {
                if (m instanceof IdeaModule) {
                    if (!includeNonProjectItems && !((IdeaModule) m).getIsProjectModule()) {
                        continue;
                    }

                    for (Package pack : newArrayList(m.getPackages())) {
                        for (Declaration declaration : newArrayList(pack.getMembers())) {
                            if (declaration.getName() == null) {
                                continue;
                            }
                            if (!(declaration.getUnit() instanceof JavaUnit)
                                    && includeDeclaration((IdeaModule) m, declaration)) {
                                declarationMap.putValue(declaration.getName(), declaration);
                            }
                        }
                    }
                }
            }
        }

        LOGGER.debug("getNames in " + (System.nanoTime() - nano) + "ns");

        Set<String> names = declarationMap.keySet();
        return names.toArray(new String[names.size()]);
    }

    // TODO copied from Eclipse's OpenDeclarationDialog
    private boolean includeDeclaration(IdeaModule module,
                                       Declaration dec) {
        try {
            boolean visibleFromSourceModules;
            if (dec.isToplevel()) {
                visibleFromSourceModules =
                        dec.isShared() ||
                                module.getIsProjectModule();
            }
            else {
                visibleFromSourceModules = dec.isShared();
            }
            return visibleFromSourceModules &&
                    isPresentable(dec) /*&&
                    !isFiltered(dec)*/;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isPresentable(Declaration d) {
        String name = d.getName();
        return name!=null &&
                !d.isAnonymous() &&
                !isOverloadedVersion(d);
    }

    private boolean isOverloadedVersion(Declaration decl) {
        return decl!=null &&
                (decl.isOverloaded() &&
                        !decl.isAbstraction());
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        if (declarationMap.containsKey(name)) {
            Set<NavigationItem> elements = new HashSet<>();

            for (Declaration decl : declarationMap.get(name)) {
                elements.add(new DeclarationNavigationItem(decl, project));
            }

            return elements.toArray(new NavigationItem[elements.size()]);
        }

        return new NavigationItem[0];
    }

    @Nullable
    @Override
    public String getQualifiedName(NavigationItem item) {
        if (item instanceof CeylonClass) {
            return ((CeylonClass) item).getQualifiedName();
        } else if (item instanceof CeylonPsi.DeclarationPsi) {
            Tree.Declaration node = ((CeylonPsi.DeclarationPsi) item).getCeylonNode();
            if (node.getDeclarationModel() != null) {
                return node.getDeclarationModel().getQualifiedNameString();
            }
            return node.getIdentifier().getText();
        } else if (item instanceof CeylonCompositeElement) {
            LOGGER.error("Couldn't get qualified name for item " + item.getClass().getName());
        }

        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return ".";
    }

}
