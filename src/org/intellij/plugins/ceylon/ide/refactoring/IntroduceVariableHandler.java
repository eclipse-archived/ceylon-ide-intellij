package org.intellij.plugins.ceylon.ide.refactoring;

import com.intellij.lang.LanguageRefactoringSupport;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenameHandler;
import com.intellij.util.Function;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.refactoring.ExtractValueResult;
import com.redhat.ceylon.ide.common.refactoring.FindContainingExpressionsVisitor;
import com.redhat.ceylon.ide.common.util.nodes_;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerInvoker;
import org.intellij.plugins.ceylon.ide.ceylonCode.refactoring.IdeaExtractValueRefactoring;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntroduceVariableHandler implements RefactoringActionHandler {

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        final PsiElement value = selectValueToExtract(project, editor, file);

        if (value != null) {
            createAndIntroduceValue(project, editor, file, value);
        }
    }

    private void createAndIntroduceValue(final Project project, final Editor editor, final PsiFile file, final PsiElement value) {
        CeylonFile ceylonFile = (CeylonFile) file;
        IdeaExtractValueRefactoring refacto = new IdeaExtractValueRefactoring(ceylonFile.getCompilationUnit(), editor.getDocument());

        Tree.Term node = ((CeylonPsi.TermPsi) value).getCeylonNode();
        String name = nodes_.get_().nameProposals(node)[0];
        ExtractValueResult result = refacto.extractValue(node, ((CeylonFile) file).getCompilationUnit(), name, false, false);

        final CeylonPsi.DeclarationPsi declaration = CeylonTreeUtil.createDeclarationFromText(project, result.getDeclaration());

        new WriteCommandAction(project, file) {

            @Override
            protected void run(@NotNull Result result) throws Throwable {
                introduceValue(declaration, value, project, file, editor);
            }
        }.execute();
    }

    private void introduceValue(CeylonPsi.DeclarationPsi declaration, PsiElement value, Project project, PsiFile file, Editor editor) {
        CeylonPsi.StatementPsi parentStatement = PsiTreeUtil.getParentOfType(value, CeylonPsi.StatementPsi.class);

        if (parentStatement == null) {
            return;
        }

        boolean isAssigningWholeStatement = parentStatement.getChildren().length == 1
                && parentStatement.getChildren()[0].getTextLength() == value.getTextLength();

        CeylonPsi.DeclarationPsi inserted;
        PsiElement identifier;

        if (isAssigningWholeStatement) {
            inserted = (CeylonPsi.DeclarationPsi) parentStatement.replace(declaration);
        } else {
            inserted = (CeylonPsi.DeclarationPsi) parentStatement.getParent().addBefore(declaration, parentStatement);
        }

        if (!isAssigningWholeStatement) {
            identifier = PsiTreeUtil.getChildOfType(inserted, CeylonPsi.IdentifierPsi.class);
            value.replace(identifier);
        }

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());

        // This sucks: the file was not reparsed, so getCeylonNode() returns null for elements we just inserted. This means
        // that CeylonReference (find usages) will not work, and when we rename the declaration, its usages won't be renamed.
        // Workaround: replace the whole document with the same content to force a reparse, then typecheck. Ugh.
        reparseFile(file, editor, project);
        inserted = (CeylonPsi.DeclarationPsi) file.findElementAt(inserted.getTextOffset()).getParent();
        identifier = PsiTreeUtil.getChildOfType(inserted, CeylonPsi.IdentifierPsi.class);

        editor.getCaretModel().moveToOffset(identifier.getTextOffset());

        Map<String, Object> myDataContext = new HashMap<>();
        myDataContext.put(CommonDataKeys.EDITOR.getName(), editor);
        myDataContext.put(CommonDataKeys.PSI_FILE.getName(), file);
        myDataContext.put(LangDataKeys.PSI_ELEMENT_ARRAY.getName(), new PsiElement[]{inserted});

        RefactoringSupportProvider supportProvider = LanguageRefactoringSupport.INSTANCE.forLanguage(CeylonLanguage.INSTANCE);
        if (supportProvider.isInplaceRenameAvailable(inserted, identifier)) {
            new VariableInplaceRenameHandler().invoke(project, editor, file, SimpleDataContext.getSimpleContext(myDataContext, null));
        }
    }

    private void reparseFile(PsiFile file, Editor editor, Project project) {
        editor.getDocument().replaceString(0, editor.getDocument().getTextLength(), editor.getDocument().getText());
        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

        if (file instanceof CeylonFile) {
            TypeCheckerInvoker.invokeTypeChecker((CeylonFile) file);
        }
    }

    private PsiElement selectValueToExtract(final Project project, final Editor editor, final PsiFile file) {
        FindContainingExpressionsVisitor visitor = new FindContainingExpressionsVisitor(editor.getCaretModel().getOffset());
        visitor.visitAny(PsiTreeUtil.findChildOfType(file, CeylonPsi.CompilationUnitPsi.class).getCeylonNode());
        List<CeylonPsi.TermPsi> allParentExpressions = toPsi(file, visitor.getElements());

        if (allParentExpressions.isEmpty()) {
            return null;
        } else if (allParentExpressions.size() == 1) {
            return allParentExpressions.get(0);
        } else {
            IntroduceTargetChooser.showChooser(editor, allParentExpressions,
                    new Pass<CeylonPsi.TermPsi>() {
                        public void pass(final CeylonPsi.TermPsi selectedValue) {
                            createAndIntroduceValue(project, editor, file, selectedValue);
                        }
                    },
                    new Function<CeylonPsi.TermPsi, String>() {
                        @Override
                        public String fun(CeylonPsi.TermPsi element) {
                            return element.getText();
                        }
                    }, "Expressions"
            );
        }
        return null;
    }

    private List<CeylonPsi.TermPsi> toPsi(PsiFile file, Tree.Term[] elements) {
        List<CeylonPsi.TermPsi> psi = new ArrayList<>();

        for (Tree.Term term : elements) {
            FindMatchingPsiNodeVisitor visitor = new FindMatchingPsiNodeVisitor(term, CeylonPsi.TermPsi.class);
            visitor.visitFile(file);
            CeylonCompositeElement element = visitor.getPsi();

            if (element != null) {
                psi.add((CeylonPsi.TermPsi) element);
            } else {
                System.err.println("Couldn't find PSI node for Node " + term);
            }
        }

        return psi;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {

    }
}
