import com.intellij.codeInsight.intention {
    IntentionAction
}
import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.lang.annotation {
    Annotation
}
import com.intellij.openapi.editor {
    Document,
    Editor
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange,
    Iconable
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Message,
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.correct {
    IdeQuickFixManager,
    ImportProposals,
    AddAnnotationQuickFix,
    RemoveAnnotationQuickFix,
    QuickFixData,
    CreateQuickFix,
    ChangeReferenceQuickFix,
    DeclareLocalQuickFix,
    CreateEnumQuickFix,
    RefineFormalMembersQuickFix,
    SpecifyTypeQuickFix,
    ExportModuleImportQuickFix,
    AddPunctuationQuickFix,
    AddParameterListQuickFix,
    AddParameterQuickFix,
    AddInitializerQuickFix,
    AddConstructorQuickFix
}

import java.lang {
    Comparable
}
import java.util {
    Collection
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    IdeaLinkedMode
}
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlightProposal
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

shared object ideaQuickFixManager
        extends IdeQuickFixManager<Document,InsertEdit,TextEdit,TextChange,TextRange,Module,CeylonFile,LookupElement,IdeaQuickFixData,IdeaLinkedMode>
        () {

    
    shared actual AddAnnotationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> addAnnotations
            => addRemoveAnnotationIntention;
    shared actual RemoveAnnotationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> removeAnnotations
            => addRemoveAnnotationIntention;
    shared actual ImportProposals<CeylonFile,LookupElement,Document,InsertEdit,TextEdit,TextChange> importProposals
            => ideaImportProposals;
    shared actual CreateQuickFix<CeylonFile,Module,Document,InsertEdit,TextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement> createQuickFix
            => ideaCreateQuickFix;
    shared actual CreateEnumQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> createEnumQuickFix
            => ideaCreateEnumIntention;
    
    shared actual void addImportProposals(Collection<LookupElement> proposals, IdeaQuickFixData data) {
        for (proposal in proposals) {
            assert(is TextChange change = proposal.\iobject);
            data.registerFix(proposal.lookupString, change, null, ideaIcons.singleImport, true);
        }
    }
    
    shared actual ChangeReferenceQuickFix<CeylonFile,Module,Document,InsertEdit,TextEdit,TextChange,IdeaQuickFixData,TextRange,LookupElement> changeReferenceQuickFix
            => ideaChangeReferenceQuickFix;
    
    shared actual DeclareLocalQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,IdeaLinkedMode,LookupElement,Module,IdeaQuickFixData,TextRange> declareLocalQuickFix
            => ideaDeclareLocalQuickFix;

    shared actual RefineFormalMembersQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> refineFormalMembersQuickFix 
            => ideaRefineFormalMembersQuickFix;
    
    shared actual SpecifyTypeQuickFix<CeylonFile,Document,InsertEdit,TextEdit,
        TextChange,TextRange,Module,IdeaQuickFixData,LookupElement,IdeaLinkedMode>
            specifyTypeQuickFix => ideaSpecifyTypeQuickFix;
        
    shared actual void addCreateTypeParameterProposal<Data>(Data data,
        Tree.BaseType bt, String brokenName)
            given Data satisfies QuickFixData<Module> {
        // TODO
    }

    shared actual ExportModuleImportQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> exportModuleImportQuickFix
            => ideaExportModuleImportQuickFix;
    
    shared actual AddPunctuationQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> addPunctuationQuickFix 
            => ideaAddPunctuationQuickFix;
    
    shared actual AddParameterListQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> addParameterListQuickFix
            => ideaAddParameterListQuickFix;
    
    shared actual AddParameterQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> addParameterQuickFix
            => ideaAddParameterQuickFix;
    
    shared actual AddInitializerQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> addInitializerQuickFix
            => ideaAddInitializerQuickFix;
    
    shared actual AddConstructorQuickFix<CeylonFile,Document,InsertEdit,TextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> addConstructorQuickFix
            => ideaAddConstructorQuickFix;
}

class CustomIntention(Integer position, String desc, TextChange? change, TextRange? selection = null, Icon? image = null,
    Boolean qualifiedNameIsPath = false, Anything callback(Project project, Editor editor, PsiFile psiFile) => noop)
        extends BaseIntentionAction()
        satisfies Iconable & Comparable<IntentionAction> {
    
    shared actual String familyName => "Ceylon Intentions";
    variable Project? project = null;
    
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        if (exists change) {
            change.apply(project);
        }
        if (exists selection) {
            editor.selectionModel.setSelection(selection.startOffset, selection.endOffset);
            editor.caretModel.moveToOffset(selection.endOffset);
        }
        callback(project, editor, psiFile);
    }
    
    shared actual Boolean isAvailable(Project project, Editor? editor, PsiFile? psiFile) {
        this.project = project;
        return true;
    }
    
    shared actual String text => if (exists p = project) then highlightProposal(desc, p, qualifiedNameIsPath) else desc;
    
    shared actual Icon? getIcon(Integer int) => image;
    
    shared actual Boolean equals(Object that) {
        if (is CustomIntention that) {
            return desc==that.desc && 
                    qualifiedNameIsPath==that.qualifiedNameIsPath;
        }
        else {
            return false;
        }
    }
    
    shared actual Integer compareTo(IntentionAction? t) {
        if (is CustomIntention t) {
            return position - t.position;
        }
        return 0;
    }
}

shared class IdeaQuickFixData(Message message, shared Document doc,
    shared actual Tree.CompilationUnit rootNode,
    shared actual Node node,
    shared actual Module project,
    shared Annotation? annotation) satisfies QuickFixData<Module> {
    
    shared actual Integer errorCode => message.code;
    shared actual Integer problemOffset => annotation?.startOffset else 0;
    
    shared void registerFix(String desc, TextChange? change, TextRange? selection = null, Icon? image = null,
        Boolean qualifiedNameIsPath = false, Anything callback(Project project, Editor editor, PsiFile psiFile) => noop) {
        
        assert(exists annotation);
        value position = annotation.quickFixes?.size() else 0;
        IntentionAction intention = CustomIntention(position, desc, change, 
            selection, image, qualifiedNameIsPath, callback);
        annotation.registerFix(intention);
    }
}
