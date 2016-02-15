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
    QuickFixData
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
        extends IdeQuickFixManager<Document,InsertEdit,TextEdit,TextChange,TextRange,Module,CeylonFile,LookupElement,IdeaQuickFixData,IdeaLinkedMode>() {
    
    addAnnotations => ideaAddRemoveAnnotationQuickFix;
    removeAnnotations => ideaAddRemoveAnnotationQuickFix;
    importProposals => ideaImportProposals;
    createQuickFix => ideaCreateQuickFix;
    createEnumQuickFix => ideaCreateEnumQuickFix;
    changeReferenceQuickFix => ideaChangeReferenceQuickFix;
    declareLocalQuickFix => ideaDeclareLocalQuickFix;
    refineFormalMembersQuickFix => ideaRefineFormalMembersQuickFix;
    specifyTypeQuickFix => ideaSpecifyTypeQuickFix;
    exportModuleImportQuickFix => ideaExportModuleImportQuickFix;
    addPunctuationQuickFix => ideaAddPunctuationQuickFix;
    addParameterListQuickFix => ideaAddParameterListQuickFix;
    addParameterQuickFix => ideaAddParameterQuickFix;
    addInitializerQuickFix => ideaAddInitializerQuickFix;
    addConstructorQuickFix => ideaAddConstructorQuickFix;
    changeDeclarationQuickFix => ideaChangeDeclarationQuickFix;
    fixAliasQuickFix => ideaFixAliasQuickFix;
    
    shared actual void addImportProposals(Collection<LookupElement> proposals, IdeaQuickFixData data) {
        for (proposal in proposals) {
            assert (is TextChange change = proposal.\iobject);
            data.registerFix(proposal.lookupString, change, null, ideaIcons.singleImport, true);
        }
    }
    
    shared actual void addCreateTypeParameterProposal<Data>(Data data,
        Tree.BaseType bt, String brokenName)
            given Data satisfies QuickFixData<Module> {
        // TODO
    }
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
        } else {
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
        
        assert (exists annotation);
        value position = annotation.quickFixes?.size() else 0;
        IntentionAction intention = CustomIntention(position, desc, change,
            selection, image, qualifiedNameIsPath, callback);
        annotation.registerFix(intention);
    }
}
