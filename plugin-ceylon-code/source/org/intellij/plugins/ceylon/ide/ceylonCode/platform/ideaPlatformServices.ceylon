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
    Unit,
    Type
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    ideaCompletionManager
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

    // TODO this method is temporary, until completionManager becomes an object in ide-common!
    shared actual Anything getTypeProposals(CommonDocument document,
        Integer offset, Integer length, Type infType,
        Tree.CompilationUnit rootNode, String? kind) {

        assert(is IdeaDocument document);
        return ideaCompletionManager.getTypeProposals(document, offset,
            length, infType, rootNode, kind);
    }

}
