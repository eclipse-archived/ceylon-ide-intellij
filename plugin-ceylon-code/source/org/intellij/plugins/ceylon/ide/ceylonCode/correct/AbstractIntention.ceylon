import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.editor {
    Editor,
    Document,
    AliasedAsTextEdit=TextChange
}
import com.intellij.openapi.\imodule {
    ModuleUtil
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    UsageWarning
}
import com.redhat.ceylon.ide.common.correct {
    GenericQuickFix
}
import com.redhat.ceylon.ide.common.platform {
    PlatformTextChange=TextChange
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.ide.common.typechecker {
    ModifiablePhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

abstract shared class AbstractIntention() extends BaseIntentionAction() {

    variable <TextChange|IdeaTextChange>? change = null;
    variable TextRange? selection = null;
    variable Boolean available = false;
    variable Anything(Project, Editor, PsiFile) callback = noop;
    
    value dummyMsg = UsageWarning(null, null, null);
    
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        if (is TextChange chg = change) {
            chg.apply(project);
        } else if (is IdeaTextChange chg = change) {
            chg.applyOnProject(project);
        }
        if (exists sel = selection) {
            editor.selectionModel.setSelection(sel.startOffset, sel.endOffset);
            editor.caretModel.moveToOffset(sel.endOffset);
        }
        callback(project, editor, psiFile);
    }
    
    shared actual Boolean isAvailable(Project project, Editor _editor, PsiFile psiFile) {
        available = false;
        callback = noop;

        if (is CeylonFile psiFile,
            is ModifiablePhasedUnit<out Anything,out Anything,out Anything,out Anything> u=psiFile.phasedUnit) {
            psiFile.ensureTypechecked();
            value offset = _editor.caretModel.offset;
            value _node = nodes.findNode(psiFile.compilationUnit,
                psiFile.tokens, offset);
            
            value mod = ModuleUtil.findModuleForFile(psiFile.virtualFile, project);
            
            if (exists _node,
                exists pr = project.getComponent(javaClass<IdeaCeylonProjects>()).getProject(mod)) {
                
                value data = object extends IdeaQuickFixData(
                    dummyMsg, 
                    psiFile.viewProvider.document,
                    psiFile.compilationUnit,
                    psiFile.phasedUnit,
                    _node,
                    mod,
                    null,
                    pr,
                    _editor
                ) {
                    shared actual void addQuickFix(String desc, PlatformTextChange|Anything() change,
                        DefaultRegion? selection, Boolean ignored) {
                        if (is IdeaTextChange change) {
                            makeAvailable(desc, change, selection);
                        }
                    }
                };

                try {
                    checkAvailable(data, psiFile, offset);
                } catch (Exception|AssertionError e) {
                    e.printStackTrace();
                }
            }
        }
        
        return available;
    }
    
    shared formal void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset);
    
    shared void makeAvailable(String desc, <TextChange|IdeaTextChange>? change = null,
        DefaultRegion? sel = null, 
        Anything callback(Project p, Editor e, PsiFile f) => noop) {
        
        setText(desc);
        available = true;
        this.change = change;
        this.callback = callback;

        if (exists sel) {
            selection = TextRange.from(sel.start, sel.length);
        } else {
            selection = null;
        }
    }
}

abstract shared class GenericIntention() 
        extends AbstractIntention()
        satisfies GenericQuickFix<CeylonFile,Document,InsertEdit,AliasedAsTextEdit,TextChange,TextRange,IdeaQuickFixData,LookupElement>
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    newProposal(IdeaQuickFixData data, String desc, TextChange change, DefaultRegion? region)
            => makeAvailable(desc, change, region);

}