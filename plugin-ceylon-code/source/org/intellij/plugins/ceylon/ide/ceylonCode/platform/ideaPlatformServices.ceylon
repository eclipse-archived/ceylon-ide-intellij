import com.redhat.ceylon.ide.common.util {
    unsafeCast
}
import com.redhat.ceylon.ide.common.platform {
    ModelServices,
    PlatformServices
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    ideaImportProposals
}

shared object ideaPlatformServices satisfies PlatformServices {
    
    shared actual ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>
            model<NativeProject, NativeResource, NativeFolder, NativeFile>()
            => unsafeCast<ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(ideaModelServices);
    
    utils() => ideaPlatformUtils;
    
    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange> 
    importProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange>()
            => unsafeCast<ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange>>(ideaImportProposals);
}