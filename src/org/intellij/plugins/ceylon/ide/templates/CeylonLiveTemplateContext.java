package org.intellij.plugins.ceylon.ide.templates;

import com.intellij.codeInsight.template.FileTypeBasedContextType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;

public class CeylonLiveTemplateContext extends FileTypeBasedContextType {

    protected CeylonLiveTemplateContext() {
        super("CEYLON", "&Ceylon", CeylonFileType.INSTANCE);
    }
}
