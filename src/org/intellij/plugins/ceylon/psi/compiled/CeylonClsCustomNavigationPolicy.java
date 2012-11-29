package org.intellij.plugins.ceylon.psi.compiled;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.compiled.ClsClassImpl;
import com.intellij.psi.impl.compiled.ClsCustomNavigationPolicy;
import com.intellij.psi.impl.compiled.ClsFieldImpl;
import com.intellij.psi.impl.compiled.ClsMethodImpl;
import com.intellij.psi.search.GlobalSearchScope;
import org.intellij.plugins.ceylon.psi.CeylonClass;
import org.intellij.plugins.ceylon.psi.stub.ClassIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CeylonClsCustomNavigationPolicy implements ClsCustomNavigationPolicy {
    @Nullable
    @Override
    public PsiElement getNavigationElement(@NotNull ClsClassImpl clsClass) {
        Collection<CeylonClass> classes = ClassIndex.getInstance().get(clsClass.getQualifiedName(), clsClass.getProject(), GlobalSearchScope.allScope(clsClass.getProject()));
        if (classes.iterator().hasNext()) {
            return classes.iterator().next().getNameIdentifier();
        }
        // TODO module.ceylon, package.ceylon etc (need to be indexed?)
        return null;
    }

    @Nullable
    @Override
    public PsiElement getNavigationElement(@NotNull ClsMethodImpl clsMethod) {
        return null; // TODO
    }

    @Nullable
    @Override
    public PsiElement getNavigationElement(@NotNull ClsFieldImpl clsField) {
        return null; // TODO
    }
}
