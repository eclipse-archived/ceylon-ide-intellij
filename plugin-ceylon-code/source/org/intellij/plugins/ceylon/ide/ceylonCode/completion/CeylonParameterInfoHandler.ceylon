import ceylon.interop.java {
    javaClass,
    javaString,
    CeylonIterable,
    CeylonList
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
    JString=String,
    ObjectArray
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

shared class CeylonParameterInfoHandler()
        satisfies ParameterInfoHandler<ArgumentListPsi, Functional> {
    
    value printer = TypePrinter(true, true, false, true, false);

    value argumentListClass = javaClass<CeylonPsi.ArgumentListPsi>();
    value invocationExpressionClass = javaClass<CeylonPsi.InvocationExpressionPsi>();

    couldShowInLookup() => true;
    
    shared actual ArgumentListPsi? findElementForParameterInfo(CreateParameterInfoContext context) {
        if (is CeylonFile file = context.file) {

            value elementAtOffset
                    = PsiUtilCore.getElementAtOffset(context.file,
                        context.editor.caretModel.offset);

            if (exists args = getParentOfType(elementAtOffset, argumentListClass),
                exists invocation = getParentOfType(args, invocationExpressionClass),
                exists node = invocation.ceylonNode,
                exists argList
                        = node.positionalArgumentList
                        else node.namedArgumentList,
                is Tree.MemberOrTypeExpression mot = node.primary,
                is Functional declaration = mot.declaration) {
                
                context.itemsToShow = ObjectArray<Object>(1, declaration);

                return args;
            }
        }
        
        return null;
    }
    
    shared actual ArgumentListPsi? findElementForUpdatingParameterInfo(UpdateParameterInfoContext context) {
        value elementAtOffset = PsiUtilCore.getElementAtOffset(context.file, context.offset);
        return getParentOfType(elementAtOffset, javaClass<ArgumentListPsi>());
    }
    
    getParametersForDocumentation(Functional? p, ParameterInfoContext? context)
            => ObjectArray<Object>(0);
    
    getParametersForLookup(LookupElement? item, ParameterInfoContext? context)
            => ObjectArray<Object>(0);
    
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
            exists model = al.ceylonNode?.namedArgumentList) {

            if (inSequencedArgs(al, model, offset)) {
                context.setCurrentParameter(model.parameterList.parameters.size()-1);
            } else {
                value arg
                        = CeylonIterable(al.ceylonNode.namedArguments)
                            .find((a) => a.startIndex.intValue()-1 <= offset <= a.endIndex.intValue()+1);

                if (exists arg) {
                    value index
                            = CeylonList(model.parameterList.parameters)
                                .firstIndexWhere((param) => param.name == arg.identifier.text);

                    if (exists index) {
                        context.setCurrentParameter(index);
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
        value noparams = "no parameters";
        variable String params = noparams;
        StringBuilder builder = StringBuilder();
        variable Integer highlightOffsetStart = - 1;
        variable Integer highlightOffsetEnd = - 1;
        variable Integer i = 0;
        
        if (is Declaration fun,
            exists parameters = fun.firstParameterList?.parameters,
            parameters.size()>0) {

            value unit = fun.unit;
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
                        => let (unescaped = convertFromHTML(string.string)) //undo the escaping that ParameterInfoUIContext does by default
                        if (unescaped == noparams) then string //don't highlight 'no parameters' text
                        else javaString(highlighter.highlight(unescaped, context.parameterOwner.project));
            });
        }
        context.setupUIComponentPresentation(params, highlightOffsetStart, highlightOffsetEnd,
            false, false, false, context.defaultParameterColor);
    }

    String convertFromHTML(String content)
            => content.replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&lt;", "<")
            .replace("&gt;", ">");

    String getParameterLabel(Parameter param, Unit unit) {
        value builder = StringBuilder();
        if (param.sequenced) {
            value type = unit.getSequentialElementType(param.type);
            builder.append(printer.print(type, unit)).append("*");
        }
        else {
            builder.append(printer.print(param.type, unit));
        }
        builder.append(" ").append(param.name);
        if (param.defaulted) {
            builder.append(" = ...");
        }
        
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