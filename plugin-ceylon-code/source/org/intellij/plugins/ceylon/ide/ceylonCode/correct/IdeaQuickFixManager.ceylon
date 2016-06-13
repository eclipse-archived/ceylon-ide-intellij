import ceylon.interop.java {
    JavaRunnable
}

import com.intellij.codeInsight.intention {
    IntentionAction
}
import com.intellij.codeInsight.intention.impl {
    BaseIntentionAction
}
import com.intellij.lang.annotation {
    Annotation
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
    asyncModuleImport,
    QuickFixKind,
    addModuleImport
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
import java.util {
    ArrayList
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

class CustomIntention(Integer position, String desc, <PlatformTextChange|Anything()>? change, TextRange? selection = null, Icon? image = null,
    Boolean qualifiedNameIsPath = false, Anything callback(Project project, Editor editor, PsiFile psiFile) => noop)
        extends BaseIntentionAction()
        satisfies Iconable & Comparable<IntentionAction> {
    
    shared actual String familyName => "Ceylon Intentions";
    variable Project? project = null;
    
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
    
    shared actual Boolean equals(Object that) {
        if (is CustomIntention that) {
            return desc==that.desc;
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

    useLazyFixes => true;
    errorCode => message.code;
    problemOffset => annotation?.startOffset else 0;
    problemLength => (annotation?.endOffset else 0) - problemOffset;
    editorSelection => if (exists e = editor)
                       then DefaultRegion(e.selectionModel.selectionStart, e.selectionModel.selectionEnd)
                       else DefaultRegion(0);
    
    shared default void registerFix(String desc, <PlatformTextChange|Anything()>? change, TextRange? selection = null, Icon? image = null,
        Boolean qualifiedNameIsPath = false, Anything callback(Project project, Editor editor, PsiFile psiFile) => noop) {
        
        if (exists annotation) {
            value position = annotation.quickFixes ?. size() else 0;
            IntentionAction intention = CustomIntention(position, desc, change,
                selection, image, qualifiedNameIsPath, callback);
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

    value candidateModules = ArrayList<[String, Icon, Anything()]>();

    void showImportModulesPopup(Editor editor) {
        value list = JBList(candidateModules);
        list.installCellRenderer(object satisfies NotNullFunction<[String, Icon, Anything()], JComponent> {
            fun([String, Icon, Anything()] tuple) => JLabel(tuple[0], tuple[1], JLabel.leading);
        });

        JBPopupFactory.instance
            .createListPopupBuilder(list)
            .setTitle("Choose module to import")
            .setItemChoosenCallback(JavaRunnable(void () {
                if (exists tuple = candidateModules.get(list.selectedIndex)) {
                    object extends WriteCommandAction<Nothing>(editor.project) {
                        run(Result<Nothing>? result) => tuple[2]();
                    }.execute();
                }
            }))
            .createPopup()
            .showInBestPositionFor(editor);
    }

    TextRange? toRange(DefaultRegion? region)
            => if (exists region)
               then TextRange.from(region.start, region.length)
               else null;

    shared actual default void addQuickFix(String desc,
        PlatformTextChange|Anything() change,
        DefaultRegion? selection, Boolean qualifiedNameIsPath, Icons? icon,
        QuickFixKind kind) {
        value range = toRange(selection);

        if (is IdeaTextChange change) {
            registerFix(desc, change, range, icons.correction, qualifiedNameIsPath);
        } else if (is Anything() change) {
            if (kind == asyncModuleImport) {
                candidateModules.add([desc, icons.imports, change]);
            } else if (kind == addModuleImport) {
                registerFix(desc, null, range, icons.correction, qualifiedNameIsPath, (p, e, f) {
                    candidateModules.clear();
                    ProgressManager.instance.runProcessWithProgressAsynchronously(
                        project.project,
                        "Querying module repositories",
                        JavaRunnable(change),
                        JavaRunnable(void () {
                            if (!candidateModules.empty) {
                                showImportModulesPopup(e);
                            }
                        }),
                        null,
                        PerformInBackgroundOption.alwaysBackground
                    );
                });
            } else {
                registerFix(desc, change, range, icons.correction, qualifiedNameIsPath);
            }
        }
    }

    shared actual default void addConvertToClassProposal(String description, Tree.ObjectDefinition declaration) {}

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
