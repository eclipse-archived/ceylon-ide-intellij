package org.intellij.plugins.ceylon.ide.psi.stub;

import com.intellij.psi.tree.IFileElementType;
import org.intellij.plugins.ceylon.ide.CeylonLanguage;
import org.intellij.plugins.ceylon.ide.psi.CeylonStubFileElementType;

public interface CeylonStubTypes {

    IFileElementType CEYLON_FILE = new CeylonStubFileElementType(CeylonLanguage.INSTANCE);
}