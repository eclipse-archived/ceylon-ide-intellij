import ceylon.collection {
    ArrayList,
    ListMutator
}
import ceylon.interop.java {
    JavaRunnable,
    JavaList
}

import com.intellij.codeInsight.daemon {
    DaemonCodeAnalyzer
}
import com.intellij.codeInsight.hint {
    HintManager,
    QuestionAction
}
import com.intellij.codeInsight.intention {
    IntentionAction
}
import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.codeInspection {
    HintAction
}
import com.intellij.lang.annotation {
    Annotation
}
import com.intellij.openapi.actionSystem {
    IdeActions,
    ActionManager
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Document,
    Editor
}
import com.intellij.openapi.keymap {
    KeymapUtil
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.progress {
    ProgressManager,
    PerformInBackgroundOption
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.ui.popup {
    JBPopupFactory
}
import com.intellij.openapi.util {
    TextRange,
    Iconable
}
import com.intellij.psi {
    PsiFile
}
import com.intellij.psi.util {
    PsiUtilBase
}
import com.intellij.ui.components {
    JBList
}
import com.intellij.util {
    NotNullFunction
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
    QuickFixData,
    QuickFixKind
}
import com.redhat.ceylon.ide.common.doc {
    Icons
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

import javax.swing {
    Icon,
    JComponent,
    JLabel
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument,
    IdeaTextChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

class CustomIntention(Integer position, String desc,
    <PlatformTextChange|Anything()>? change,
    TextRange? selection = null, Icon? image = null,
    Boolean qualifiedNameIsPath = false,
    [String,TextRange]? hint = null,
    Anything callback(Project project, Editor editor, PsiFile psiFile) => noop())
        extends BaseIntentionAction()
        satisfies Iconable & Comparable<IntentionAction> & HintAction {

    variable Project? project = null;

    shared actual Boolean showHint(Editor editor) {
        //TODO: only popup the hint if our caret is really close to the annotation?
        if (exists [text,range] = hint,
            exists project = this.project) {

            object hintedAction satisfies QuestionAction {
                shared actual Boolean execute() {
//                    hint = null;
                    value p = project;
                    object extends WriteCommandAction<Nothing>(p, "Execute Hinted Fix") {
                        shared actual void run(Result<Nothing> result) {
                            value file = PsiUtilBase.getPsiFileInEditor(editor, project);
                            invoke {
                                project = project;
                                editor = editor;
                                psiFile = file;
                            };
                            //cleared later on in CeylonTypeCheckerAnnotator.apply()
                            DaemonCodeAnalyzer.getInstance(project)
                                .setImportHintsEnabled(file, false);
                        }
                    }.execute();
                    return true;
                }
            }

            value shortcut
                = KeymapUtil.getFirstKeyboardShortcutText(
                    ActionManager.instance.getAction(
                        IdeActions.actionShowIntentionActions));

            HintManager.instance.showQuestionHint(editor,
                text + " " + shortcut,
                range.startOffset, range.endOffset,
                hintedAction);

            return true;
        }
        else {
            return false;
        }
    }

    familyName => "Ceylon Intentions";

    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        if (is IdeaTextChange change) {
            change.applyOnProject(project);
        } else if (is Anything() change) {
            change();
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
    
    text => if (exists p = project) 
        then highlighter.highlightQuotedMessage(desc, p, qualifiedNameIsPath) 
        else desc;
    
    shared actual Icon? getIcon(Integer int) => image;
    
    equals(Object that)
            => if (is CustomIntention that)
            then desc==that.desc else false;
    
    compareTo(IntentionAction? t)
            => if (is CustomIntention t)
            then position - t.position else 0;
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

    useLazyFixes => true;
    errorCode => message.code;
    problemOffset => annotation?.startOffset else 0;
    problemLength => (annotation?.endOffset else 0) - problemOffset;
    editorSelection => if (exists e = editor)
                       then DefaultRegion(e.selectionModel.selectionStart, e.selectionModel.selectionEnd)
                       else DefaultRegion(0);
    
    shared default void registerFix(String desc,
        <PlatformTextChange|Anything()>? change,
        TextRange? selection = null, Icon? image = null,
        Boolean qualifiedNameIsPath = false, String? hint = null,
        Anything callback(Project project, Editor editor, PsiFile psiFile) => noop()) {
        
        if (exists annotation) {
            value position = annotation.quickFixes ?. size() else 0;
            value intention
                = CustomIntention {
                    position = position;
                    desc = desc;
                    change = change;
                    selection = selection;
                    image = image;
                    qualifiedNameIsPath = qualifiedNameIsPath;
                    callback = callback;
                    hint = if (exists hint) then [hint, TextRange(annotation.startOffset, annotation.endOffset)] else null;
                };
            annotation.registerFix(intention);
        } else {
            if (is IdeaTextChange change) {
                change.apply();
            } else if (is Anything() change){
                change();
            } else {
                return;
            }

            if (exists selection, exists e = editor) {
                e.selectionModel.setSelection(selection.startOffset, selection.endOffset);
                e.caretModel.moveToOffset(selection.endOffset);
            }
        }
    }

    document = IdeaDocument(nativeDoc);

    void showPopup(Editor editor, List<Resolution> candidates, String title) {
        value list = JBList(JavaList(candidates));
        list.installCellRenderer(object satisfies NotNullFunction<Resolution, JComponent> {
            fun(Resolution action)
                    => JLabel(action.description, action.icon, JLabel.leading);
        });

        JBPopupFactory.instance
            .createListPopupBuilder(list)
            .setTitle(title)
            .setItemChoosenCallback(JavaRunnable(void () {
                if (exists candidate = candidates[list.selectedIndex]) {
                    object extends WriteCommandAction<Nothing>(editor.project) {
                        shared actual void run(Result<Nothing>? result) {
                            switch (change = candidate.change)
                            case (is PlatformTextChange) {
                                change.apply();
                            }
                            else {
                                change();
                            }
                        }
                    }.execute();
                }
            }))
            .createPopup()
            .showInBestPositionFor(editor);
    }

    function toRange(DefaultRegion? region)
            => if (exists region)
               then TextRange.from(region.start, region.length)
               else null;

    class Resolution(shared String description, shared Icon icon, shared PlatformTextChange|Anything() change) {}
    variable ListMutator<Resolution>? resolutions = null;

    shared actual default void addQuickFix(String desc,
        PlatformTextChange|Anything() change,
        DefaultRegion? selection, Boolean qualifiedNameIsPath, Icons? icon,
        QuickFixKind kind, String? hint, Boolean asynchronous) {
        value range = toRange(selection);

        if (asynchronous) {
            assert (is Anything() change);
            registerFix {
                desc = desc;
                change = null;
                selection = range;
                image = icons.correction;
                qualifiedNameIsPath = qualifiedNameIsPath;
                hint = hint;
                void callback(Project p, Editor e, PsiFile f) {
                    value candidates = ArrayList<Resolution>();
                    resolutions = candidates;
                    ProgressManager.instance
                            .runProcessWithProgressAsynchronously(
                        project.project,
                        "Searching...",
                        JavaRunnable(change),
                        JavaRunnable(void() {
                            resolutions = null;
                            if (!candidates.empty) {
                                showPopup(e, candidates, "Select a Resolution");
                            }
                        }),
                        null,
                        PerformInBackgroundOption.alwaysBackground);
                }
            };
        } else if (exists candidates = resolutions) {
            candidates.add(Resolution(desc, icons.imports, change));
        } else {
            registerFix {
                desc = desc;
                change = change;
                selection = range;
                image = icons.correction;
                qualifiedNameIsPath = qualifiedNameIsPath;
                hint = hint;
            };
        }
    }

    shared actual default void addConvertToClassProposal(String description, Tree.ObjectDefinition declaration) {
        //TODO
    }

    shared actual void addAssignToLocalProposal(String description) {
        registerFix { 
            desc = description;
            change = null;
            callback = (p, e, f) {
                if(is CeylonFile f) {
                    AssignToLocalElement(this, p, e, f).perform();
                }
            };
        };
    }
    
}
