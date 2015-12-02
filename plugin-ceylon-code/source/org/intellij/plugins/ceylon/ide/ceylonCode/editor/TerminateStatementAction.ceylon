import com.intellij.codeInsight.editorActions.smartEnter {
    SmartEnterProcessor
}
import com.redhat.ceylon.ide.common.editor {
    AbstractTerminateStatementAction
}
import com.intellij.openapi.editor {
    Document,
    Editor
}
import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    InsertEdit,
    TextEdit,
    TextChange,
    IdeaDocumentChanges
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.intellij.openapi.util {
    TextRange
}
import java.util {
    List
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import org.antlr.runtime {
    CommonToken,
    ANTLRStringStream,
    CommonTokenStream
}
import com.redhat.ceylon.compiler.typechecker.parser {
    CeylonParser,
    CeylonLexer
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast
}

shared class TerminateStatementAction()
        extends SmartEnterProcessor()
        satisfies AbstractTerminateStatementAction
            <Document,InsertEdit,TextEdit,TextChange>
                & IdeaDocumentChanges {
    
    shared actual void applyChange(TextChange change) {
        change.apply();
    }
    
    shared actual [DefaultRegion, String] getLineInfo(Document doc, Integer line) {
        value region = DefaultRegion(
            doc.getLineStartOffset(line),
            doc.getLineEndOffset(line) - doc.getLineStartOffset(line)
        );
        value range = TextRange.from(region.start, region.length);
        return [region, doc.getText(range)];
    }
    
    shared actual TextChange newChange(String desc, Document doc)
            => TextChange(doc);
    
    shared actual [Tree.CompilationUnit, List<CommonToken>] parse(Document doc) {
        value stream = ANTLRStringStream(doc.text);
        value lexer = CeylonLexer(stream);
        value tokenStream = CommonTokenStream(lexer);
        value parser = CeylonParser(tokenStream);
        value cu = parser.compilationUnit();
        
        value toks = unsafeCast<List<CommonToken>>(tokenStream.tokens);
        
        return [cu, toks];
    }
    
    shared actual Boolean process(Project? project, Editor editor,
        PsiFile? psiFile) {
        
        value line = editor.document.getLineNumber(editor.caretModel.offset);
        terminateStatement(editor.document, line);
        
        return true;
    }
    
    shared actual Character getChar(Document doc, Integer offset)
            => doc.text[offset] else ' ';
    
    
}
