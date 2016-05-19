import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.ide.common.platform {
    ModelServices,
    PlatformServices,
    VfsServices,
    CommonDocument
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast,
    Indents
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}

shared object ideaPlatformServices satisfies PlatformServices {
    
    shared actual ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>
            model<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaModelServices);
    
    utils() => ideaPlatformUtils;
    
    shared actual VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile> vfs<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaVfsServices);

    shared actual Indents<IDocument> indents<IDocument>()
            => unsafeCast<Indents<IDocument>>(ideaIndents);

    createTextChange(String desc, CommonDocument|PhasedUnit input) => IdeaTextChange(input);

    createCompositeChange(String desc) => IdeaCompositeChange();

    shared actual void gotoLocation(Unit unit, Integer offset, Integer length) {
        // TODO
    }

}
