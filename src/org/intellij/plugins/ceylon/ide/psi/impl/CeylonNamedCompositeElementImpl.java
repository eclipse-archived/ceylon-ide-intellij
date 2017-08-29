package org.intellij.plugins.ceylon.ide.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiNamedElement;

/**
 * Resolves inheritance ambiguities between PsiElementBase, PsiNamedElement and NavigationItem.
 * @see CeylonNamedTypePsiImpl
 */
public abstract class CeylonNamedCompositeElementImpl
        extends CeylonCompositeElementImpl
        implements PsiNamedElement {

    public CeylonNamedCompositeElementImpl(ASTNode node) {
        super(node);
    }
}
