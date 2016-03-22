import com.intellij.codeInsight.lookup {
    LookupElement
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
    Editor,
    Document
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
import com.redhat.ceylon.ide.common.refactoring {
    isInlineRefactoringAvailable,
    InlineRefactoring
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
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    InsertEdit,
    TextEdit,
    TextChange,
    IdeaDocumentChanges
}
import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonCompositeElement
}
import com.redhat.ceylon.ide.common.util {
    nodes
}

shared class InlineAction() extends InlineActionHandler() {

    shared actual Boolean canInlineElement(PsiElement element) {
        if (is CeylonFile file = element.containingFile,
            is CeylonCompositeElement element,
            is Tree.Declaration td = element.ceylonNode,
            is Declaration decl = td.declarationModel) {

            return isInlineRefactoringAvailable(decl, file.compilationUnit, true);
        }

        return false;
    }

    shared actual void inlineElement(Project _project, Editor editor, PsiElement element) {
        if (is CeylonFile file = element.containingFile) {
            value node = nodes.findNode(
                file.compilationUnit, file.tokens,
                editor.selectionModel.selectionStart,
                editor.selectionModel.selectionEnd
            );

            if (exists node,
                is Declaration decl = nodes.getReferencedDeclaration(node)) {

                value change = TextChange(editor.document);
                value refactoring = IdeaInlineRefactoring(file, node, decl, editor);
                value dialog = InlineDialog(_project, element, refactoring);

                if (dialog.showAndGet()) {
                    refactoring.build(change);

                    object extends WriteCommandAction<Nothing>(_project, file) {
                        shared actual void run(Result<Nothing>? result) {
                            change.apply(project);
                        }
                    }.execute();
                }
            }
        }
    }

    shared actual Boolean isEnabledForLanguage(Language l) => l == CeylonLanguage.\iINSTANCE;
}

class IdeaInlineRefactoring(CeylonFile file, Node node, Declaration decl, Editor editor) 
        satisfies InlineRefactoring<LookupElement,Document,InsertEdit,TextEdit,TextChange,TextChange>
                & IdeaDocumentChanges {

    shared actual void addChangeToChange(TextChange change, TextChange tc) {
        change.addAll(tc);
    }
    
    class IdeaInlineData() satisfies InlineData {
        shared actual Declaration declaration => decl;
        
        shared actual variable Boolean delete = true;
        
        shared actual Document doc => editor.document;
        
        shared actual variable Boolean justOne = false;
        
        shared actual Node node => outer.node;
        
        shared actual Tree.CompilationUnit rootNode => file.compilationUnit;
        
        shared actual VirtualFile? sourceVirtualFile => null;
        
        shared actual List<CommonToken> tokens => file.tokens;
    }
    
    shared actual InlineData editorData = IdeaInlineData();
    
    shared actual List<PhasedUnit> getAllUnits() => ArrayList<PhasedUnit>();
    
    shared actual TextChange newDocChange(Document doc) => TextChange(doc);
    
    shared actual TextChange newFileChange(PhasedUnit pu) => TextChange(pu);
    
    shared actual Tree.CompilationUnit rootNode => file.compilationUnit;
    
    shared actual Boolean searchInEditor() => true;
    
    shared actual Boolean searchInFile(PhasedUnit pu) => true;

    shared actual Boolean inSameProject(Declaration decl) => true; // TODO
}
