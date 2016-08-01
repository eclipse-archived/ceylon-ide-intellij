import com.redhat.ceylon.compiler.typechecker.parser {
    CeylonLexer
}
import com.intellij.openapi.util {
    Key
}
import java.util {
    Arrays,
    JList=List
}
import com.intellij.psi.tree {
    IElementType
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}

shared object parserConstants {
    shared JList<Integer> _NODES_ALLOWED_AT_EOF = Arrays.asList(CeylonLexer.eof,
        CeylonLexer.ws, CeylonLexer.lineComment, CeylonLexer.multiComment);

    // Leaves which will be wrapped in a CeylonCompositeElement, for example to allow refactoring them
    shared JList<IElementType> _LEAVES_TO_WRAP = Arrays.asList(CeylonTypes.identifier,
        CeylonTypes.naturalLiteral, CeylonTypes.functionLiteral, CeylonTypes.stringLiteral);

    shared Key<Node> _CEYLON_NODE_KEY = Key<Node>("CEYLON-SPEC_NODE");
}

