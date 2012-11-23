package org.intellij.plugins.ceylon.codeInsight.navigation;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import org.intellij.plugins.ceylon.psi.CeylonClassDeclaration;
import org.intellij.plugins.ceylon.psi.CeylonTypeNameDeclaration;
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
        Collection<CeylonClassDeclaration> classes = ClassIndex.getInstance().get(name, project, GlobalSearchScope.projectScope(project));

        NavigationItem[] result = new NavigationItem[classes.size()];
        int i = 0;

        for (CeylonClassDeclaration aClass : classes) {
            result[i++] = aClass.getTypeNameDeclaration(); // TODO getPresentation()
        }

        return result;
    }

    @Nullable
    @Override
    public String getQualifiedName(NavigationItem item) {
        if (item instanceof CeylonTypeNameDeclaration) {
            return ((CeylonTypeNameDeclaration) item).getText();
        }

        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return ".";
    }
}
