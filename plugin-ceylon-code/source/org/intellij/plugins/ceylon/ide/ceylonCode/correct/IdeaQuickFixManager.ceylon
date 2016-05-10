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
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
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
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    PlatformTextChange=TextChange
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
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
    appendMemberReferenceQuickFix => ideaAppendMemberReferenceQuickFix;
    changeTypeQuickFix => ideaChangeTypeQuickFix;
    addSatisfiesQuickFix => ideaAddSatisfiesQuickFix;
    addSpreadToVariadicParameterQuickFix => ideaAddSpreadToVariadicParameterQuickFix;
    addTypeParameterQuickFix => ideaAddTypeParameterQuickFix;
    shadowReferenceQuickFix => ideaShadowReferenceQuickFix;
    changeInitialCaseQuickFix => ideaChangeInitialCaseQuickFix;
    fixMultilineStringIndentationQuickFix => ideaFixMultilineStringIndentationQuickFix;
    addModuleImportQuickFix => ideaAddModuleImportQuickFix;
    renameDescriptorQuickFix => ideaRenameDescriptorQuickFix;
    changeRefiningTypeQuickType => ideaChangeRefiningTypeQuickType;
    switchQuickFix => ideaSwitchQuickFix;
    changeToQuickFix => ideaChangeToQuickFix;
    addNamedArgumentQuickFix => ideaAddNamedArgumentQuickFix;
    assignToLocalQuickFix => ideaAssignToLocalQuickFix;
    
    shared actual void addImportProposals(Collection<LookupElement> proposals, IdeaQuickFixData data) {
        for (proposal in proposals) {
            assert (is TextChange change = proposal.\iobject);
            data.registerFix(proposal.lookupString, change, null, ideaIcons.singleImport, true);
        }
    }
    
    shared actual void addCreateTypeParameterProposal<Data>(Data data,
        Tree.BaseType bt, String brokenName)
            given Data satisfies QuickFixData {
        // TODO
    }
}

class CustomIntention(Integer position, String desc, <TextChange|PlatformTextChange>? change, TextRange? selection = null, Icon? image = null,
    Boolean qualifiedNameIsPath = false, Anything callback(Project project, Editor editor, PsiFile psiFile) => noop)
        extends BaseIntentionAction()
        satisfies Iconable & Comparable<IntentionAction> {
    
    shared actual String familyName => "Ceylon Intentions";
    variable Project? project = null;
    
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        if (is TextChange change) {
            change.apply(project);
        } else if (is IdeaTextChange change) {
            change.applyOnProject(project);
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

shared class IdeaQuickFixData(
    Message message,
    shared Document nativeDoc,
    shared actual Tree.CompilationUnit rootNode,
    shared actual PhasedUnit phasedUnit,
    shared actual Node node,
    shared Module project,
    shared Annotation? annotation,
    shared actual BaseCeylonProject ceylonProject,
    shared variable Editor? editor = null
) satisfies QuickFixData {
    
    errorCode => message.code;
    problemOffset => annotation?.startOffset else 0;
    problemLength => (annotation?.endOffset else 0) - problemOffset;
    
    shared void registerFix(String desc, <TextChange|PlatformTextChange>? change, TextRange? selection = null, Icon? image = null,
        Boolean qualifiedNameIsPath = false, Anything callback(Project project, Editor editor, PsiFile psiFile) => noop) {
        
        assert (exists annotation);
        value position = annotation.quickFixes?.size() else 0;
        IntentionAction intention = CustomIntention(position, desc, change,
            selection, image, qualifiedNameIsPath, callback);
        annotation.registerFix(intention);
    }

    doc = DocumentWrapper(nativeDoc);

    shared actual default void addQuickFix(String desc, PlatformTextChange change, DefaultRegion? selection) {
        value range = if (exists selection)
                      then TextRange.from(selection.start, selection.length)
                      else null;

        if (is IdeaTextChange change) {
            registerFix(desc, change, range, ideaIcons.correction);
        }
    }
}
