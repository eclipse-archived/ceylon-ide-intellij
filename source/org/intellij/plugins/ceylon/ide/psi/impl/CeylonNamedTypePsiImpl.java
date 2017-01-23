package org.intellij.plugins.ceylon.ide.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiNamedElement;
import org.intellij.plugins.ceylon.ide.psi.CeylonPsiImpl;

/**
 * Resolves inheritance ambiguities:
 *
 * - may not inherit two declarations with the same name unless redefined in subclass: 'name' is defined by supertypes 'PsiElementBase' and 'PsiNamedElement'
 * - may not inherit two declarations with the same name that do not share a common supertype: 'name' is defined by supertypes 'PsiNamedElement' and 'NavigationItem'
 */
public abstract class CeylonNamedTypePsiImpl extends CeylonPsiImpl.TypePsiImpl
    implements PsiNamedElement {

    public CeylonNamedTypePsiImpl(ASTNode astNode) {
        super(astNode);
    }
}
