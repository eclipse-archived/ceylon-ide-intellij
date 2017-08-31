import com.intellij.codeInsight.template {
    FileTypeBasedContextType
}
import com.intellij.codeInsight.template.impl {
    DefaultLiveTemplatesProvider
}

import java.lang {
    ObjectArray,
    Types
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonFileType
}

shared class CeylonLiveTemplateContext()
        extends FileTypeBasedContextType("CEYLON", "&Ceylon", CeylonFileType.instance) {}


shared class CeylonLiveTemplatesProvider()
        satisfies DefaultLiveTemplatesProvider {

    value files = ["/liveTemplates/CeylonLiveTemplates"];

    defaultLiveTemplateFiles = ObjectArray.with(files.map(Types.nativeString));

    hiddenLiveTemplateFiles => null;

}
