import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.\imodule {
    ModuleUtil
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    PsiFile
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
    ExtractValueRefactoring,
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}

import java.util {
    List,
    ArrayList
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    IdeaDocumentChanges,
    InsertEdit,
    TextEdit,
    TextChange,
    IdeaImportProposals,
    ideaImportProposals
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    VirtualFileVirtualFile
}

shared class IdeaExtractValueRefactoring(CeylonFile file, Document document, Node _node)
        satisfies ExtractValueRefactoring<CeylonFile, LookupElement, Document, InsertEdit, TextEdit, TextChange>
                & IdeaDocumentChanges {

    shared actual List<PhasedUnit> getAllUnits() => ArrayList<PhasedUnit>();
    shared actual Boolean searchInFile(PhasedUnit pu) => false;
    shared actual Boolean searchInEditor() => false;
    shared actual Tree.CompilationUnit rootNode => file.compilationUnit;
    shared variable actual Boolean canBeInferred = false;
    shared actual variable Type? type = null;
    shared actual variable Boolean getter = false;

    shared actual String toString(Node node)
            => document.getText(TextRange.from(node.startIndex.intValue(), node.stopIndex.intValue() - node.startIndex.intValue() + 1));

    shared actual EditorData? editorData => object satisfies EditorData {
        shared actual List<CommonToken>? tokens => null;
        shared actual Tree.CompilationUnit? rootNode => file.compilationUnit;
        shared actual Node? node => _node;
        shared actual VirtualFile? sourceVirtualFile
                => VirtualFileVirtualFile(file.virtualFile, ModuleUtil.findModuleForFile(file.virtualFile, file.project));
    };

    shared variable actual String? internalNewName = "";
    shared actual variable Boolean explicitType = false;

    shared actual variable DefaultRegion? typeRegion = null;
    shared actual variable DefaultRegion? decRegion = null;
    shared actual variable DefaultRegion? refRegion = null;

    shared actual DefaultRegion newRegion(Integer start, Integer length) => DefaultRegion(start, length);

    shared actual IdeaImportProposals importProposals => ideaImportProposals;

    shared DefaultRegion? extractInFile(Project myProject, PsiFile file) {
        TextChange tfc = TextChange(document);
        build(tfc);

        return object extends WriteCommandAction<DefaultRegion?>(myProject, file) {
            shared actual void run(Result<DefaultRegion?> result) {
                tfc.apply();

                result.setResult(decRegion);
            }
        }.execute().resultObject;
    }
}
