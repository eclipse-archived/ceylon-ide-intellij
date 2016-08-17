import ceylon.interop.java {
    javaClass,
    javaString,
    CeylonIterable,
    CeylonList,
    javaObjectArray
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
    NamedArgumentList,
    ModelUtil
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
    CeylonPsi
}

shared class CeylonParameterInfoHandler()
        satisfies ParameterInfoHandler<CeylonPsi.ArgumentListPsi, Functional&Declaration> {
    
    value printer = TypePrinter(true, true, false, true, false);

    value argumentListClass = javaClass<CeylonPsi.ArgumentListPsi>();
    value invocationExpressionClass = javaClass<CeylonPsi.InvocationExpressionPsi>();

    couldShowInLookup() => true;

    shared actual CeylonPsi.ArgumentListPsi? findElementForParameterInfo(CreateParameterInfoContext context) {
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

                Array<Object?> overloads;
                if (ModelUtil.isAbstraction(declaration)) {
                    overloads = Array<Object?> { for (dec in declaration.overloads) dec };
                }
                else if (ModelUtil.isOverloadedVersion(declaration)) {
                    value abstraction
                            = declaration.scope.getDirectMember(declaration.name, null, false);
                    overloads = Array<Object?> { for (dec in abstraction.overloads) dec };
                }
                else {
                    overloads = Array<Object?> { declaration };
                }

                function weight(Anything x)
                        => if (is Functional x)
                        then (x==declaration
                                then -1
                                else x.firstParameterList?.parameters?.size() else 0)
                        else 100;

                overloads.sortInPlace(byIncreasing(weight));
                context.itemsToShow = javaObjectArray(overloads);

                return args;
            }
        }
        
        return null;
    }
    
    findElementForUpdatingParameterInfo(UpdateParameterInfoContext context)
            => getParentOfType(PsiUtilCore.getElementAtOffset(context.file, context.offset),
                    argumentListClass);
    
    getParametersForDocumentation(Functional&Declaration? fun, ParameterInfoContext? context)
            => ObjectArray<Object>(0);
    
    getParametersForLookup(LookupElement? item, ParameterInfoContext? context)
            => ObjectArray<Object>(0);
    
    parameterCloseChars => ParameterInfoUtils.defaultParameterCloseChars;
    
    showParameterInfo(CeylonPsi.ArgumentListPsi pal,
        CreateParameterInfoContext context)
            => context.showHint(pal, pal.textRange.startOffset, this);
    
    tracksParameterIndex() => true;
    
    shared actual void updateParameterInfo(CeylonPsi.ArgumentListPsi al,
        UpdateParameterInfoContext context) {

        value offset = context.editor.caretModel.offset;

        if (is CeylonPsi.PositionalArgumentListPsi al) {
            value index
                    = ParameterInfoUtils.getCurrentParameterIndex(al.node, offset,
                        TokenTypes.comma.tokenType);
            context.setCurrentParameter(index);
        }
        else if (is CeylonPsi.NamedArgumentListPsi al,
            exists node = al.ceylonNode,
            exists model = node.namedArgumentList) {

            if (inSequencedArgs(al, model, offset)) {
                context.setCurrentParameter(model.parameterList.parameters.size()-1);
            } else {
                value arg
                        = CeylonIterable(node.namedArguments)
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

    Boolean inSequencedArgs(CeylonPsi.NamedArgumentListPsi nal,
                NamedArgumentList model, Integer offset)
            => if (exists node = nal.ceylonNode,
                   exists seq = node.sequencedArgument)
            then node.namedArguments.empty
              || seq.startIndex.intValue()-1 <= offset <= seq.stopIndex.intValue()+1
            else false;

    shared actual void updateUI(Functional&Declaration fun, ParameterInfoUIContext context) {
        value noparams = "'no parameters'";

        value unit = fun.unit;
        value builder = StringBuilder();

        variable Integer highlightOffsetStart = -1;
        variable Integer highlightOffsetEnd = -1;
        variable Integer i = 0;
        if (!ModelUtil.isAbstraction(fun),
            exists parameters = fun.firstParameterList?.parameters) {
            if (parameters.empty) {
                builder.append(noparams);
            }
            else {
                for (param in parameters) {
                    value paramLabel = getParameterLabel(param, unit);
                    if (i == context.currentParameterIndex) {
                        highlightOffsetStart = builder.size;
                        highlightOffsetEnd = builder.size + paramLabel.size;
                    }
                    builder.append(paramLabel).append(", ");
                    i ++;
                }
                builder.deleteTerminal(2);
            }
        }

        if (is ParameterInfoUIContextEx context) {
            context.setEscapeFunction(object satisfies IJFunction<JString, JString> {
                fun(JString string)
                        => javaString(highlighter.highlight {
                            rawText = convertFromHTML(string.string).replace(noparams, "$$");
                            project = context.parameterOwner.project;
                        }
                        .replace("$$", "<i>no parameters</i>"));
            });
        }

        context.setupUIComponentPresentation(builder.string,
            highlightOffsetStart, highlightOffsetEnd,
            !context.uiComponentEnabled, fun.deprecated, false,
            context.defaultParameterColor);
    }

    String convertFromHTML(String content)
            => content.replace("&#10;", "\n")
            .replace("&amp;", "&")
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

        if (is Function model = param.model) {
            builder.append("(");
            for (parameter in model.firstParameterList.parameters) {
                builder.append(getParameterLabel(parameter, unit));
                builder.append(", ");
            }
            builder.deleteTerminal(2);
            builder.append(")");
        }

        if (param.defaulted) {
            builder.append(" = ...");
        }

        return builder.string;
    }
}