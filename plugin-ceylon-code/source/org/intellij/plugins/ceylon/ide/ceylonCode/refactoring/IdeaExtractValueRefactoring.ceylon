import com.intellij.openapi.application {
    Result
}
import com.intellij.openapi.command {
    WriteCommandAction
}
import com.intellij.openapi.editor {
    Document,
    Editor
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
    PsiFile,
    PsiDocumentManager,
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
    ExtractValueRefactoring
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Type,
    Declaration
}

import java.lang {
    JString=String
}
import java.util {
    List,
    ArrayList
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    IdeaTextChange,
    DocumentWrapper
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    CeylonPsi
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    VirtualFileVirtualFile
}

shared class ExtractValueHandler() extends AbstractExtractHandler() {

    shared actual TextRange? extract(Project project, Editor editor, PsiFile file, PsiElement val) {
        assert(is CeylonFile file);
        assert(is CeylonPsi.TermPsi val);
        value node = val.ceylonNode;

        value refacto = IdeaExtractValueRefactoring(file, editor.document, node);
        value name = nodes.nameProposals(node).first;
        refacto.newName = name;

        return refacto.extractInFile(project, file);
    }
}

class IdeaExtractValueRefactoring(CeylonFile file, Document document, Node _node)
        satisfies ExtractValueRefactoring<TextRange> {

    editorPhasedUnit => file.phasedUnit;

    shared actual List<PhasedUnit> getAllUnits() => ArrayList<PhasedUnit>();
    shared actual Boolean searchInFile(PhasedUnit pu) => false;
    shared actual Boolean searchInEditor() => false;
    shared actual Tree.CompilationUnit rootNode => file.compilationUnit;
    shared variable actual Boolean canBeInferred = false;
    shared actual variable Type? type = null;
    shared actual variable Boolean getter = false;

    shared actual List<TextRange> dupeRegions => ArrayList<TextRange>();

    shared actual EditorData editorData => object satisfies EditorData {
        shared actual List<CommonToken> tokens => file.tokens;
        shared actual Tree.CompilationUnit rootNode => file.compilationUnit;
        shared actual Node node => _node;
        shared actual VirtualFile? sourceVirtualFile
                => VirtualFileVirtualFile(file.virtualFile, ModuleUtil.findModuleForFile(file.virtualFile, file.project));
    };

    shared variable actual String? internalNewName = "";
    shared actual variable Boolean explicitType = false;

    shared actual variable TextRange? typeRegion = null;
    shared actual variable TextRange? decRegion = null;
    shared actual variable TextRange? refRegion = null;

    shared actual TextRange newRegion(Integer start, Integer length) => TextRange.from(start, length);

    shared TextRange? extractInFile(Project myProject, PsiFile file) {
        value tfc = IdeaTextChange(DocumentWrapper(document));
        build(tfc);

        return object extends WriteCommandAction<TextRange?>(myProject, file) {
            shared actual void run(Result<TextRange?> result) {
                tfc.apply();

                if (exists reg = decRegion) {
                    value newId = document.getText(reg);
                    for (dupe in dupeRegions) {
                        document.replaceString(dupe.startOffset, dupe.endOffset, JString(newId));
                    }
                }

                PsiDocumentManager.getInstance(project).commitAllDocuments();
                
                result.setResult(decRegion);
            }
        }.execute().resultObject;
    }
    shared actual Boolean inSameProject(Declaration decl) => true; // TODO
}
