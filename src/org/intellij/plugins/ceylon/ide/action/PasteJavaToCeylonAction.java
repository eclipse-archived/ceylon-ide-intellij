package org.intellij.plugins.ceylon.ide.action;

import ceylon.tool.converter.java2ceylon.Java8Lexer;
import ceylon.tool.converter.java2ceylon.Java8Parser;
import ceylon.tool.converter.java2ceylon.JavaToCeylonConverter;
import com.intellij.ide.PasteProvider;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.StringWriter;

public class PasteJavaToCeylonAction extends AnAction {

    @Override
    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();
        DataContext dataContext = event.getDataContext();

        PasteProvider provider = PlatformDataKeys.PASTE_PROVIDER.getData(dataContext);
        presentation.setEnabled(provider != null && provider.isPastePossible(dataContext));
        if (event.getPlace().equals(ActionPlaces.EDITOR_POPUP) && provider != null) {
            presentation.setVisible(presentation.isEnabled());
        } else {
            presentation.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        String ceylonCode = "<error>";

        try {
            String javaCode = CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor);
            final DataContext dataContext = e.getDataContext();
            final Editor editor = (Editor) dataContext.getData("editor");
            PsiFile psiFile = PsiDocumentManager.getInstance(e.getProject()).getPsiFile(editor.getDocument());
            IdeaCeylonProjects projects = e.getProject().getComponent(IdeaCeylonProjects.class);
            Module module = ModuleUtil.findModuleForFile(psiFile.getVirtualFile(), psiFile.getProject());
            CeylonIdeConfig<Module> ideConfig = projects.getProject(module).getIdeConfiguration();

            ceylonCode = transformJavaToCeylon(javaCode, ideConfig);
        } catch (IOException exc) {
            Logger.getInstance(PasteJavaToCeylonAction.class).error("Couldn't transform Java code to Ceylon", exc);
        }

        insertTextInEditor(ceylonCode, e);
    }

    private String transformJavaToCeylon(String javaCode, CeylonIdeConfig<Module> ideConfig) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(javaCode);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParserRuleContext tree = parser.compilationUnit();

        StringWriter out = new StringWriter();

        JavaToCeylonConverter converter = new JavaToCeylonConverter(out,
                ideConfig.getConverterConfig().getTransformGetters(),
                ideConfig.getConverterConfig().getUseVariableInParameters(),
                ideConfig.getConverterConfig().getUseVariableInLocals(),
                ideConfig.getConverterConfig().getUseValues());

        tree.accept(converter);

        return out.toString();
    }

    private void insertTextInEditor(final String ceylonCode, AnActionEvent evt) {
        final Project eventProject = getEventProject(evt);
        final DataContext dataContext = evt.getDataContext();
        final Editor editor = (Editor) dataContext.getData("editor");

        if (eventProject == null || editor == null) {
            return;
        }

        final PsiFile file = PsiDocumentManager.getInstance(eventProject).getPsiFile(editor.getDocument());

        if (file == null) {
            return;
        }

        new WriteCommandAction(eventProject) {

            @Override
            protected void run(@NotNull Result result) throws Throwable {
                SelectionModel selection = editor.getSelectionModel();

                // Remove the current selected text, if any
                if (selection.getSelectionStart() != selection.getSelectionEnd()) {
                    editor.getDocument().deleteString(selection.getSelectionStart(), selection.getSelectionEnd());
                }

                int insertOffset = editor.getCaretModel().getOffset();
                editor.getDocument().insertString(insertOffset, ceylonCode);

                int endInsertOffset = insertOffset + ceylonCode.length();
                selection.setSelection(insertOffset, endInsertOffset);
                CodeStyleManager.getInstance(eventProject).reformatText(file, insertOffset, endInsertOffset);
            }
        }.execute();
    }
}
