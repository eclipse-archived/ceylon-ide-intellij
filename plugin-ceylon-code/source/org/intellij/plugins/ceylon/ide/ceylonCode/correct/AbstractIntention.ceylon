import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.openapi.editor {
    Editor,
    Document,
    AliasedAsTextEdit=TextChange
}
import com.intellij.openapi.\imodule {
    ModuleUtil,
    Module
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
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
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
import com.redhat.ceylon.ide.common.correct {
    GenericQuickFix
}
import com.intellij.codeInsight.lookup {
    LookupElement
}

abstract shared class AbstractIntention() extends BaseIntentionAction() {

    variable TextChange? change = null;
    variable TextRange? selection = null;
    variable Boolean available = false;
    variable Anything(Project, Editor, PsiFile) callback = noop;
    
    value dummyMsg = UsageWarning(null, null, null);
    
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        if (exists chg = change) {
            chg.apply(project);
        }
        if (exists sel = selection) {
            editor.selectionModel.setSelection(sel.startOffset, sel.endOffset);
            editor.caretModel.moveToOffset(sel.endOffset);
        }
        callback(project, editor, psiFile);
    }
    
    shared actual Boolean isAvailable(Project project, Editor editor, PsiFile psiFile) {
        available = false;

        if (is CeylonFile psiFile) {
            psiFile.ensureTypechecked();
            value offset = editor.caretModel.offset;
            value node = nodes.findNode(psiFile.compilationUnit, 
                psiFile.tokens, offset);
            
            value mod = ModuleUtil.findModuleForFile(psiFile.virtualFile, project);
            
            if (exists node,
                exists pr = project.getComponent(javaClass<IdeaCeylonProjects>()).getProject(mod)) {
                
                value data = IdeaQuickFixData(
                    dummyMsg, 
                    psiFile.viewProvider.document,
                    psiFile.compilationUnit, node,
                    mod,
                    null,
                    pr
                );

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
    
    shared void makeAvailable(String desc, TextChange? change = null,
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
        satisfies GenericQuickFix<CeylonFile,Document,InsertEdit,AliasedAsTextEdit,TextChange,TextRange,Module,IdeaQuickFixData,LookupElement> 
                & IdeaDocumentChanges
                & IdeaQuickFix {
    
    newProposal(IdeaQuickFixData data, String desc, TextChange change, DefaultRegion? region)
            => makeAvailable(desc, change, region);

}