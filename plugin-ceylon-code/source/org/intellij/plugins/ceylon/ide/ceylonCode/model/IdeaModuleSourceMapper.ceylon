import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.redhat.ceylon.ide.common.model {
    BaseIdeModule,
    IdeModuleSourceMapper
}
import com.redhat.ceylon.compiler.typechecker.context {
    Context
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status
}

shared class IdeaModuleSourceMapper(
    Context context, IdeaModuleManager moduleManager)
        extends IdeModuleSourceMapper
        <Module,VirtualFile,VirtualFile,VirtualFile>(context, moduleManager) {
    
    shared actual String defaultCharset
            => moduleManager.ceylonProject?.defaultCharset else "UTF-8";
    
    shared actual void logModuleResolvingError(BaseIdeModule mod, Exception e) {
        platformUtils.log(Status._ERROR,
            "Failed to resolve module " + mod.signature, e);
    }
}
