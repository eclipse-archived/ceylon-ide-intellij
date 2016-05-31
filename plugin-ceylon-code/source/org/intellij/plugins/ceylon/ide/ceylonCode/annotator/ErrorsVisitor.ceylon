import ceylon.collection {
    LinkedList
}

import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker.tree {
    ...
}
import com.redhat.ceylon.ide.common.util {
    ErrorVisitor
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

"A visitor that visits a compilation unit returned by
 [[com.redhat.ceylon.compiler.typechecker.parser::CeylonParser]] to gather errors and
  warnings."
shared class ErrorsVisitor(Tree.CompilationUnit compilationUnit, CeylonFile file) extends ErrorVisitor() {

    value messages = LinkedList<[Message, TextRange?]>();

    shared actual void handleException(Exception e, Node that) {
        e.printStackTrace();
    }
    
    shared {[Message, TextRange?]*} extractMessages() {
        compilationUnit.visit(this);
        return messages;
    }

    shared actual void handleMessage(Integer startOffset, Integer endOffset,
        Integer startCol, Integer startLine, Message error) {

        messages.add([error, TextRange(startOffset, endOffset)]);
    }

}
