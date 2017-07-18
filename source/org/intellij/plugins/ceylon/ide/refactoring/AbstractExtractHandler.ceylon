import com.intellij.openapi.actionSystem {
    CommonDataKeys,
    LangDataKeys,
    DataContext
}
import com.intellij.openapi.actionSystem.impl {
    SimpleDataContext
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange,
    Pair
}
import com.intellij.psi {
    PsiElement,
    PsiFile,
    PsiNamedElement
}
import com.intellij.psi.search {
    SearchScope
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.intellij.refactoring {
    RefactoringActionHandler
}
import com.intellij.refactoring.rename.inplace {
    VariableInplaceRenameHandler,
    VariableInplaceRenamer
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.refactoring {
    FindContainingExpressionsVisitor
}

import java.lang {
    Types {
        nativeString
    },
    ObjectArray,
    JString=String,
    overloaded
}
import java.util {
    HashMap,
    JList=List,
    Arrays,
    ArrayList
}

import org.intellij.plugins.ceylon.ide.model {
    getModelManager
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi,
    CeylonFile,
    CeylonCompositeElement
}
import org.intellij.plugins.ceylon.ide.resolve {
    FindMatchingPsiNodeVisitor
}

shared abstract class AbstractExtractHandler() satisfies RefactoringActionHandler {

    shared formal [TextRange+]? extract(Project project, Editor editor, CeylonFile file, TextRange range, Tree.Declaration? scope);

    shared default void extractToScope(Project project, Editor editor, CeylonFile file, TextRange range)
            => createAndIntroduceValue {
                proj = project;
                editor = editor;
                file = file;
                range = range;
                ceylonNode = null;
            };

    overloaded
    shared actual void invoke(Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        assert (exists modelManager = getModelManager(project));
        try {
            modelManager.pauseAutomaticModelUpdate();
            
            if (is CeylonFile psiFile, psiFile.localAnalyzer?.ensureTypechecked() exists) {
                extractSelectedExpression(project, editor, psiFile);
            }

        } finally {
            modelManager.resumeAutomaticModelUpdate(0);
        }
    }

    overloaded
    shared actual void invoke(Project p, ObjectArray<PsiElement> elements, DataContext ctx) {
        // Do nothing
    }

    void extractSelectedExpression(Project project, Editor editor, CeylonFile file) {

        value selectionModel = editor.selectionModel;
        if (selectionModel.selectionStart < selectionModel.selectionEnd) {
            extractToScope {
                project = project;
                editor = editor;
                file = file;
                range = TextRange(selectionModel.selectionStart,
                                  selectionModel.selectionEnd);
            };
        }
        else {
            value visitor = FindContainingExpressionsVisitor(editor.caretModel.offset);
            assert (exists cu = PsiTreeUtil.findChildOfType(file, `CeylonPsi.CompilationUnitPsi`));
            visitor.visitAny(cu.ceylonNode);

            value allParentExpressions
                = psiElements<CeylonPsi.TermPsi> {
                    file = file;
                    *visitor.elements
                };
            if (!nonempty allParentExpressions) {
                //noop
            }
            else if (allParentExpressions.size == 1) {
                extractToScope {
                    project = project;
                    editor = editor;
                    file = file;
                    range = allParentExpressions.first.textRange;
                };
            }
            else {
                showChooser {
                    editor = editor;
                    expressions = allParentExpressions;
                    title = "Select expression";
                }
                    ((selectedValue)
                        => extractToScope {
                            project = project;
                            editor = editor;
                            file = file;
                            range = selectedValue.textRange;
                        },
                    PsiElement.text);
            }
        }
    }

    function createContext(Editor editor, CeylonFile file, CeylonPsi.DeclarationPsi inserted) {
        value myDataContext = HashMap<JString,Object>();
        myDataContext[nativeString(CommonDataKeys.editor.name)] = editor;
        myDataContext[nativeString(CommonDataKeys.psiFile.name)] = file;
        myDataContext[nativeString(CommonDataKeys.psiElement.name)] = inserted;
        myDataContext[nativeString(LangDataKeys.psiElementArray.name)]
                    = ObjectArray<PsiElement>(0, inserted);
        return SimpleDataContext.getSimpleContext(myDataContext, null);
    }

    shared void createAndIntroduceValue(Project proj, Editor editor, CeylonFile file, TextRange range, Tree.Declaration? ceylonNode) {

        if (exists [newIdentifier, *usages] = extract(proj, editor, file, range, ceylonNode)) {
            object extends WriteCommandAction<Nothing>(proj, file) {
                run(Result<Nothing> result) => file.forceReparse();
            }.execute();

            PsiElement? identifierElement = file.findElementAt(newIdentifier.startOffset);
            if (! exists identifierElement) {
                return;
            }

            assert (is CeylonPsi.DeclarationPsi inserted = identifierElement.parent.parent);

            if (exists identifier = PsiTreeUtil.getChildOfType(inserted, `CeylonPsi.IdentifierPsi`)) {
                editor.caretModel.moveToOffset(identifier.textOffset);
                ExtractedVariableRenameHandler(usages)
                    .invoke(proj, editor, file,
                        createContext(editor, file, inserted));
            }
        }
    }

    shared T[] psiElements<T>(PsiFile file, {Node*} elements)
            given T satisfies CeylonCompositeElement
            => elements.map((term) {
                value visitor
                    = FindMatchingPsiNodeVisitor(term, Types.classForType<T>());
                visitor.visitFile(file);
                if (is T element = visitor.psi) {
                    return element;
                } else {
                    return null;
                }
            })
            .coalesced
            .sequence();

}

class ExtractedVariableRenameHandler(TextRange[] usages = [])
        extends VariableInplaceRenameHandler() {

    isAvailable(PsiElement? element, Editor editor, PsiFile file) => true;
    
    shared actual VariableInplaceRenamer createRenamer(PsiElement elementToRename, Editor editor) {
        assert (is PsiNamedElement elementToRename);

        return object extends VariableInplaceRenamer(elementToRename, editor) {

            shared actual void finish(Boolean success) {
                super.finish(success);
                if (success, is CeylonFile file = elementToRename.containingFile) {
                    object extends WriteCommandAction<Nothing>(myProject, file) {
                        run(Result<Nothing> result) => file.forceReparse();
                    }.execute();
                }
            }

            collectAdditionalElementsToRename(JList<Pair<PsiElement,TextRange>> stringUsages)
                    => noop();

            collectRefs(SearchScope referencesSearchScope)
                    => ArrayList(Arrays.asList(
                        for (r in usages)
                        if (exists e = elementToRename.containingFile.findReferenceAt(r.startOffset))
                        e));

            checkLocalScope() => elementToRename.containingFile;
        };
    }
    
}