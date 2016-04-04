import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.ide.common.platform {
    ModelServices,
    PlatformServices,
    VfsServices
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast,
    Indents
}

import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    ideaImportProposals
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIndents
}

shared object ideaPlatformServices satisfies PlatformServices {
    
    shared actual ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>
            model<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaModelServices);
    
    utils() => ideaPlatformUtils;
    
    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange> 
    importProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange>()
            => unsafeCast<ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange>>(ideaImportProposals);

    shared actual VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile> vfs<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaVfsServices);

    shared actual Indents<IDocument> indents<IDocument>()
            => unsafeCast<Indents<IDocument>>(ideaIndents);
}
