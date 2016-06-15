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
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.correct {
    QuickFixKind,
    convertToClassQuickFix
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.ide.common.platform {
    PlatformTextChange=TextChange,
    platformUtils,
    Status
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

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument,
    IdeaTextChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

abstract shared class AbstractIntention() extends BaseIntentionAction() {

    variable <IdeaTextChange|Anything()>? change = null;
    variable TextRange? selection = null;
    variable Boolean available = false;
    variable Anything(Project, Editor, PsiFile) callback = noop;
    
    value dummyMsg = UsageWarning(null, null, null);
    
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        if (is IdeaTextChange chg = change) {
            chg.applyOnProject(project);
        } else if (is Anything() callback = change) {
            callback();
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
            exists localAnalysisResult = psiFile.localAnalysisResult,
            is ModifiablePhasedUnit<out Anything,out Anything,out Anything,out Anything> analyzedPhasedUnit = localAnalysisResult.lastPhasedUnit) {
            
            value typecheckedCompilationUnit = localAnalysisResult.typecheckedRootNode;
            if (! exists typecheckedCompilationUnit) {
                platformUtils.log(Status._ERROR, "AbstractIntention `` this`` is not available because the file `` psiFile `` is not typechecked and up-to-date");
                return false;
            }
            value offset = _editor.caretModel.offset;
            value _node = nodes.findNode(typecheckedCompilationUnit,
                localAnalysisResult.tokens, offset);
            
            value mod = ModuleUtil.findModuleForFile(psiFile.virtualFile, project);
            
            if (exists _node,
                exists pr = project.getComponent(javaClass<IdeaCeylonProjects>()).getProject(mod)) {

                value outerProject = project;
                value data = object extends IdeaQuickFixData(
                    dummyMsg, 
                    psiFile.viewProvider.document,
                    typecheckedCompilationUnit,
                    analyzedPhasedUnit,
                    _node,
                    mod,
                    null,
                    pr,
                    _editor
                ) {
                    shared actual void addQuickFix(String desc, PlatformTextChange|Anything() change,
                        DefaultRegion? selection, Boolean ignored, Icons? icon, QuickFixKind kind,
                        String? hint, Boolean async) {
                        if (is IdeaTextChange|Anything() change) {
                            makeAvailable(outerProject, desc, change, selection);
                        }
                    }

                    shared actual void registerFix(String desc, <PlatformTextChange|Anything()>? change,
                        TextRange? selection, Icon? image, Boolean qualifiedNameIsPath, String? hint,
                        Anything callback(Project project, Editor editor, PsiFile psiFile)) {

                        value sel = if (exists selection)
                                    then DefaultRegion(selection.startOffset, selection.length)
                                    else null;
                        if (is <IdeaTextChange|Anything()>? change) {
                            makeAvailable(outerProject, desc, change, sel, callback);
                        }
                    }

                    shared actual void addConvertToClassProposal(String description, Tree.ObjectDefinition declaration) {
                        makeAvailable(outerProject, description, null, null, (p, e, f) {
                                value document = IdeaDocument(f.viewProvider.document);
                                convertToClassQuickFix.applyChanges(document, declaration);
                        });
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
    
    void makeAvailable(Project p, String desc, <IdeaTextChange|Anything()>? change = null,
        DefaultRegion? sel = null, 
        Anything callback(Project p, Editor e, PsiFile f) => noop) {
        
        setText(highlighter.highlightQuotedMessage(desc, p));
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
