package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import com.redhat.ceylon.compiler.typechecker.tree.Node;

public interface CeylonCompositeElement extends PsiElement, NavigationItem {

    Node getCeylonNode();
}
