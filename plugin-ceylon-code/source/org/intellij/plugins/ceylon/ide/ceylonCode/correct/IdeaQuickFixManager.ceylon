import ceylon.interop.java {
    JavaRunnable
}

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
    IdeQuickFixManager,
    QuickFixData,
    exportModuleImportQuickFix,
    addModuleImportQuickFix
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
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Scope,
    Type,
    Referenceable
}

import java.lang {
    Comparable
}
import java.util {
    Collection,
    ArrayList
}

import javax.swing {
    Icon,
    JComponent,
    JLabel
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
        extends IdeQuickFixManager<Document,InsertEdit,TextEdit,TextChange,TextRange,CeylonFile,LookupElement,IdeaQuickFixData,IdeaLinkedMode>() {
    
    importProposals => ideaImportProposals;
    createQuickFix => ideaCreateQuickFix;
    createEnumQuickFix => ideaCreateEnumQuickFix;
    changeReferenceQuickFix => ideaChangeReferenceQuickFix;
    declareLocalQuickFix => ideaDeclareLocalQuickFix;
    refineFormalMembersQuickFix => ideaRefineFormalMembersQuickFix;
    specifyTypeQuickFix => ideaSpecifyTypeQuickFix;
    changeTypeQuickFix => ideaChangeTypeQuickFix;
    addSatisfiesQuickFix => ideaAddSatisfiesQuickFix;
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

    useLazyFixes => true;
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

    document = DocumentWrapper(nativeDoc);

    value candidateModules = ArrayList<[Unit, String, String, String]>();

    void showImportModulesPopup(Editor editor) {
        value list = JBList(candidateModules);
        list.installCellRenderer(object satisfies NotNullFunction<[Unit, String, String, String], JComponent> {
            fun([Unit, String, String, String] tuple) => JLabel(tuple[1]);
        });

        JBPopupFactory.instance
            .createListPopupBuilder(list)
            .setTitle("Choose module to import")
            .setItemChoosenCallback(JavaRunnable(void () {
                if (exists val = candidateModules.get(list.selectedIndex)) {
                    object extends WriteCommandAction<Nothing>(editor.project) {
                        run(Result<Nothing>? result)
                                => addModuleImportQuickFix.applyChanges(outer, val[0], val[2], val[3]);
                    }.execute();
                }
            }))
            .createPopup()
            .showInBestPositionFor(editor);
    }

    shared actual default void addQuickFix(String desc,
        PlatformTextChange|Anything() change,
        DefaultRegion? selection, Boolean qualifiedNameIsPath) {
        value range = if (exists selection)
                      then TextRange.from(selection.start, selection.length)
                      else null;

        if (is IdeaTextChange change) {
            registerFix(desc, change, range, ideaIcons.correction);
        } else if (is Anything() change) {
            registerFix(desc, null, null, ideaIcons.correction, false, (p, e, f) {
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
        }
    }

    shared actual void addInitializerQuickFix(String description, PlatformTextChange change,
        DefaultRegion selection, Unit unit, Scope scope, Type? type) {

        if (is IdeaTextChange change) {
            value range = TextRange.from(selection.start, selection.length);
            void callback(Project project, Editor editor, PsiFile file) {
                IdeaInitializer().addInitializer(editor.document,
                    selection,
                    type,
                    unit,
                    scope
                );
            }
            registerFix(description, change, range, ideaIcons.correction, false, callback);
        }
    }
    shared actual void addParameterQuickFix(String description, PlatformTextChange change,
        DefaultRegion selection, Unit unit, Scope scope, Type? type, Integer exitPos) {

        if (is IdeaTextChange change) {
            value range = TextRange.from(selection.start, selection.length);
            void callback(Project project, Editor editor, PsiFile file) {
                IdeaInitializer().addInitializer(editor.document,
                    selection,
                    type,
                    unit,
                    scope
                );
            }
            registerFix(description, change, range, ideaIcons.correction, false, callback);
        }
    }
    shared actual void addParameterListQuickFix(String description, PlatformTextChange change,
        DefaultRegion selection) {

        if (is IdeaTextChange change) {
            value range = TextRange.from(selection.start, selection.length);
            registerFix(description, change, range, ideaIcons.correction);
        }
    }

    shared actual void addAnnotationProposal(Referenceable declaration, String text,
        String description, PlatformTextChange change, DefaultRegion? selection) {

        if (is IdeaTextChange change) {
            value range = if (exists selection) then TextRange.from(selection.start, selection.length) else null;
            registerFix(description, change, range, ideaIcons.correction);
        }
    }

    shared actual void addExportModuleImportProposal(Unit u, String description,
        String name, String version) {

        registerFix(description, null, null, ideaIcons.modules, false,
            void (Project project, Editor editor, PsiFile psiFile) {
                exportModuleImportQuickFix.applyChanges(this, u, name);
            }
        );
    }

    shared actual void addModuleImportProposal(Unit u, String description, String name, String version) {
        candidateModules.add([u, description, name, version]);
    }
}
