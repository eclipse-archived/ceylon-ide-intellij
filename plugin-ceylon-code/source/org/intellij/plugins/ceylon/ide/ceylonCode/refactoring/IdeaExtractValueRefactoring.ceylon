import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.refactoring {
    CommonExtractValueRefactoring
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

shared class IdeaExtractValueRefactoring(Tree.CompilationUnit cu, Document document) satisfies CommonExtractValueRefactoring {

    shared actual List<PhasedUnit> getAllUnits() => ArrayList<PhasedUnit>();
    shared actual Boolean searchInFile(PhasedUnit pu) => false;
    shared actual Boolean searchInEditor() => false;
    shared actual Tree.CompilationUnit rootNode => cu;
    
    shared actual String toString(Node node) 
            => document.getText(TextRange.from(node.startIndex.intValue(), node.stopIndex.intValue() - node.startIndex.intValue() + 1));
}

