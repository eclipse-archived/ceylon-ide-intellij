import ceylon.interop.java {
    Iter = CeylonIterable
}

import com.intellij.codeInsight.completion {
    CompletionParameters,
    CompletionResultSet,
    InsertHandler
}
import com.intellij.codeInsight.lookup {
    LookupElementBuilder,
    LookupElement
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
    Value,
    Declaration,
    Class,
    Interface,
    TypeAlias,
    Unit
}

import javax.swing {
    Icon
}
import com.intellij.openapi.util {
    IconLoader
}

shared object ideaCompletionManager extends IdeCompletionManager() {
    shared void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet result, Tree.CompilationUnit cu) {
        value element = parameters.originalPosition;
        
        value visitor = FindNodeVisitor(element.textOffset, element.textOffset + element.textLength);
        cu.visit(visitor);
        
        if (exists node = visitor.node) {
            for (decl in Iter(getProposals(node, cu.scope, "", false, cu).values())) {
                result.addElement(MyLookupElementBuilder(decl.declaration, cu.unit).lookupElement);
            }
        }
    }
}

class MyLookupElementBuilder(Declaration decl, Unit unit) {
    
    String text = decl.nameAsString;
    variable String tailText = "";
    variable Boolean grayTailText = false; 
    variable Icon? icon = null;
    variable String? typeText = null;
    variable InsertHandler<LookupElement>? handler = null;
        
    void visitFunction(Function fun) {
        if (fun.annotation) {
            icon = PlatformIcons.\iANNOTATION_TYPE_ICON;
        } else {
            value params = Iter(fun.firstParameterList.parameters).map((p) => p.type.declaration.name + " " + p.name);
            tailText = "(``", ".join(params)``)";
            icon = PlatformIcons.\iMETHOD_ICON;
            typeText = if (fun.declaredVoid) then "void" else fun.typeDeclaration.name;
        }
    }
    
    void visitValue(Value val) {
        if (is Class t = val.type.declaration, t.name.first?.lowercase else false) {
            icon = IconLoader.getIcon("/icons/object.png");
            handler = classInsertHandler;
        } else {
            icon = PlatformIcons.\iPROPERTY_ICON;
            typeText = val.typeDeclaration.name;
        }
    }
    
    void visitClass(Class klass) {
        icon = PlatformIcons.\iCLASS_ICON;
        tailText = " (``klass.container.qualifiedNameString``)";
        grayTailText = true;
        handler = classInsertHandler;
    }
    
    void visitInterface(Interface int) {
        icon = PlatformIcons.\iINTERFACE_ICON;
        tailText = " (``int.container.qualifiedNameString``)";
        grayTailText = true;
        handler = classInsertHandler;
    }
    
    void visitAlias(TypeAlias typeAlias) {
        // TODO create an icon for aliases
    }
    
    void visit(Declaration decl) {
        if (is Function decl) {
            visitFunction(decl);
        } else if (is Value decl) {
            visitValue(decl);
        } else if (is Class decl) {
            visitClass(decl);
        } else if (is Interface decl) {
            visitInterface(decl);
        } else if (is TypeAlias decl) {
            visitAlias(decl);
        }
    }
    
    visit(decl);
    
    shared LookupElement lookupElement = LookupElementBuilder.create([decl, unit], text)
        .withTailText(tailText, grayTailText)
        .withTypeText(typeText)
        .withIcon(icon)
        .withInsertHandler(handler);
}
