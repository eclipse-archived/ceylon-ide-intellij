import ceylon.interop.java {
    javaClass,
    javaString
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
    Pass,
    Condition
}
import com.intellij.psi {
    PsiFile,
    PsiDocumentManager,
    PsiElement
}
import com.intellij.refactoring {
    IntroduceTargetChooser
}
import com.intellij.util {
    Function
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Visitor,
    Tree
}
import com.redhat.ceylon.ide.common.refactoring {
    createExtractFunctionRefactoring
}

import java.lang {
    JString=String
}
import java.util {
    List,
    ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument,
    IdeaTextChange,
    IdeaCompositeChange
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsi,
    ceylonDeclarationDescriptionProvider
}
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve {
    FindMatchingPsiNodeVisitor
}
import com.intellij.psi.util {
    PsiTreeUtil
}

shared class ExtractFunctionHandler() extends AbstractExtractHandler() {

    List<CeylonPsi.DeclarationPsi> toPsi(PsiFile file, List<Tree.Declaration> elements) {
        value psi = ArrayList<CeylonPsi.DeclarationPsi>();
        for (term in elements) {
            value visitor = FindMatchingPsiNodeVisitor(term, javaClass<CeylonPsi.DeclarationPsi>());
            visitor.visitFile(file);

            if (is CeylonPsi.DeclarationPsi element = visitor.psi) {
                psi.add(element);
            } else {
                print("Couldn't find PSI node for Node " + term.string);
            }
        }
        return psi;
    }

    shared actual void extractToScope(Project myProject, Editor editor, CeylonFile file, TextRange range) {
        
        object containerFinder extends Visitor() {
            shared List<Tree.Declaration> declarations =
                ArrayList<Tree.Declaration>();

            shared actual void visit(Tree.Declaration that) {
                if (that.startIndex.intValue()<=range.startOffset,
                        that.endIndex.intValue()>=range.endOffset) {
                        super.visit(that);
                    if (!that is Tree.AttributeDeclaration) {
                        declarations.add(that);
                    }
                }
            }
        }
        file.compilationUnit.visit(containerFinder);
        
        if (containerFinder.declarations.size()>1) {
            \IceylonDeclarationDescriptionProvider provider
                = ceylonDeclarationDescriptionProvider;
            IntroduceTargetChooser.showChooser(editor,
                toPsi(file, containerFinder.declarations),
                object extends Pass<CeylonPsi.DeclarationPsi>() {
                    pass(CeylonPsi.DeclarationPsi selectedValue)
                            => createAndIntroduceValue(myProject, editor, file, range, selectedValue.ceylonNode);
                },
                object satisfies Function<CeylonPsi.DeclarationPsi,JString> {
                    fun(CeylonPsi.DeclarationPsi element)
                            => if (is CeylonPsi.DeclarationPsi container
                                        = PsiTreeUtil.findFirstParent(element,
                                            object satisfies Condition<PsiElement> {
                                                \ivalue(PsiElement cand)
                                                        => cand is CeylonPsi.DeclarationPsi
                                                        && cand!=element;
                                            }),
                                    exists desc = provider.getDescription(container, true, true))
                                then javaString(desc)
                                else javaString("package " + element.ceylonNode.unit.\ipackage.qualifiedNameString);
                },
                "Select target scope");
        }
        else {
            super.extractToScope(myProject, editor, file, range);
        }
        
    }
    
    shared actual default TextRange? extract(Project myProject, Editor editor, CeylonFile file, TextRange range, Tree.Declaration? scope) {
        
        value refacto = createExtractFunctionRefactoring {
            doc = IdeaDocument(editor.document);
            selectionStart = range.startOffset;
            selectionEnd = range.endOffset;
            rootNode = file.compilationUnit;
            tokens = file.tokens;
            target = scope;
            moduleUnits = empty;
            vfile = file.phasedUnit.unitFile;
        };

        if (exists refacto) {

            return object extends WriteCommandAction<TextRange?>(myProject, file) {
                shared actual void run(Result<TextRange?> result) {

                    switch (change = refacto.build())
                    case (is IdeaTextChange) {
                        change.applyOnProject(myProject);
                    }
                    case (is IdeaCompositeChange) {
                        change.applyChanges(myProject);
                    }
                    else {}

                    if (exists reg = refacto.decRegion) {
                        value range = TextRange.from(reg.start, reg.length);
                        editor.selectionModel.setSelection(reg.start, reg.end);
                        value newId = editor.document.getText(range);

                        for (dupe in refacto.dupeRegions) {
                            editor.document.replaceString(dupe.start, dupe.start + dupe.length, JString(newId));
                        }

                        result.setResult(range);
                    }

                    PsiDocumentManager.getInstance(project).commitAllDocuments();
                }
            }.execute().resultObject;
        }

        return null;
    }
}
