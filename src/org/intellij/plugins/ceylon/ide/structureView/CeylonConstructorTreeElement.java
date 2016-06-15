package org.intellij.plugins.ceylon.ide.structureView;

import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.util.icons_;

import javax.swing.*;

class CeylonConstructorTreeElement extends CeylonDeclarationTreeElement<CeylonPsi.ConstructorPsi> {

    CeylonConstructorTreeElement(CeylonPsi.ConstructorPsi psiElement) {
        super(psiElement, false);
    }

}
