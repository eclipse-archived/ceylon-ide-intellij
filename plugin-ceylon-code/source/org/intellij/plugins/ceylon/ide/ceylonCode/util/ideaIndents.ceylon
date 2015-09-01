import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.intellij.openapi.editor {
    Document
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.intellij.openapi.util {
    TextRange
}

// TODO create settings for Ceylon code style
shared object ideaIndents satisfies Indents<Document> {
    
    shared actual String getLine(Node node, Document doc) {
        value line = node.token.line - 1;
        return doc.getText(TextRange(doc.getLineStartOffset(line), doc.getLineEndOffset(line)));
    }
    
    shared actual Integer indentSpaces => 4;
    
    shared actual Boolean indentWithSpaces => true;
    
    shared actual String getDefaultLineDelimiter(Document? document) => "\n";

}