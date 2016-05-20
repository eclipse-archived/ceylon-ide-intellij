import com.intellij.psi.codeStyle {
    CodeStyleSettings
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
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

shared object ideaPlatformServices satisfies PlatformServices {
    
    shared actual ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>
            model<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaModelServices);
    
    utils() => ideaPlatformUtils;
    
    shared actual VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile> vfs<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaVfsServices);

    createTextChange(String desc, CommonDocument|PhasedUnit input) => IdeaTextChange(input);

    createCompositeChange(String desc) => IdeaCompositeChange();

    shared actual void gotoLocation(Unit unit, Integer offset, Integer length) {
        // TODO
    }
    
    // TODO take the settings from the current project
    indentSpaces => CodeStyleSettings().indentOptions.indentSize;
    
    indentWithSpaces => true;

    createLinkedMode(CommonDocument document)
            => if (is IdeaDocument document)
               then IdeaLinkedMode(document)
               else NoopLinkedMode(document);
}
