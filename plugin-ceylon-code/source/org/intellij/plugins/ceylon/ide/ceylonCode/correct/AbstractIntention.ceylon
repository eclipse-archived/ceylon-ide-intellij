import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.openapi.editor {
    Editor
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

abstract shared class AbstractIntention() extends BaseIntentionAction() {

    variable TextChange? change = null;
    variable TextRange? selection = null;
    variable Boolean available = false;
    
    value dummyMsg = UsageWarning(null, null, null);
    
    shared actual String familyName => "Ceylon inspections";
    
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        if (exists chg = change) {
            chg.apply(project);
        }
        if (exists sel = selection) {
            editor.selectionModel.setSelection(sel.startOffset, sel.endOffset);
            editor.caretModel.moveToOffset(sel.endOffset);
        }
    }
    
    shared actual Boolean isAvailable(Project project, Editor editor, PsiFile psiFile) {
        available = false;

        if (is CeylonFile psiFile) {
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

                checkAvailable(data, psiFile, offset);
            }
        }
        
        return available;
    }
    
    shared formal void checkAvailable(IdeaQuickFixData data, CeylonFile file, Integer offset);
    
    shared void makeAvailable(String desc, TextChange change, DefaultRegion? sel) {
        setText(desc);
        available = true;
        this.change = change;

        if (exists sel) {
            selection = TextRange.from(sel.start, sel.length);
        } else {
            selection = null;
        }
    }
}