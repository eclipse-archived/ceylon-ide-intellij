import ceylon.interop.java {
    createJavaObjectArray,
    javaClass,
    javaString
}

import com.intellij.codeInsight.lookup {
    LookupElement
}
import com.intellij.lang.parameterInfo {
    ParameterInfoHandler,
    ParameterInfoContext,
    ParameterInfoUIContext,
    CreateParameterInfoContext,
    UpdateParameterInfoContext,
    ParameterInfoUtils,
    ParameterInfoUIContextEx
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Functional,
    Unit,
    Parameter,
    Function,
    Declaration
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    TokenTypes,
    CeylonPsi {
        ArgumentListPsi
    },
    CeylonTreeUtil
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.ide.common.correct {
    FindInvocationVisitor
}
import com.intellij.psi.util {
    PsiUtilCore,
    PsiTreeUtil
}
import com.redhat.ceylon.model.typechecker.util {
    TypePrinter
}

import com.intellij.util {
    IJFunction=Function
}
import java.lang {
    JString=String
}
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}

shared class CeylonParameterInfoHandler() satisfies ParameterInfoHandler<ArgumentListPsi, Functional> {
    
    value printer = TypePrinter(true, true, false, true, false);
    value htmlize = object satisfies IJFunction<JString, JString> {
        fun(JString s) => javaString(s.string.replace("≤", "<").replace("≥", ">"));
    };
    
    couldShowInLookup() => true;
    
    shared actual ArgumentListPsi? findElementForParameterInfo(CreateParameterInfoContext context) {
        if (is CeylonFile file = context.file,
            exists node = nodes.findNode(file.compilationUnit, file.tokens, context.offset)) {
            
            value fiv = FindInvocationVisitor(node);
            fiv.visit(file.compilationUnit);
            
            if (exists expr = fiv.result,
                exists argList = expr.positionalArgumentList,
                is Tree.MemberOrTypeExpression mot = expr.primary,
                is Functional declaration = mot.declaration) {
                
                context.itemsToShow = createJavaObjectArray({declaration});
                
                if (is ArgumentListPsi psi = CeylonTreeUtil.findPsiElement(argList, file)) {
                    return psi;
                }
            }
        }
        
        return null;
    }
    
    shared actual ArgumentListPsi? findElementForUpdatingParameterInfo(UpdateParameterInfoContext context) {
        value elementAtOffset = PsiUtilCore.getElementAtOffset(context.file, context.offset);

        return PsiTreeUtil.getParentOfType(elementAtOffset, javaClass<ArgumentListPsi>());
    }
    
    getParametersForDocumentation(Functional? p, ParameterInfoContext? context)
            => createJavaObjectArray(empty);
    
    getParametersForLookup(LookupElement? item, ParameterInfoContext? context)
            => createJavaObjectArray(empty);
    
    parameterCloseChars => ParameterInfoUtils.defaultParameterCloseChars;
    
    shared actual void showParameterInfo(ArgumentListPsi pal,
        CreateParameterInfoContext context) {

        context.showHint(pal, pal.textRange.startOffset, this);
    }
    
    tracksParameterIndex() => true;
    
    shared actual void updateParameterInfo(ArgumentListPsi pal,
        UpdateParameterInfoContext context) {

        value index = ParameterInfoUtils.getCurrentParameterIndex(pal.node, context.offset,
            TokenTypes.comma.tokenType);
        context.setCurrentParameter(index);
    }

    shared actual void updateUI(Functional fun, ParameterInfoUIContext context) {
        variable String params = "<no parameters>";
        StringBuilder builder = StringBuilder();
        variable Integer highlightOffsetStart = - 1;
        variable Integer highlightOffsetEnd = - 1;
        variable Integer i = 0;
        
        if (is Declaration fun,
            exists parameters = fun.firstParameterList?.parameters,
            parameters.size()>0) {
            
            for (param in parameters) {
                variable String paramLabel = getParameterLabel(param, (fun of Declaration).unit);
                paramLabel = highlighter.highlight(paramLabel, context.parameterOwner.project)
                    .replace("<", "≤")
                    .replace(">", "≥")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("->", "→");

                if (i == context.currentParameterIndex) {
                    highlightOffsetStart = builder.size;
                    highlightOffsetEnd = builder.size +paramLabel.size;
                }
                builder.append(paramLabel).append(", ");
                i ++;
            }
            builder.deleteTerminal(2);
            params = builder.string;
        }
        if (is ParameterInfoUIContextEx context) {
            context.setEscapeFunction(htmlize);
        }
        context.setupUIComponentPresentation(params, highlightOffsetStart, highlightOffsetEnd,
            false, false, false, context.defaultParameterColor);
    }

    String getParameterLabel(Parameter param, Unit unit) {
        value builder = StringBuilder()
            .append(printer.print(param.type, unit))
            .append(" ").append(param.name);
        
        if (is Function model = param.model) {
            builder.append("(");
            for (Parameter parameter in model.firstParameterList.parameters) {
                builder.append(getParameterLabel(parameter, unit));
                builder.append(", ");
            }
            builder.deleteTerminal(2);
            builder.append(")");
        }
        return builder.string;
    }
}