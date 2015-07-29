import ceylon.interop.java {
    Iter=CeylonIterable
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
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.util {
    ProcessingContext,
    PlatformIcons
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager,
    FindScopeVisitor
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
    Unit,
    DeclarationWithProximity,
    ModelUtil {
        isConstructor
    },
    Scope,
    FunctionOrValue
}

import javax.swing {
    Icon
}

shared object ideaCompletionManager extends IdeCompletionManager() {
    shared void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet result, Tree.CompilationUnit cu) {
        value isSecondLevel = parameters.invocationCount >= 2;
        value element = parameters.originalPosition;
        variable Integer startOffset = element.textOffset;
        variable Integer stopOffset = element.textOffset + element.textLength;
        variable Boolean isDot = false;

        if (element.textOffset > 1) {
            value doc = parameters.editor.document;
            value range = TextRange(element.textOffset - 1, element.textOffset);
            
            if (doc.getText(range).equals(".")) {
                isDot = true;
                startOffset = element.textOffset - 2;
                stopOffset = element.textOffset;
            }
        }

        value visitor = FindNodeVisitor(startOffset, stopOffset);
        cu.visit(visitor);

        if (exists node = visitor.node) {
            value scopeVisitor = FindScopeVisitor(node);
            scopeVisitor.visit(cu);
            value scope = scopeVisitor.scope else cu.scope;

            for (decl in Iter(getProposals(node, scope, "", isDot, cu).values())) {
                if (isSecondLevel) {
                    addSecondLevelCompletions(decl, cu.unit, scope, result);
                } else {
                    if (!isQualifiedType(node) || isConstructor(decl.declaration) || decl.declaration.staticallyImportable) {
                        result.addElement(MyLookupElementBuilder(decl.declaration, cu.unit, true).lookupElement);
                    }
                    result.addElement(MyLookupElementBuilder(decl.declaration, cu.unit, false).lookupElement);
                }
            }
            
            if (!isDot) {
                for (decl in Iter(getFunctionProposals(node, scope, "", isDot).values())) {
                    result.addElement(MyLookupElementBuilder(decl.declaration, cu.unit, true).lookupElement);
                }
            }
            
            if (!isSecondLevel) {
                result.addLookupAdvertisement("Call again to toggle second-level completions");
            }
        }
    }
    
    void addSecondLevelCompletions(DeclarationWithProximity dwp, Unit unit, Scope scope, CompletionResultSet result) {
        value decl = dwp.declaration;
        
        if (is Value decl) {
            value matchingMembers = decl.type.declaration.getMatchingMemberDeclarations(unit, scope, "", 0);
            
            for (dwp2 in Iter(matchingMembers.values())) {
                if (is FunctionOrValue|Class member = dwp2.declaration, !isConstructor(member)) {
                    result.addElement(MyLookupElementBuilder(member, unit, true, dwp.declaration).lookupElement);
                }
            }
        } else if (is Class decl) {
            value matchingMembers = decl.type.declaration.getMatchingMemberDeclarations(unit, scope, "", 0);
            
            for (dwp2 in Iter(matchingMembers.values())) {
                if (isConstructor(dwp2.declaration)) {
                    result.addElement(MyLookupElementBuilder(dwp2.declaration, unit, true, dwp.declaration).lookupElement);
                }
            }
        }
    }
}

class MyLookupElementBuilder(Declaration decl, Unit unit, Boolean allowInvocation, Declaration? parentDecl = null) {

    String text = if (exists name = parentDecl?.nameAsString) then "``name``.``decl.nameAsString``" else decl.nameAsString;
    variable String tailText = "";
    variable Boolean grayTailText = false;
    variable Icon? icon = null;
    variable String? typeText = null;
    variable InsertHandler<LookupElement>? handler = null;

    void visitFunction(Function fun) {
        if (fun.annotation) {
            icon = PlatformIcons.\iANNOTATION_TYPE_ICON;
        } else {
            icon = PlatformIcons.\iMETHOD_ICON;
            
            if (allowInvocation) {
                value params = Iter(fun.firstParameterList.parameters).map((p) => p.type.declaration.name + " " + p.name);
                tailText = "(``", ".join(params)``)";
                // see https://github.com/ceylon/ceylon-compiler/issues/2255
                value foo = if (fun.declaredVoid) then "void" else fun.typeDeclaration.name;
                typeText = foo;
                handler = functionInsertHandler;
            }
        }
    }

    void visitValue(Value val) {
        if (is Class t = val.type?.declaration, t.name.first?.lowercase else false) {
            icon = PlatformIcons.\iANONYMOUS_CLASS_ICON;
            handler = declarationInsertHandler;
        } else {
            icon = PlatformIcons.\iPROPERTY_ICON;
            typeText = val.typeDeclaration?.name;
        }
    }

    void visitClass(Class klass) {
        icon = PlatformIcons.\iCLASS_ICON;
        tailText = " (``klass.container.qualifiedNameString``)";
        grayTailText = true;
        handler = declarationInsertHandler;
    }

    void visitInterface(Interface int) {
        icon = PlatformIcons.\iINTERFACE_ICON;
        tailText = " (``int.container.qualifiedNameString``)";
        grayTailText = true;
        handler = declarationInsertHandler;
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
