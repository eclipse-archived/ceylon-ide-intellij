package org.intellij.plugins.ceylon.ide.codeInsight;

import java.util.Collection;
import java.util.List;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.psi.PsiElement;

// TODO remove me once this is fixed: https://github.com/ceylon/ceylon/issues/2424
public abstract class MyLineMarkerProvider implements LineMarkerProvider {

    @Override
    public final void collectSlowLineMarkers(List<PsiElement> elements,
            Collection<LineMarkerInfo> result) {
        collectLineMarkers(elements, (Collection) result);
    }

    public void collectLineMarkers(List<PsiElement> elements,  Collection<LineMarkerInfo<? extends PsiElement>> result) {}
}
