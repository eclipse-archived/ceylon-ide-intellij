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
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.util {
    ProcessingContext,
    PlatformIcons
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}
import com.redhat.ceylon.ide.common.util {
    OccurrenceLocation
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
    FunctionOrValue,
    Type,
    Reference,
    ClassOrInterface
}

import java.util {
    JList=List
}
import java.util.regex {
    Pattern
}

import javax.swing {
    Icon
}

import org.antlr.runtime {
    CommonToken
}


shared object ideaCompletionManager extends IdeCompletionManager<PhasedUnit, LookupElement, Document>() {
    
    shared void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet result, PhasedUnit pu) {
        value isSecondLevel = parameters.invocationCount >= 2;
        value element = parameters.originalPosition;
        value doc = parameters.editor.document;
        value params = object satisfies LocalAnalysisResult<Document> {
            shared actual Tree.CompilationUnit rootNode => pu.compilationUnit;
            shared actual PhasedUnit phasedUnit => pu;
            shared actual Document document => doc;
            shared actual JList<CommonToken>? tokens => pu.tokens;
        };
        value line = doc.getLineNumber(element.textOffset);
        print(line);
        value completions = getContentProposals(params, element.textOffset + 1, line, isSecondLevel, pu);
        
        for (completion in completions) {
            result.addElement(completion);
        }
        
        if (!isSecondLevel) {
            result.addLookupAdvertisement("Call again to toggle second-level completions");
        }
    }
    
    shared actual List<Pattern> proposalFilters => empty; 
    
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
    
    shared actual LookupElement newParametersCompletionProposal(Integer offset, 
            Type type, JList<Type> argTypes, Node node, PhasedUnit pu) {
        return MyLookupElementBuilder(type.declaration, node.unit, true).lookupElement;
    }
    
    shared actual Boolean showParameterTypes => true;
    shared actual Tree.CompilationUnit getCompilationUnit(PhasedUnit pu) => pu.compilationUnit;
    shared actual String inexactMatches => "positional";

    shared actual String getDocumentSubstring(Document doc, Integer start, Integer length)
        => doc.getText(TextRange.from(start, length));
    
    shared actual LookupElement newPositionalInvocationCompletion(Integer offset, String prefix,
        Declaration dec, Reference? pr, Scope scope, PhasedUnit pu, Boolean isMember,
        OccurrenceLocation? ol, String? typeArgs, Boolean includeDefaulted) {

        return MyLookupElementBuilder(dec, dec.unit, true).lookupElement;
    }

    shared actual LookupElement newNamedInvocationCompletion(Integer offset, String prefix,
        Declaration dec, Reference? pr, Scope scope, PhasedUnit pu, Boolean isMember,
        OccurrenceLocation? ol, String? typeArgs, Boolean includeDefaulted) {
        
        print("newNamedInvocationCompletion");
        return nothing; // TODO
    }
    
    shared actual LookupElement newReferenceCompletion(Integer offset, String prefix,
        Declaration dec, Unit u, Reference? pr, Scope scope, PhasedUnit pu,
        Boolean isMember, Boolean includeTypeArgs) {
        
        return MyLookupElementBuilder(dec, dec.unit, false).lookupElement;
    }

    shared actual LookupElement newRefinementCompletionProposal(Integer offset, String prefix,
        Declaration dec, Reference? pr, Scope scope, PhasedUnit pu, Boolean isInterface,
        ClassOrInterface ci, Node node, Unit unit, Document doc, Boolean preamble) {
        
        print("newRefinementCompletionProposal");
        return nothing; // TODO
    }
    
    shared actual LookupElement newMemberNameCompletionProposal(Integer offset, String prefix, String name, String unquotedName) {
        return LookupElementBuilder.create(unquotedName, name);
    }
    
    shared actual LookupElement newKeywordCompletionProposal(Integer offset, String prefix, String keyword) {
        print("keyword ``keyword``");
        return LookupElementBuilder.create(keyword);
    }

    shared actual LookupElement newAnonFunctionProposal(Integer offset, Type? requiredType,
            Unit unit, String text, String header, Boolean isVoid) {
        
        print("newAnonFunctionProposal");
        return nothing; // TODO
    }

    shared actual JList<CommonToken> getTokens(PhasedUnit pu) {
        return pu.tokens;
    }
    
    shared actual LookupElement newNamedArgumentProposal(Integer offset, String prefix, 
        PhasedUnit pu, Tree.CompilationUnit unit, Declaration dec, Scope scope) {
        print("newNamedArgumentProposal");
        return nothing; // TODO
    }

    shared actual LookupElement newInlineFunctionProposal(Integer offset, FunctionOrValue dec,
        Scope scope, Node node, String prefix, PhasedUnit pu, Document doc) {
        print("newInlineFunctionProposal");
        return nothing;
    }
    
    shared actual LookupElement newProgramElementReferenceCompletion(Integer offset, String prefix,
        Declaration dec, Unit? u, Reference? pr, Scope scope, PhasedUnit pu, Boolean isMember) {
        print("newProgramElementReferenceCompletion");
        return nothing;
    }

    shared actual LookupElement newBasicCompletionProposal(Integer offset, String prefix, 
        String text, String escapedText, Declaration decl, PhasedUnit pu) {
        print("newBasicCompletionProposal");
        return nothing;
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
                typeText = if (fun.declaredVoid) then "void" else fun.typeDeclaration.name;
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
