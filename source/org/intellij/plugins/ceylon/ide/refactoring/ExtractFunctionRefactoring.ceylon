import ceylon.collection {
    ArrayList
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
    Condition
}
import com.intellij.psi {
    PsiDocumentManager,
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Visitor,
    Tree
}
import com.redhat.ceylon.ide.common.refactoring {
    createExtractFunctionRefactoring,
    DefaultRegion
}

import java.lang {
    JString=String
}

import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument,
    IdeaTextChange,
    IdeaCompositeChange
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    CeylonPsi,
    descriptions
}

shared class ExtractFunctionHandler() extends AbstractExtractHandler() {

    function findContainer(CeylonPsi.DeclarationPsi element) {
        assert (is CeylonPsi.DeclarationPsi? result 
            = PsiTreeUtil.findFirstParent(element,
                    object satisfies Condition<PsiElement> {
                        \ivalue(PsiElement cand)
                                => cand is CeylonPsi.DeclarationPsi
                                && cand!=element;
                    }));
        return result;
    }

    shared actual void extractToScope(Project project, Editor editor, CeylonFile file, TextRange range) {

        value declarations = ArrayList<Tree.Declaration>();
        
        file.compilationUnit.visit(object extends Visitor() {
            shared actual void visit(Tree.Declaration that) {
                if (exists start = that.startIndex,
                    exists end = that.endIndex,
                    start.intValue() <= range.startOffset,
                    end.intValue() >= range.endOffset) {
                    super.visit(that);
                    if (!that is Tree.AttributeDeclaration) {
                        declarations.add(that);
                    }
                }
            }
        });
        
        if (declarations.size>1) {
            value allParentScopes
                = psiElements<CeylonPsi.DeclarationPsi>(file, declarations);
            showChooser {
                editor = editor;
                expressions = allParentScopes;
                title = "Select target scope";
            }
                ((selectedValue)
                    => createAndIntroduceValue {
                        proj = project;
                        editor = editor;
                        file = file;
                        range = range;
                        ceylonNode = selectedValue.ceylonNode;
                    },
                (element)
                    => if (exists container = findContainer(element),
                           exists desc
                               = descriptions.descriptionForPsi {
                                   element = container;
                                   includeReturnType = false;
                               })
                    then desc
                    else "package " + element.ceylonNode.unit.\ipackage.qualifiedNameString);
        }
        else {
            super.extractToScope(project, editor, file, range);
        }
        
    }
    
    shared actual default [TextRange+]? extract(Project proj, Editor editor, CeylonFile file, TextRange range, Tree.Declaration? scope) {
        assert(exists localAnalysisResult = file.localAnalyzer?.result);
        assert(exists phasedUnit = localAnalysisResult.lastPhasedUnit);
        
        value refactoring = if (exists typecheckedRootNode = localAnalysisResult.typecheckedRootNode)
        then createExtractFunctionRefactoring {
            doc = IdeaDocument(editor.document);
            selectionStart = range.startOffset;
            selectionEnd = range.endOffset;
            rootNode = typecheckedRootNode;
            tokens = localAnalysisResult.tokens;
            target = scope;
            moduleUnits = [];
            vfile = phasedUnit.unitFile;
        }
        else null;

        if (exists refactoring) {

            return object extends WriteCommandAction<[TextRange+]?>(proj, file) {
                shared actual void run(Result<[TextRange+]?> result) {

                    switch (change = refactoring.build())
                    case (is IdeaTextChange) {
                        change.applyOnProject(proj);
                    }
                    case (is IdeaCompositeChange) {
                        change.applyChanges(proj);
                    }
                    else {}

                    function textRange(DefaultRegion r) => TextRange.from(r.start, r.length);

                    if (exists dec = refactoring.decRegion,
                        exists ref = refactoring.refRegion) {
                        editor.selectionModel.setSelection(dec.start, dec.end);
                        value newId = editor.document.getText(textRange(dec));

                        for (dupe in refactoring.dupeRegions) {
                            editor.document.replaceString(
                                dupe.start, 
                                dupe.start + dupe.length, 
                                JString(newId));
                        }

                        result.setResult([dec, ref, for (dupe in refactoring.dupeRegions) dupe].collect(textRange));
                    }

                    PsiDocumentManager.getInstance(project).commitAllDocuments();
                }
            }.execute().resultObject;
        }

        return null;
    }
}
