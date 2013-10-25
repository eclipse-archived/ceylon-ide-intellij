package org.intellij.plugins.ceylon.psi.stub;

import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import org.intellij.plugins.ceylon.CeylonLanguage;

public interface CeylonStubTypes {

    IFileElementType COMPILATION_UNIT = new IStubFileElementType(CeylonLanguage.INSTANCE);
}