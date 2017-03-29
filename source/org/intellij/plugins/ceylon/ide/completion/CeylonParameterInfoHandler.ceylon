import ceylon.interop.java {
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
import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiUtilCore {
        getElementAtOffset
    },
    PsiTreeUtil {
        getParentOfType
    }
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
    ModelUtil,
    Reference
}
import com.redhat.ceylon.model.typechecker.util {
    TypePrinter
}

import java.lang {
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile,
    TokenTypes,
    CeylonPsi
}

shared class CeylonParameterInfoHandler()
        satisfies ParameterInfoHandler<CeylonPsi.ArgumentListPsi, Functional&Declaration> {
    
    value printer = TypePrinter(true, true, false, true, false);

    function findArgumentList(ParameterInfoContext context)
            => getParentOfType(getElementAtOffset(context.file, context.offset), `CeylonPsi.ArgumentListPsi`);

    couldShowInLookup() => true;

    function overloadedVersions(Declaration&Functional declaration) {
        if (ModelUtil.isAbstraction(declaration)) {
            return Array<Object?> { *declaration.overloads };
        }
        else if (ModelUtil.isOverloadedVersion(declaration)) {
            value abstraction
                    = declaration.scope.getDirectMember(declaration.name, null, false);
            return Array<Object?> { *abstraction.overloads };
        }
        else {
            return Array<Object?> { declaration };
        }
    }

    shared actual CeylonPsi.ArgumentListPsi?
    findElementForParameterInfo(CreateParameterInfoContext context) {
        if (is CeylonFile file = context.file,
            exists args = findArgumentList(context),
            exists invocation = getParentOfType(args, `CeylonPsi.InvocationExpressionPsi`),
            exists node = invocation.ceylonNode,
            exists argList
                    = node.positionalArgumentList
                    else node.namedArgumentList,
            is Tree.MemberOrTypeExpression mot = node.primary,
            is Functional declaration = mot.declaration) {

            value overloads = overloadedVersions(declaration);

            function weight(Anything x)
                    => if (is Functional x, exists pl = x.firstParameterList)
                    then (x==declaration then -1 else pl.parameters.size())
                    else 0;

            overloads.sortInPlace(byIncreasing(weight));
            context.itemsToShow = javaObjectArray(overloads);

            return args;
        }
        
        return null;
    }
    
    findElementForUpdatingParameterInfo(UpdateParameterInfoContext context)
            => findArgumentList(context);
    
    getParametersForDocumentation(Functional&Declaration? fun, ParameterInfoContext? context)
            => ObjectArray<Object>(0);
    
    getParametersForLookup(LookupElement? item, ParameterInfoContext? context)
            => ObjectArray<Object>(0);
    
    parameterCloseChars => ParameterInfoUtils.defaultParameterCloseChars;
    
    showParameterInfo(CeylonPsi.ArgumentListPsi pal, CreateParameterInfoContext context)
            => context.showHint(pal, pal.textRange.startOffset, this);
    
    tracksParameterIndex() => true;
    
    shared actual void updateParameterInfo(CeylonPsi.ArgumentListPsi al,
        UpdateParameterInfoContext context) {

        value offset = context.offset;

        switch (al)
        case (is CeylonPsi.PositionalArgumentListPsi) {
            value index
                    = ParameterInfoUtils.getCurrentParameterIndex(al.node,
                        offset, TokenTypes.comma.tokenType);
            context.setCurrentParameter(index);
        }
        else case (is CeylonPsi.NamedArgumentListPsi) {
            if (exists node = al.ceylonNode,
                exists model = node.namedArgumentList,
                exists parameterList = model.parameterList) {

                if (inSequencedArgs(al, model, offset)) {
                    context.setCurrentParameter(parameterList.parameters.size() - 1);
                } else {
                    for (arg in node.namedArguments) {
                        if (arg.startIndex.intValue() - 1<=offset<=arg.endIndex.intValue() + 1) {
                            value params = parameterList.parameters;
                            for (index in 0:params.size()) {
                                if (exists param = params[index],
                                    param.name == arg.identifier.text) {
                                    context.setCurrentParameter(index);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        else {}
    }

    Boolean inSequencedArgs(CeylonPsi.NamedArgumentListPsi nal,
                NamedArgumentList model, Integer offset)
            => if (exists node = nal.ceylonNode,
                   exists seq = node.sequencedArgument)
            then node.namedArguments.empty
              || seq.startIndex.intValue()-1 <= offset <= seq.stopIndex.intValue()+1
            else false;

    function getReference(PsiElement psi) {
        if (is CeylonPsi.InvocationExpressionPsi ie
                = psi.parent,
            is Tree.MemberOrTypeExpression mte
                = ie.ceylonNode?.primary) {
            return mte.target;
        }
        else {
            return null;
        }
    }

    shared actual void updateUI(Functional&Declaration fun, ParameterInfoUIContext context) {
        value noparams = "'no parameters'";

        value ref = getReference(context.parameterOwner);

        value builder = StringBuilder();

        variable Integer highlightOffsetStart = -1;
        variable Integer highlightOffsetEnd = -1;
        variable Integer i = 0;
        if (!ModelUtil.isAbstraction(fun),
            exists parameterList = fun.firstParameterList) {
            value parameters = parameterList.parameters;
            value unit = fun.unit;
            if (parameters.empty) {
                builder.append(noparams);
            }
            else {
                for (param in parameters) {
                    value paramLabel = getParameterLabel(param, unit, ref);
                    if (i == context.currentParameterIndex) {
                        highlightOffsetStart = builder.size;
                        highlightOffsetEnd = builder.size + paramLabel.size;
                    }
                    builder.append(paramLabel)
                           .append(", ");
                    i ++;
                }
                builder.deleteTerminal(2);
            }
        }

        if (is ParameterInfoUIContextEx context) {
            context.setEscapeFunction((string)
                    => highlighter.highlight {
                        rawText = convertFromHTML(string).replace(noparams, "$$");
                        project = context.parameterOwner.project;
                    }
                    .replace("$$", "<i>no parameters</i>"));
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

    String getParameterLabel(Parameter param, Unit unit, Reference? ref) {
        value builder = StringBuilder();
        if (exists type = param.type) {
            value paramType
                    = if (exists ref)
                    then ref.getTypedParameter(param).type
                    else type;
            if (param.sequenced) {
                value elementType = unit.getSequentialElementType(paramType);
                builder.append(printer.print(elementType, unit))
                       .append("*");
            } else {
                builder.append(printer.print(paramType, unit));
            }
            builder.append(" ");
        }
        builder.append(param.name);

        if (is Function model = param.model,
            exists parameterList = model.firstParameterList) {
            builder.append("(");
            for (parameter in parameterList.parameters) {
                if (!builder.endsWith("(")) {
                    builder.append(", ");
                }
                builder.append(getParameterLabel(parameter, unit, ref));
            }
            builder.append(")");
        }

        if (param.defaulted) {
            builder.append(" = ...");
        }

        return builder.string;
    }
}