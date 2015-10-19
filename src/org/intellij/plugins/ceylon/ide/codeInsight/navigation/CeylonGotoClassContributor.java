package org.intellij.plugins.ceylon.ide.codeInsight.navigation;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.MultiMap;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.*;
import com.redhat.ceylon.model.typechecker.model.Package;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonClass;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.intellij.plugins.ceylon.ide.ceylonCode.resolve.CeylonReference.resolveDeclaration;

public class CeylonGotoClassContributor implements GotoClassContributor {

    MultiMap<String, Declaration> declarationMap = MultiMap.createSet();

    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        declarationMap.clear();

        long nano = System.nanoTime();

        for (Module module : ModuleManager.getInstance(project).getModules()) {
            TypeChecker tc = getTypeChecker(module);
            if (tc == null) {
                continue;
            }

            for (PhasedUnit phasedUnit : tc.getPhasedUnits().getPhasedUnits()) {
                for (Declaration declaration : phasedUnit.getDeclarations()) {
                    if (declaration.getName() == null) {
                        continue;
                    }
                    if (declaration instanceof ClassOrInterface) {
                        declarationMap.putValue(declaration.getName(), declaration);
                    }
                    if (declaration instanceof Function) {
                        declarationMap.putValue(declaration.getName(), declaration);
                    }
                }
            }

            if (includeNonProjectItems) {
                for (com.redhat.ceylon.model.typechecker.model.Module m : tc.getContext().getModules().getListOfModules()) {
                    for (Package pack : m.getPackages()) {
                        for (Declaration declaration : pack.getMembers()) {
                            if (declaration.getName() == null) {
                                continue;
                            }
                            if (declaration instanceof ClassOrInterface) {
                                declarationMap.putValue(declaration.getName(), declaration);
                            }
                            if (declaration instanceof Function) {
                                declarationMap.putValue(declaration.getName(), declaration);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("getNames in " + (System.nanoTime() - nano) + "ns");
        Set<String> names = declarationMap.keySet();
        return names.toArray(new String[names.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        if (declarationMap.containsKey(name)) {
            Set<CeylonCompositeElement> elements = new HashSet<>();

            // TODO this loop is ugly
            for (Module module : ModuleManager.getInstance(project).getModules()) {
                TypeChecker tc = getTypeChecker(module);
                if (tc == null) {
                    continue;
                }
                for (Declaration decl : declarationMap.get(name)) {
                    elements.add(resolveDeclaration(decl, tc, project));
                }
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
        }

        Logger.getInstance(CeylonGotoClassContributor.class).error("Couldn't get qualified name for item " + item.getClass().getName());

        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return ".";
    }

    @Nullable
    private TypeChecker getTypeChecker(Module module) {
        ITypeCheckerProvider provider = module.getComponent(ITypeCheckerProvider.class);
        if (provider == null) {
            return null;
        }

        return provider.getTypeChecker();
    }
}
