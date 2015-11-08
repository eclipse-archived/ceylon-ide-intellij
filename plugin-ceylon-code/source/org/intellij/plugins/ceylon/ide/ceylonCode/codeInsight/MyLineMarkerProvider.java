package org.intellij.plugins.ceylon.ide.ceylonCode.codeInsight;

import java.util.Collection;
import java.util.List;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.psi.PsiElement;

// TODO remove me once this is fixed: https://github.com/ceylon/ceylon-compiler/issues/2424
public abstract class MyLineMarkerProvider implements LineMarkerProvider {

    @Override
    public void collectSlowLineMarkers(List<PsiElement> elements,
            Collection<LineMarkerInfo> result) {
        
    }
}
