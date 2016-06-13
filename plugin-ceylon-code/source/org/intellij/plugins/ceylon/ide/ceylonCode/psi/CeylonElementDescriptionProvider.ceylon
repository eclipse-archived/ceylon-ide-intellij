import com.intellij.psi {
    ElementDescriptionLocation,
    ElementDescriptionProvider,
    PsiElement
}
import com.intellij.usageView {
    UsageViewLongNameLocation,
    UsageViewShortNameLocation
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Function,
    ModelUtil,
    Declaration,
    Type,
    ParameterList,
    Class,
    Functional,
    Unit,
    Interface,
    Value,
    TypeAlias,
    Constructor,
    ClassOrInterface,
    TypedDeclaration
}

shared class CeylonElementDescriptionProvider() satisfies ElementDescriptionProvider {
    
    shared actual String? getElementDescription(PsiElement element,
        ElementDescriptionLocation location) 
            => if (is CeylonCompositeElement element)
            then (switch (location)
                  case (is UsageViewLongNameLocation) 
                    descriptions.descriptionForPsi(element)
                  case (is UsageViewShortNameLocation) 
                    element.name
                  else null) 
            else null;
}

shared object descriptions {

    shared String descriptionForDeclaration(Declaration decl,
            Boolean includeKeyword = true,
            Boolean includeContainer = true,
            Boolean includeReturnType = true,
            Unit unit = decl.unit) {
        value result = StringBuilder();
        if (includeKeyword) {
            result.append(keyword(decl)).append(" ");
        }
        if (includeContainer) {
            result.append(container(decl));
        }
        result.append(decl.name else "new")
                .append(parameterLists(decl, unit));
        if (includeReturnType,
            is TypedDeclaration decl,
            !ModelUtil.isConstructor(decl)) {
            if (is Function decl, decl.declaredVoid) {
                //noop for void
            }
            else if (exists returnType = decl.type,
                    !ModelUtil.isTypeUnknown(returnType)) {
                result.append(" ∊ ")
                    .append(returnType.asString(unit));
            }
        }
        return result.string;
    }
    
    shared String descriptionForNode(Tree.Declaration node, Boolean includeKeyword = true) {
        value result = StringBuilder();
        if (includeKeyword) {
            result.append(nodeKeyword(node)).append(" ");
        }
        result.append(node.identifier?.text else "new");
        switch (node)
        case (is Tree.AnyClass) {
            if (exists pl = node.parameterList) {
                appendTreeParameters(result, pl);
            }
        }
        case (is Tree.AnyMethod) {
            for (pl in node.parameterLists) {
                appendTreeParameters(result, pl);
            }
        }
        case (is Tree.Constructor) {
            if (exists pl = node.parameterList) {
                appendTreeParameters(result, pl);
            }
        }
        else {}
        return result.string;
    }

    shared String? descriptionForPsi(CeylonCompositeElement element,
        Boolean includeKeyword = true,
        Boolean includeContainer = true,
        Boolean includeReturnType = true) {
        value node = element.ceylonNode;
        value decl =
            switch (node)
            case (is Tree.Declaration) node.declarationModel
            case (is Tree.SpecifierStatement) (node.refinement then node.declaration)
            else null;

        if (exists decl) {
            return descriptionForDeclaration {
                decl = decl;
                includeKeyword = includeKeyword;
                includeContainer = includeContainer;
                includeReturnType = includeReturnType;
                unit = node.unit else decl.unit;
            };
        }
        else if (is Tree.Declaration node) {
            return descriptionForNode(node, includeKeyword);
        }
        else {
            return null;
        }
    }
    
    String container(Declaration decl)
            => if (is ClassOrInterface container = decl.container)
            then container.name + "." else "";
    
    String keyword(Declaration declaration) {
        if (ModelUtil.isConstructor(declaration)) {
            return "new";
        }
        return switch (declaration)
            case (is Interface) "interface"
            case (is Class) (declaration.objectClass then "object" else "class")
            case (is Value) (ModelUtil.isObject(declaration) then "object" else "value")
            case (is Function) (declaration.declaredVoid then "void" else "function")
            case (is TypeAlias) "alias"
            case (is Constructor) "new"
            else "";
    }

    String nodeKeyword(Tree.Declaration declaration)
            => switch (declaration)
            case (is Tree.ObjectDefinition) "object"
            case (is Tree.AnyClass) "class"
            case (is Tree.AnyInterface) "interface"
            case (is Tree.AnyAttribute) "value"
            case (is Tree.TypeAliasDeclaration) "alias"
            case (is Tree.Constructor|Tree.Enumerated) "new"
            case (is Tree.AnyMethod) (declaration.type is Tree.VoidModifier then "void" else "function")
            else "";

    String parameterLists(Declaration decl, Unit unit) {
        if (!is Functional decl) {
            return "";
        }
        value builder = StringBuilder();
        for (paramList in decl.parameterLists) {
            appendParameters(builder, paramList, unit);
        }
        return builder.string;
    }

    void appendParameters(StringBuilder builder, ParameterList paramList, Unit unit) {
        builder.append("(");
        variable value first = true;
        for (param in paramList.parameters) {
            if (first) {
                first = false;
            }
            else {
                builder.append(", ");
            }
            Type? type = param.type;
            if (ModelUtil.isTypeUnknown(type)) {
                //builder.append("unknown");
            }
            else if (param.sequenced) {
                builder.append("*")
                        .append(unit.getSequentialElementType(type).asString(unit))
                        .append(" ");
            }
            else {
                builder.append(param.type.asString(unit))
                        .append(" ");
            }
            if (exists name = param.name) {
                builder.append(name);
            }
            if (exists model = param.model) {
                builder.append(parameterLists(model, unit));
            }
        }
        builder.append(")");
    }

    void appendTreeParameters(StringBuilder builder, Tree.ParameterList paramList) {
        builder.append("(");
        variable value first = true;
        for (param in paramList.parameters) {
            if (first) {
                first = false;
            }
            else {
                builder.append(", ");
            }
            switch (param)
            case (is Tree.InitializerParameter) {
                builder.append(param.identifier.text);
            }
            case (is Tree.ParameterDeclaration) {
                if (exists id = param.typedDeclaration.identifier) {
                    builder.append(id.text);
                }
                if (is Tree.AnyMethod dec = param.typedDeclaration) {
                    for (pl in dec.parameterLists) {
                        appendTreeParameters(builder, pl);
                    }
                }
            }
            //TODO: pattern parameters?
            else {}
        }
        builder.append(")");
    }
}