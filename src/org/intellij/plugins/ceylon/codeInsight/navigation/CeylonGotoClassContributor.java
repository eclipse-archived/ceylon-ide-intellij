package org.intellij.plugins.ceylon.codeInsight.navigation;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.CeylonTypeName;
import org.intellij.plugins.ceylon.psi.stub.ClassIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CeylonGotoClassContributor implements GotoClassContributor {
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        Collection<String> allKeys = ClassIndex.getInstance().getAllKeys(project);
        return allKeys.toArray(new String[allKeys.size()]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        Collection<CeylonClass> declarations = ClassIndex.getInstance().get(name, project, GlobalSearchScope.projectScope(project));

        NavigationItem[] result = new NavigationItem[declarations.size()];
        int i = 0;

        for (CeylonClass decl : declarations) {
            result[i++] = (NavigationItem) decl.getNameIdentifier();
        }

        return result;
    }

    @Nullable
    @Override
    public String getQualifiedName(NavigationItem item) {
        if (item instanceof CeylonTypeName) {
            return ((CeylonTypeName) item).getText();
        }

        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return ".";
    }
}
