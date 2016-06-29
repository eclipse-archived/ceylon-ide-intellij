import ceylon.interop.java {
    createJavaObjectArray,
    javaClass,
    javaString,
    CeylonIterable
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
import com.intellij.psi.util {
    PsiUtilCore,
    PsiTreeUtil {
        getParentOfType
    }
}
import com.intellij.util {
    IJFunction=Function
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.doc {
    convertToHTML
}
import com.redhat.ceylon.model.typechecker.model {
    Functional,
    Unit,
    Parameter,
    Function,
    Declaration,
    NamedArgumentList
}
import com.redhat.ceylon.model.typechecker.util {
    TypePrinter
}

import java.lang {
    JString=String
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    TokenTypes,
    CeylonPsi {
        ArgumentListPsi,
        PositionalArgumentListPsi,
        NamedArgumentListPsi
    }
}

shared class CeylonParameterInfoHandler() satisfies ParameterInfoHandler<ArgumentListPsi, Functional> {
    
    value printer = TypePrinter(true, true, false, true, false);

    couldShowInLookup() => true;
    
    shared actual ArgumentListPsi? findElementForParameterInfo(CreateParameterInfoContext context) {
        if (is CeylonFile file = context.file) {

            value elementAtOffset = PsiUtilCore.getElementAtOffset(context.file,
                context.editor.caretModel.offset);

            if (exists args = getParentOfType(elementAtOffset, javaClass<CeylonPsi.ArgumentListPsi>()),
                exists invocation = getParentOfType(args, javaClass<CeylonPsi.InvocationExpressionPsi>()),
                exists argList = invocation.ceylonNode.positionalArgumentList
                    else invocation.ceylonNode.namedArgumentList,
                is Tree.MemberOrTypeExpression mot = invocation.ceylonNode.primary,
                is Functional declaration = mot.declaration) {
                
                context.itemsToShow = createJavaObjectArray({declaration});

                return args;
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
    
    showParameterInfo(ArgumentListPsi pal,
        CreateParameterInfoContext context)
            => context.showHint(pal, pal.textRange.startOffset, this);
    
    tracksParameterIndex() => true;
    
    shared actual void updateParameterInfo(ArgumentListPsi al,
        UpdateParameterInfoContext context) {

        value offset = context.editor.caretModel.offset;

        if (is PositionalArgumentListPsi al) {
            value index
                    = ParameterInfoUtils.getCurrentParameterIndex(al.node, offset,
                        TokenTypes.comma.tokenType);
            context.setCurrentParameter(index);
        } else if (is NamedArgumentListPsi al,
            exists model = al.ceylonNode.namedArgumentList) {

            if (inSequencedArgs(al, model, offset)) {
                context.setCurrentParameter(model.parameterList.parameters.size()-1);
            } else {
                value arg
                        = CeylonIterable(al.ceylonNode.namedArguments)
                            .find((a) => a.startIndex.intValue()-1 <= offset <= a.endIndex.intValue()+1);

                if (exists arg) {
                    value index
                            = CeylonIterable(model.parameterList.parameters).indexed
                                .find((idx -> param) => param.name == arg.identifier.text);

                    if (exists index) {
                        context.setCurrentParameter(index.key);
                    }
                }
            }
        }
    }

    Boolean inSequencedArgs(NamedArgumentListPsi nal, NamedArgumentList model, Integer offset) {

        if (exists seq = nal.ceylonNode.sequencedArgument) {
            if (nal.ceylonNode.namedArguments.empty) {
                return true;
            }
            if (seq.startIndex.intValue() - 1 <= offset <= seq.stopIndex.intValue() + 1) {
                return true;
            }
        }

        return false;
    }

    shared actual void updateUI(Functional fun, ParameterInfoUIContext context) {
        value noparams = "<no parameters>";
        variable String params = noparams;
        StringBuilder builder = StringBuilder();
        variable Integer highlightOffsetStart = - 1;
        variable Integer highlightOffsetEnd = - 1;
        variable Integer i = 0;
        
        if (is Declaration fun,
            exists parameters = fun.firstParameterList?.parameters,
            parameters.size()>0) {

            value unit = (fun of Declaration).unit;
            for (param in parameters) {
                value paramLabel
                        = getParameterLabel(param, unit)
                            .replace("->", "→");

                if (i == context.currentParameterIndex) {
                    highlightOffsetStart = builder.size;
                    highlightOffsetEnd = builder.size + paramLabel.size;
                }
                builder.append(paramLabel).append(", ");
                i++;
            }
            builder.deleteTerminal(2);
            params = builder.string;
        }
        if (is ParameterInfoUIContextEx context) {
            context.setEscapeFunction(object satisfies IJFunction<JString, JString> {
                fun(JString string)
                        => string.string == convertToHTML(noparams) then string
                        else javaString(highlighter.highlight(string.string, context.parameterOwner.project));
            });
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
            for (parameter in model.firstParameterList.parameters) {
                builder.append(getParameterLabel(parameter, unit));
                builder.append(", ");
            }
            builder.deleteTerminal(2);
            builder.append(")");
        }
        return builder.string;
    }
}