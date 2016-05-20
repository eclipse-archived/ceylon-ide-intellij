import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument,
    IdeaTextChange
}
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
import com.redhat.ceylon.ide.common.doc {
    Icons
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
import com.redhat.ceylon.model.typechecker.model {
    Type
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProjects
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import com.redhat.ceylon.ide.common.correct {
    refineFormalMembersQuickFix,
    specifyTypeQuickFix,
    QuickFixKind
}
import javax.swing {
    Icon
}
import org.intellij.plugins.ceylon.ide.ceylonCode.completion {
    ideaCompletionManager
}

abstract shared class AbstractIntention() extends BaseIntentionAction() {

    variable IdeaTextChange? change = null;
    variable TextRange? selection = null;
    variable Boolean available = false;
    variable Anything(Project, Editor, PsiFile) callback = noop;
    
    value dummyMsg = UsageWarning(null, null, null);
    
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        if (is IdeaTextChange chg = change) {
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
                        DefaultRegion? selection, Boolean ignored, Icons? icon, QuickFixKind kind) {
                        if (is IdeaTextChange change) {
                            makeAvailable(desc, change, selection);
                        }
                    }

                    shared actual void registerFix(String desc, PlatformTextChange? change,
                        TextRange? selection, Icon? image, Boolean qualifiedNameIsPath,
                        Anything callback(Project project, Editor editor, PsiFile psiFile)) {

                        if (is IdeaTextChange change) {
                            value sel = if (exists selection)
                                        then DefaultRegion(selection.startOffset, selection.length)
                                        else null;
                            makeAvailable(desc, change, sel, callback);
                        }
                    }

                    shared actual void addConvertToClassProposal(String description, Tree.ObjectDefinition declaration) {
                        makeAvailable(description, null, null, (p, e, f) {
                                value document = IdeaDocument(f.viewProvider.document);
                                ConvertToClassProposal(p).applyChanges(document, declaration);
                        });
                    }
                    
                    shared actual void addSpecifyTypeProposal(String description, Tree.Type type, Tree.CompilationUnit cu, Type infType) {
                        makeAvailable(description, null, null, (project, editor, file) {
                                specifyTypeQuickFix.specifyType(document, type, true, cu, infType,
                                    nativeDoc, ideaCompletionManager);
                            }
                        );
                    }
                    
                    shared actual void addRefineFormalMembersProposal(String description) {
                        if (exists e = editor,
                            is IdeaTextChange change = refineFormalMembersQuickFix.refineFormalMembers(this, e.caretModel.offset)) {
                            makeAvailable(description, change);
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
    
    shared void makeAvailable(String desc, IdeaTextChange? change = null,
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
