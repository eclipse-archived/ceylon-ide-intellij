import ceylon.collection {
    ArrayList,
    ListMutator
}
import ceylon.interop.java {
    JavaList
}

import com.intellij.codeInsight.hint {
    HintManager
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
    Result,
    ApplicationManager
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Document,
    Editor
}
import com.intellij.openapi.\imodule {
    Mod=Module
}
import com.intellij.openapi.progress {
    ProgressManager
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
import com.intellij.ui {
    SimpleTextAttributes
}
import com.intellij.ui.components {
    JBList,
    JBLabel
}
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails
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
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    TypeDeclaration,
    TypedDeclaration,
    Referenceable,
    Module
}

import java.awt {
    Color
}
import java.lang {
    Comparable
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.highlighting {
    highlighter,
    ceylonHighlightingColors,
    foregroundColor
}
import org.intellij.plugins.ceylon.ide.model {
    concurrencyManager {
        withAlternateResolution
    },
    getModelManager
}
import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument,
    IdeaTextChange
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

class CustomIntention(Integer position, String desc,
    <PlatformTextChange|Anything()>? change,
    TextRange? selection = null, Icon? image = null,
//    Boolean qualifiedNameIsPath = false,
    [String,TextRange]? hint = null,
    Anything callback(Project project, Editor editor, PsiFile psiFile) => noop(),
    Boolean affectsOtherUnits = false)
        extends BaseIntentionAction()
        satisfies Iconable & Comparable<IntentionAction> /*& HintAction*/ {

    variable Project? project = null;

    /*shared actual Boolean showHint(Editor editor) {
        //TODO: only popup the hint if our caret is really close to the annotation?
        if (exists [text,range] = hint,
            exists project = this.project) {

            object hintedAction satisfies QuestionAction {
                shared actual Boolean execute() {
                    value project = project;
                    object extends WriteCommandAction<Nothing>(project, "Execute Hinted Fix") {
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
    }*/

    familyName => "Ceylon Intentions";

    shared actual void invoke(Project project, Editor editor, PsiFile psiFile) {
        assert (exists modelManager = getModelManager(project));
        try {
            modelManager.pauseAutomaticModelUpdate();

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
        } finally {
            if (affectsOtherUnits) {
                modelManager.resumeAutomaticModelUpdate(0);
            } else {
                modelManager.resumeAutomaticModelUpdate();
            }
        }
    }
    
    shared actual Boolean isAvailable(Project project, Editor? editor, PsiFile? psiFile) {
        this.project = project;
        return true;
    }
    
    text => if (exists p = project) 
        then highlighter.highlightQuotedMessage(desc, p/*, qualifiedNameIsPath*/)
        else desc;
    
    getIcon(Integer int) => image;
    
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
    shared Mod ideaModule,
    shared Annotation? annotation,
    shared actual BaseCeylonProject ceylonProject,
    shared variable Editor? editor = null
) satisfies QuickFixData {

    assert (exists modelManager = getModelManager(ideaModule.project));

    useLazyFixes => true;
    errorCode => message.code;
    problemOffset => annotation?.startOffset else 0;
    problemLength => (annotation?.endOffset else 0) - problemOffset;
    editorSelection
            => if (exists e = editor)
            then DefaultRegion {
                start = e.selectionModel.selectionStart;
                length = e.selectionModel.selectionEnd
                       - e.selectionModel.selectionStart;
            }
            else DefaultRegion(0);
    
    function textRange(Annotation annotation)
            => TextRange(annotation.startOffset,
                         annotation.endOffset);

    shared default void registerFix(String desc,
        <PlatformTextChange|Anything()>? change,
        TextRange? selection = null, Icon? image = null,
        Boolean qualifiedNameIsPath = false,
        String? hint = null,
        Anything callback(Project project, Editor editor, PsiFile psiFile) => noop(),
        Boolean affectsOtherUnits = false) {
        
        if (exists annotation) {
            // Register the quick fix in a list
            value intention
                = CustomIntention {
                    position = annotation.quickFixes?.size() else 0;
                    desc = desc;
                    change = change;
                    selection = selection;
                    image = image;
//                    qualifiedNameIsPath = qualifiedNameIsPath;
                    callback = callback;
                    hint = if (exists hint) then [hint, textRange(annotation)] else null;
                    affectsOtherUnits = affectsOtherUnits;
                };
            annotation.registerFix(intention);
        } else {
            // Apply the quick fix immediately
            try {
                modelManager.pauseAutomaticModelUpdate();

                switch (change)
                case (null) {
                    return;
                } case (is PlatformTextChange) {
                    change.apply();
                } else {
                    change();
                }

                if (exists selection, exists e = editor) {
                    e.selectionModel.setSelection(selection.startOffset, selection.endOffset);
                    e.caretModel.moveToOffset(selection.endOffset);
                }
            } finally {
                if (affectsOtherUnits) {
                    modelManager.resumeAutomaticModelUpdate(0);
                } else {
                    modelManager.resumeAutomaticModelUpdate();
                }
            }
        }
    }

    document = IdeaDocument(nativeDoc);

    function toRange(DefaultRegion? region)
            => if (exists region)
               then TextRange.from(region.start, region.length)
               else null;

    variable ListMutator<Resolution>? resolutions = null;

    shared actual default void addQuickFix(String desc,
        PlatformTextChange|Anything() change,
        DefaultRegion? selection, Boolean qualifiedNameIsPath,
        Icons? icon, QuickFixKind kind, String? hint,
        Boolean asynchronous, Referenceable|ModuleVersionDetails? declaration,
        Boolean affectsOtherUnits) {

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
                void callback(Project project, Editor editor, PsiFile file) {
                    value candidates = ArrayList<Resolution>();
                    resolutions = candidates;
                    ProgressManager.instance
                            .runProcessWithProgressAsynchronously(
                        project, "Searching...",
                        () =>
                            withAlternateResolution(() =>
                                ApplicationManager.application.runReadAction(change)),
                        () {
                            resolutions = null;
                            if (candidates.empty) {
                                //TODO show it at the right location!
                                HintManager.instance.showErrorHint(editor,
                                    "No resolutions found");
                            }
                            else if (candidates.size==1) {
                                assert (exists resolution = candidates[0]);
                                value change = resolution.change;
                                value p = project;

                                try {
                                    modelManager.pauseAutomaticModelUpdate();

                                    object extends WriteCommandAction<Nothing>(p, file) {
                                        shared actual void run(Result<Nothing> result) {
                                            if (is PlatformTextChange change) {
                                                assert (is IdeaTextChange change);
                                                change.applyOnProject(project);
                                            } else {
                                                change();
                                            }
                                            if (exists qualifier = resolution.qualifier) {
                                                value text = highlighter.highlightQuotedMessage {
                                                    description = "Automatically resolved '``resolution.description``' ('``qualifier``')";
                                                    project = project;
                                                    //qualifiedNameIsPath = true;
                                                };
                                                //TODO: the icon?
                                                HintManager.instance.showInformationHint(editor, JBLabel(text));
                                            }
                                        }
                                    }.execute();
                                } finally {
                                    modelManager.resumeAutomaticModelUpdate(0);
                                }
                            }
                            else {
                                showPopup {
                                    editor = editor;
                                    candidates = candidates;
                                    title = "Select a Resolution";
                                    project = project;
                                };
                            }
                        },
                        () {},
                        () => true);
                }
                affectsOtherUnits = affectsOtherUnits;
            };
        } else if (exists candidates = resolutions) {
            candidates.add(Resolution {
                description
                    = switch (declaration)
                    case (is Referenceable) declaration.nameAsString
                    else case (is ModuleVersionDetails) declaration.\imodule
                    else desc;
                qualifier
                    = switch (declaration)
                    case (is Declaration) declaration.unit?.\ipackage?.nameAsString
                    case (is Module) declaration.version
                    case (is ModuleVersionDetails) declaration.version
                    else null;
//                qualifiedNameIsPath = qualifiedNameIsPath;
                icon
                    = if (is Declaration declaration,
                          exists icon = icons.forDeclaration(declaration))
                    then icon
                    else icons.moduleArchives;
                color
                    = switch (declaration)
                    case (is TypeDeclaration) foregroundColor(ceylonHighlightingColors.type)
                    case (is TypedDeclaration) foregroundColor(ceylonHighlightingColors.identifier)
                    case (is Module|ModuleVersionDetails) foregroundColor(ceylonHighlightingColors.packages)
                    else SimpleTextAttributes.regularAttributes.fgColor;
                change = change;
            });
        } else {
            registerFix {
                desc = desc;
                change = change;
                selection = range;
                image = icons.correction;
                qualifiedNameIsPath = qualifiedNameIsPath;
                hint = hint;
                affectsOtherUnits = affectsOtherUnits;
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
            void callback(Project p, Editor e, PsiFile f) {
                if (is CeylonFile f) {
                    AssignToLocalElement(this, e, f).perform();
                }
            }
        };
    }
    
}

shared class Resolution(description, icon, change, /*qualifiedNameIsPath,*/ qualifier, color) {
    shared PlatformTextChange|Anything() change;
    shared Icon icon;
    shared String description;
    shared String? qualifier;
//    shared Boolean qualifiedNameIsPath;
    shared Color color;
}

shared void showPopup(Editor editor, List<Resolution> candidates, String title, Project project) {

    value list = JBList(JavaList(candidates.collect((c)
            => CeylonListItem {
                icon = c.icon;
                color = c.color;
                label = c.description;
                qualifier = c.qualifier;
                extraQualifier = null;
                payload = c.change;
            })));

    list.setCellRenderer(CeylonCellRenderer());

    /*list.installCellRenderer(object satisfies NotNullFunction<Resolution, JComponent> {
        shared actual JLabel fun(Resolution resolution) {
            value desc = resolution.description;
            value index = desc.lastInclusion(" (") else desc.size;
            value description = desc;
//                = highlighter.highlightQuotedMessage {
//                    description = desc[0:index];
//                    qualifiedNameIsPath = resolution.qualifiedNameIsPath;
//                    project = project;
//                }
//                .removeTerminal("</html>")
//                + highlighter.toColoredHtml {
//                    token = desc[index...];
//                    attr =
//                        UsageTreeColorsScheme.instance.scheme
//                            .getAttributes(UsageTreeColors.numberOfUsages);
//                }
//                + "</html>";
            return JLabel(description, resolution.icon, JLabel.leading);
        }
    });*/

    JBPopupFactory.instance
        .createListPopupBuilder(list)
        .setTitle(title)
        .setMovable(true)
        .setItemChoosenCallback(() {
            if (exists candidate = candidates[list.selectedIndex]) {
                object extends WriteCommandAction<Nothing>(editor.project) {
                    shared actual void run(Result<Nothing> result) {
                        assert (exists modelManager = getModelManager(project));
                        try {
                            modelManager.pauseAutomaticModelUpdate();

                            switch (change = candidate.change)
                            case (is PlatformTextChange) {
                                change.apply();
                            }
                            else {
                                change();
                            }
                        } finally {
                            modelManager.resumeAutomaticModelUpdate(0);
                        }
                    }
                }.execute();
            }
        })
        .createPopup()
        .showInBestPositionFor(editor);
}

