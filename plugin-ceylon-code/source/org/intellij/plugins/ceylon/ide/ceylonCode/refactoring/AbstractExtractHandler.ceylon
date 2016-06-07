import com.intellij.psi {
    PsiElement,
    PsiFile
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.editor {
    Editor
}
import com.redhat.ceylon.ide.common.refactoring {
    FindContainingExpressionsVisitor
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import ceylon.interop.java {
    javaClass,
    createJavaObjectArray
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi,
    CeylonFile
}
import java.lang {
    ObjectArray,
    JString=String
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    FindMatchingPsiNodeVisitor
}
import com.intellij.refactoring {
    IntroduceTargetChooser,
    RefactoringActionHandler
}
import com.intellij.openapi.util {
    Pass,
    TextRange
}
import com.intellij.util {
    Function
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.application {
    Result
}
import java.util {
    HashMap,
    List,
    ArrayList
}
import com.intellij.openapi.actionSystem {
    CommonDataKeys,
    LangDataKeys,
    DataContext
}
import com.intellij.openapi.actionSystem.impl {
    SimpleDataContext
}

shared abstract class AbstractExtractHandler() satisfies RefactoringActionHandler {

    shared formal TextRange? extract(Project project, Editor editor, PsiFile file, TextRange range);

    shared actual void invoke(Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        if (is CeylonFile psiFile,
            exists _ = psiFile.ensureTypechecked(),
            exists range = selectValueToExtract(project, editor, psiFile)) {
            createAndIntroduceValue(project, editor, psiFile, range);
        }
    }

    shared actual void invoke(Project p, ObjectArray<PsiElement> elements, DataContext ctx) {
        // Do nothing
    }

    TextRange? selectValueToExtract(Project project, Editor editor, PsiFile file) {
        if (editor.selectionModel.selectionStart < editor.selectionModel.selectionEnd) {
            return TextRange(editor.selectionModel.selectionStart, editor.selectionModel.selectionEnd);
        }

        value visitor = FindContainingExpressionsVisitor(editor.caretModel.offset);
        visitor.visitAny(PsiTreeUtil.findChildOfType(file, javaClass<CeylonPsi.CompilationUnitPsi>()).ceylonNode);

        value allParentExpressions = toPsi(file, visitor.elements);
        if (allParentExpressions.empty) {
            return null;
        } else if (allParentExpressions.size() == 1) {
            return allParentExpressions.get(0).textRange;
        } else {
            IntroduceTargetChooser.showChooser(
                editor,
                allParentExpressions,
                object extends Pass<CeylonPsi.TermPsi>() {
                    shared actual void pass(CeylonPsi.TermPsi selectedValue) {
                        createAndIntroduceValue(project, editor, file, selectedValue.textRange);
                    }
                },
                object satisfies Function<CeylonPsi.TermPsi,JString> {
                    shared actual JString fun(CeylonPsi.TermPsi element) {
                        return JString(element.text);
                    }
                },
                "Expressions");
        }
        return null;
    }

    void createAndIntroduceValue(Project _project, Editor editor, PsiFile file, TextRange range) {
        assert (is CeylonFile ceylonFile = file);

        if (exists newIdentifier = extract(_project, editor, file, range)) {
            object extends WriteCommandAction<Nothing>(_project, file) {
                shared actual void run(Result<Nothing> result) {
                    ceylonFile.forceReparse();
                }
            }.execute();

            PsiElement? identifierElement = file.findElementAt(newIdentifier.startOffset);
            if (! exists identifierElement) {
                return;
            }

            assert (is CeylonPsi.DeclarationPsi inserted = identifierElement.parent.parent);
            PsiElement? identifier = PsiTreeUtil.getChildOfType(inserted, javaClass<CeylonPsi.IdentifierPsi>());
            if (! exists identifier) {
                return;
            }
            editor.caretModel.moveToOffset(identifier.textOffset);
            value myDataContext = HashMap<JString,Object>();
            myDataContext.put(JString(CommonDataKeys.editor.name), editor);
            myDataContext.put(JString(CommonDataKeys.psiFile.name), file);
            myDataContext.put(JString(LangDataKeys.psiElementArray.name), createJavaObjectArray<PsiElement>({ inserted }));
            value handler = CeylonVariableRenameHandler(true);
            handler.invoke(_project, editor, file, SimpleDataContext.getSimpleContext(myDataContext, null));
        }
    }

    List<CeylonPsi.TermPsi> toPsi(PsiFile file, ObjectArray<Tree.Term> elements) {
        value psi = ArrayList<CeylonPsi.TermPsi>();

        for (Tree.Term term in elements) {
            value visitor = FindMatchingPsiNodeVisitor(term, javaClass<CeylonPsi.TermPsi>());
            visitor.visitFile(file);

            if (is CeylonPsi.TermPsi element = visitor.psi) {
                psi.add(element);
            } else {
                print("Couldn't find PSI node for Node " + term.string);
            }
        }
        return psi;
    }
}