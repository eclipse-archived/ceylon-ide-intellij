import ceylon.collection {
    LinkedList
}

import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker.parser {
    CeylonLexer,
    RecognitionError
}
import com.redhat.ceylon.compiler.typechecker.tree {
    ...
}

import java.util {
    ArrayList
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile
}

"A visitor that visits a compilation unit returned by
 [[com.redhat.ceylon.compiler.typechecker.parser::CeylonParser]] to gather errors and
  warnings."
shared class ErrorsVisitor(Tree.CompilationUnit compilationUnit, CeylonFile file) extends Visitor() {

    value messages = LinkedList<[Message, TextRange?]>();

    shared actual void visitAny(Node that) {
        for (Message error in ArrayList(that.errors)) {
            if (exists start = that.startIndex,
                exists end = that.endIndex) {

                variable TextRange? range = null;
                if (is Tree.Declaration that,
                    exists id = that.identifier,
                    exists idStart = id.startIndex,
                    exists idEnd = id.endIndex) {

                    range = TextRange(id.startIndex.intValue(), id.endIndex.intValue());
                } else {
                    range = TextRange(that.startIndex.intValue(), that.endIndex.intValue());
                }

                if (is RecognitionError error,
                    is CommonToken token = error.recognitionException.token) {

                    if (token.type == CeylonLexer.\iEOF) {
                        // we can't underline EOF, so we try to underline the last word instead
                        variable Integer lastNonWS = file.textLength - 1;
                        while (lastNonWS>=0 && (file.text[lastNonWS]?.whitespace else false)) {
                            lastNonWS --;
                        }
                        variable Integer wordStart = lastNonWS;
                        while (wordStart>=0 && !(file.text[wordStart]?.whitespace else false)) {
                            wordStart --;
                        }
                        if (wordStart<lastNonWS) {
                            range = TextRange(wordStart + 1, lastNonWS + 1);
                        } else {
                            range = TextRange(token.startIndex - 1, token.stopIndex);
                        }
                    } else {
                        range = TextRange(token.startIndex, token.stopIndex + 1);
                    }
                }

                messages.add([error, range]);
            }
        }
        super.visitAny(that);
    }

    shared actual void handleException(Exception e, Node that) {
        e.printStackTrace();
    }
    
    shared {[Message, TextRange?]*} extractMessages() {
        compilationUnit.visit(this);
        return messages;
    }
}
