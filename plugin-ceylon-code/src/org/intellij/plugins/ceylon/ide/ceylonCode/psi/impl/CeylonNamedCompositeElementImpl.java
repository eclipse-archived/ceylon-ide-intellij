package org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiNamedElement;

/**
 * Created by gavin on 7/6/16.
 */
public abstract class CeylonNamedCompositeElementImpl
        extends CeylonCompositeElementImpl
        implements PsiNamedElement {

    public CeylonNamedCompositeElementImpl(ASTNode node) {
        super(node);
    }
}
