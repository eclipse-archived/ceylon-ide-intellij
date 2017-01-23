import ceylon.collection {
    LinkedList
}

import com.intellij.openapi.util {
    TextRange
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    ModuleSourceMapper,
    Warning
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Message,
    Node,
    UnexpectedError
}
import com.redhat.ceylon.compiler.typechecker.util {
    WarningSuppressionVisitor
}
import com.redhat.ceylon.ide.common.util {
    ErrorVisitor
}

import org.intellij.plugins.ceylon.ide.model {
    findProjectForFile
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}


"A visitor that visits a compilation unit returned by
 [[com.redhat.ceylon.compiler.typechecker.parser::CeylonParser]] to gather errors and
  warnings."
shared class ErrorsVisitor(Tree.CompilationUnit compilationUnit, CeylonFile file) extends ErrorVisitor() {

    value messages = LinkedList<[Message, TextRange?]>();

    handleException(Exception e, Node that) => e.printStackTrace();
    
    shared {[Message, TextRange?]*} extractMessages() {
        if (exists ceylonProject = findProjectForFile(file)) {
            compilationUnit.visit(WarningSuppressionVisitor(`Warning`,
                ceylonProject.configuration.suppressWarningsEnum));
        }
        compilationUnit.visit(this);

        if (file.name == "module.ceylon",
            exists project = findProjectForFile(file)) {

            value errors = project.build
                .messagesForSourceFile(file.virtualFile)
                .map((msg) => msg.typecheckerMessage)
                .narrow<ModuleSourceMapper.ModuleDependencyAnalysisError>();

            messages.addAll(errors.map((err) {
                value range = TextRange(
                    err.treeNode.startIndex.intValue(),
                    err.treeNode.endIndex.intValue()
                );
                return [err, range];
            }));
        }

        return messages;
    }

    shared actual void handleMessage(Integer startOffset, Integer endOffset,
        Integer startCol, Integer startLine, Message error) {
        if (error is UnexpectedError) {
            process.writeError(error.message);
        }
        else {
            messages.add([error, TextRange(startOffset, endOffset)]);
        }
    }

}
