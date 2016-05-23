import com.redhat.ceylon.ide.common.platform {
    ModelServices,
    PlatformServices,
    VfsServices,
    CommonDocument,
    NoopLinkedMode
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}
import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    ideaCompletionServices
}

shared object ideaPlatformServices satisfies PlatformServices {
    
    shared actual ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>
            model<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaModelServices);
    
    utils() => ideaPlatformUtils;
    
    shared actual VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile> vfs<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaVfsServices);

    shared actual void gotoLocation(Unit unit, Integer offset, Integer length) {
        // TODO
    }
    
    document => ideaDocumentServices;
    completion => ideaCompletionServices;
    
    createLinkedMode(CommonDocument document)
            => if (is IdeaDocument document)
               then IdeaLinkedMode(document)
               else NoopLinkedMode(document);
}
