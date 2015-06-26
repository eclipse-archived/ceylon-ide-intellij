import ceylon.interop.java {
    CeylonIterable
}
import com.intellij.codeInsight.completion {
    CompletionParameters,
    CompletionResultSet
}
import com.intellij.codeInsight.lookup {
    LookupElementBuilder
}
import com.intellij.util {
    ProcessingContext,
    PlatformIcons
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import com.redhat.ceylon.ide.common.util {
    FindNodeVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    Function,
    Value
}
import javax.swing {
    Icon
}

shared object ideaCompletionManager extends IdeCompletionManager() {
    shared void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet result, Tree.CompilationUnit cu) {
        value element = parameters.originalPosition;
        
        value visitor = FindNodeVisitor(element.textOffset, element.textOffset + element.textLength);
        cu.visit(visitor);
        
        if (exists node = visitor.node) {
            for (decl in CeylonIterable(getProposals(node, cu.scope, "", true, cu).values())) {
                
                print(decl.declaration);
                variable String tailText = "";
                variable Icon? icon = null;
                
                if (is Function fun = decl.declaration) {
                    value params = CeylonIterable(fun.firstParameterList.parameters).map((p) => p.type.declaration.name + " " + p.name);
                    tailText = "(``", ".join(params)``)";
                    icon = PlatformIcons.\iMETHOD_ICON;
                } else if (is Value val = decl.declaration) {
                    icon = PlatformIcons.\iPROPERTY_ICON;
                }
                
                result.addElement(
                    LookupElementBuilder.create(decl.declaration.nameAsString)
                        .withTailText(tailText)
                        .withIcon(icon)
                );
            }
        }
    }
}
