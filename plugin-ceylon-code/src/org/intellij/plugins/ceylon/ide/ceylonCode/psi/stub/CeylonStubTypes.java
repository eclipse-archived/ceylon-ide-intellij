package org.intellij.plugins.ceylon.ide.ceylonCode.psi.stub;

import com.intellij.psi.tree.IFileElementType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonStubFileElementType;

public interface CeylonStubTypes {

    IFileElementType CEYLON_FILE = new CeylonStubFileElementType(CeylonLanguage.INSTANCE);
}