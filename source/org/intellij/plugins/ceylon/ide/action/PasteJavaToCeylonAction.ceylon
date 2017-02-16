import ceylon.tool.converter.java2ceylon {
    Java8Lexer,
    Java8Parser,
    JavaToCeylonConverter,
    ScopeTree
}

import com.intellij.openapi.actionSystem {
    ...
}
import com.intellij.openapi.application {
    ApplicationManager,
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.diagnostic {
    Logger
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.ide {
    CopyPasteManager
}
import com.intellij.openapi.progress {
    ProgressIndicator,
    ProgressManager,
    Task
}
import com.intellij.openapi.util {
    Computable
}
import com.intellij.psi {
    PsiDocumentManager,
    PsiFile
}
import com.intellij.psi.codeStyle {
    CodeStyleManager
}
import com.redhat.ceylon.ide.common.model {
    CeylonIdeConfig
}

import java.awt.datatransfer {
    DataFlavor
}
import java.io {
    IOException,
    StringWriter
}
import java.lang {
    JString=String
}

import org.antlr.v4.runtime {
    ANTLRInputStream,
    CommonTokenStream
}
import org.intellij.plugins.ceylon.ide.model {
    getCeylonProject
}

shared class PasteJavaToCeylonAction() extends AnAction() {

    shared actual void update(AnActionEvent event) {
        value presentation = event.presentation;
        value dataContext = event.dataContext;
        value provider = PlatformDataKeys.pasteProvider.getData(dataContext);
        presentation.enabled = provider?.isPastePossible(dataContext) else false;

        if (event.place.equals(ActionPlaces.editorPopup), exists provider) {
            presentation.visible = presentation.enabled;
        } else {
            presentation.visible = true;
        }
    }

    shared actual void actionPerformed(AnActionEvent e) {
        value dataContext = e.dataContext;

        if (exists javaCode = CopyPasteManager.instance.getContents<JString>(DataFlavor.stringFlavor),
            is Editor editor = dataContext.getData("editor"),
            exists project = e.project,
            exists psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document),
            exists ceylonProject = getCeylonProject(psiFile)) {

            value task = object extends Task.Backgroundable(psiFile.project, "Transforming Java code", false) {

                shared actual void run(ProgressIndicator indicator) {
                    variable String ceylonCode;
                    try {
                        ceylonCode = transformJavaToCeylon(javaCode.string, ceylonProject.ideConfiguration);
                    } catch (IOException e) {
                        Logger.getInstance(`PasteJavaToCeylonAction`)
                            .error("Couldn't transform Java code to Ceylon", e);
                        ceylonCode = "<error>";
                    }
                    insertTextInEditor(ceylonCode, e);
                }
            };
            ProgressManager.instance.run(task);
        }
    }

    String transformJavaToCeylon(String javaCode, CeylonIdeConfig ideConfig) {
        value lexer = Java8Lexer(ANTLRInputStream(javaCode));
        value parser = Java8Parser(CommonTokenStream(lexer));
        value tree = parser.compilationUnit();
        value output = StringWriter();
        value scopeTree = ScopeTree();

        tree.accept(scopeTree);

        value converter = JavaToCeylonConverter(output,
            ideConfig.converterConfig.transformGetters,
            ideConfig.converterConfig.useValues, scopeTree);

        tree.accept(converter);

        return output.string;
    }

    void insertTextInEditor(String ceylonCode, AnActionEvent evt) {
        value eventProject = getEventProject(evt);
        value dataContext = evt.dataContext;

        if (is Editor editor = PlatformDataKeys.editor.getData(dataContext),
            exists eventProject) {

            value file = ApplicationManager.application.runReadAction(
                object satisfies Computable<PsiFile> {
                    shared actual PsiFile? compute() {
                        return PsiDocumentManager.getInstance(eventProject).getPsiFile(editor.document);
                    }
                }
            );

            if (!exists file) {
                return;
            }

            object extends WriteCommandAction<Nothing>(eventProject) {

                shared actual void run(Result<Nothing> result) {
                    value selection = editor.selectionModel;
                    if (selection.selectionStart != selection.selectionEnd) {
                        editor.document.deleteString(selection.selectionStart, selection.selectionEnd);
                    }
                    value insertOffset = editor.caretModel.offset;
                    editor.document.insertString(insertOffset, ceylonCode);

                    value endInsertOffset = insertOffset + ceylonCode.size;
                    selection.setSelection(insertOffset, endInsertOffset);

                    CodeStyleManager.getInstance(eventProject)
                        .reformatText(file, insertOffset, endInsertOffset);
                }
            }.execute();
        }
    }
}
