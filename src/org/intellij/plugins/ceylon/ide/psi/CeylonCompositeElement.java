package org.intellij.plugins.ceylon.ide.psi;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;

public interface CeylonCompositeElement extends PsiElement, NavigationItem {

    Node getCeylonNode();
}
