import com.intellij.codeInsight.hint {
    HintManager
}
import com.intellij.lang {
    Language
}
import com.intellij.lang.refactoring {
    InlineActionHandler
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
import com.intellij.psi {
    PsiElement
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.io {
    VirtualFile
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}
import com.redhat.ceylon.ide.common.refactoring {
    isInlineRefactoringAvailable,
    InlineRefactoring
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.util {
    List,
    ArrayList
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.lang {
    ceylonLanguage
}
import org.intellij.plugins.ceylon.ide.model {
    getModelManager
}
import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument,
    IdeaCompositeChange
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    CeylonCompositeElement
}

shared class InlineAction() extends InlineActionHandler() {

    shared actual Boolean canInlineElement(PsiElement element) {
        if (is CeylonFile file = element.containingFile,
            is CeylonCompositeElement element,
            is Tree.Declaration td = element.ceylonNode,
            exists decl = td.declarationModel) {

            return isInlineRefactoringAvailable {
                declaration = decl;
                rootNode = file.compilationUnit;
                inSameProject = true;
            };
        }

        return false;
    }

    shared actual void inlineElement(Project proj, Editor editor, PsiElement element) {
        assert (exists modelManager = getModelManager(proj));
        try {
            modelManager.pauseAutomaticModelUpdate();

            if (is CeylonFile file = element.containingFile,
                exists localAnalysisResult = file.localAnalyzer?.result,
                exists phasedUnit = localAnalysisResult.typecheckedPhasedUnit,
                exists node
                        = nodes.findNode {
                            node = phasedUnit.compilationUnit;
                            tokens = localAnalysisResult.tokens;
                            startOffset = editor.selectionModel.selectionStart;
                            endOffset = editor.selectionModel.selectionEnd;
                        },
                is Declaration decl = nodes.getReferencedDeclaration(node)) {

                value refactoring = IdeaInlineRefactoring {
                    phasedUnit = phasedUnit;
                    theTokens = localAnalysisResult.tokens;
                    node = node;
                    decl = decl;
                    editor = editor;
                };
                value dialog = InlineDialog(proj, element, refactoring);

                if (dialog.showAndGet()) {
                    if (is String availability = refactoring.checkAvailability()) {
                        HintManager.instance.showErrorHint(editor, availability);
                        return;
                    }

                    value change = IdeaCompositeChange();
                    refactoring.build(change);

                    object extends WriteCommandAction<Nothing>(proj, file) {
                        run(Result<Nothing> result) => change.applyChanges(project);
                    }.execute();
                }
            }

        } finally {
            modelManager.resumeAutomaticModelUpdate(0);
        }
    }

    shared actual Boolean isEnabledForLanguage(Language l) => l == ceylonLanguage;
}

class IdeaInlineRefactoring(PhasedUnit phasedUnit, List<CommonToken> theTokens, Node node, Declaration decl, Editor editor) 
        satisfies InlineRefactoring {

    editorPhasedUnit => phasedUnit;

    shared class IdeaInlineData() satisfies InlineData {
        shared actual Declaration declaration => decl;
        
        shared actual variable Boolean delete = true;
        
        shared actual CommonDocument doc = IdeaDocument(editor.document);
        
        shared actual variable Boolean justOne = false;
        
        shared actual Node node => outer.node;
        
        shared actual Tree.CompilationUnit rootNode => phasedUnit.compilationUnit;
        
        shared actual VirtualFile? sourceVirtualFile => null;
        
        shared actual List<CommonToken> tokens => theTokens;
    }
    
    shared actual IdeaInlineData editorData = IdeaInlineData();
    
    shared actual List<PhasedUnit> getAllUnits() => ArrayList<PhasedUnit>();
    
    shared actual Tree.CompilationUnit rootNode => phasedUnit.compilationUnit;
    
    shared actual Boolean searchInEditor() => true;
    
    shared actual Boolean searchInFile(PhasedUnit pu)
            => pu.unit != editorPhasedUnit.unit;

    shared actual Boolean inSameProject(Declaration decl) => true; // TODO
}
