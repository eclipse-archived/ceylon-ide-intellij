package org.intellij.plugins.ceylon.ide.psi.stub;

import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import org.intellij.plugins.ceylon.ide.CeylonLanguage;

public interface CeylonStubTypes {

    IFileElementType CEYLON_FILE = new IStubFileElementType(CeylonLanguage.INSTANCE);
}