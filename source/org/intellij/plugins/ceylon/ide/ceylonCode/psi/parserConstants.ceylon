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
    shared JList<Integer> nodesAllowedAtEof = Arrays.asList(CeylonLexer.eof,
        CeylonLexer.ws, CeylonLexer.lineComment, CeylonLexer.multiComment);

    // Leaves which will be wrapped in a CeylonCompositeElement, for example to allow refactoring them
    shared JList<IElementType> leavesToWrap = Arrays.asList(CeylonTypes.identifier,
        CeylonTypes.naturalLiteral, CeylonTypes.functionLiteral, CeylonTypes.stringLiteral,
        CeylonTypes.valueModifier, CeylonTypes.functionModifier);

    shared Key<Node> ceylonNodeKey = Key<Node>("CEYLON-SPEC_NODE");
    shared Key<Anything()> postParseAction = Key<Anything()>("POST-PARSE-ACTION");
}

