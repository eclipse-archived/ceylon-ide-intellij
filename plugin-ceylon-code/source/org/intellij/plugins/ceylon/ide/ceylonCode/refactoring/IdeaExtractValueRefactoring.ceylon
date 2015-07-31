import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.refactoring {
    ExtractValueRefactoring
}
import java.util {
    List,
    ArrayList
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}
import org.antlr.runtime {
    CommonToken
}
import com.redhat.ceylon.compiler.typechecker.io {
    VirtualFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    PsiFileVirtualFile
}

shared class IdeaExtractValueRefactoring(CeylonFile file, Document document, Node _node) satisfies ExtractValueRefactoring {
    
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
        shared actual VirtualFile? sourceVirtualFile => PsiFileVirtualFile(file);
    };
    
    shared variable actual String? internalNewName = "";
    shared actual variable Boolean explicitType = false;
}
